package com.suber.router.complier.processor

import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.suber.annotation.Route
import com.suber.annotation.enums.RouteType
import com.suber.annotation.model.RouteMeta
import com.suber.router.complier.utils.CharsUtils
import com.suber.router.complier.utils.Const
import com.suber.router.complier.utils.javaToKotlinType
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic

@AutoService(Processor::class)
@SupportedAnnotationTypes(Const.RouterName)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(Const.Options)
class BaseProcessor : AbstractProcessor() {
    // 打印日志工具类
    private lateinit var mMessage: Messager
    // 文件操作类，我们将通过此类生成kotlin文件
    private lateinit var mFiler: Filer
    // 类型工具类，处理Element的类型
    private lateinit var types: Types
    private lateinit var elementUtils: Elements

    // Module name, maybe its 'app' or others
    private lateinit var moduleName: String

    // If need generate router doc
    var generateDoc = false

    private val mPathMap = LinkedHashMap<String, RouteMeta>()


    @Synchronized
    override fun init(processingEnvironment: ProcessingEnvironment) {
        super.init(processingEnvironment)
        mMessage = processingEnvironment.messager
        mFiler = processingEnvironment.filer
        types = processingEnvironment.typeUtils
        elementUtils = processingEnvironment.elementUtils
        // Attempt to get user configuration [moduleName]
        val options = processingEnvironment.options

        moduleName = options["moduleName"].toString()
        mMessage.printMessage(Diagnostic.Kind.NOTE, "processor 初始化完成.....${moduleName}")
        /*if (MapUtils.isNotEmpty(options)) {
            moduleName = options.get(KEY_MODULE_NAME);
            generateDoc = VALUE_ENABLE.equals(options.get(KEY_GENERATE_DOC_NAME));
        }

        if (StringUtils.isNotEmpty(moduleName)) {
            moduleName = moduleName.replaceAll("[^0-9a-zA-Z_]+", "");

            logger.info("The user has configuration the module name, it was [" + moduleName + "]");
        } else {
            logger.error(NO_MODULE_NAME_TIPS);
            throw new RuntimeException("ARouter::Compiler >>> No module name, for more information, look at gradle log.");
        }*/
    }

    override fun process(set: Set<TypeElement?>, roundEnvironment: RoundEnvironment): Boolean {
        if (set.isNullOrEmpty()) {
            mMessage.printMessage(Diagnostic.Kind.NOTE, "${moduleName}没有地方使用注解")
            return false
        }
        // 获取所有的被注解的节点
        val elements = roundEnvironment.getElementsAnnotatedWith(Route::class.java)

        /*elements.forEach {
            mMessage.printMessage(Diagnostic.Kind.NOTE, "module名称:${moduleName}")
            mMessage.printMessage(Diagnostic.Kind.NOTE, "类名：${it}")
        }*/
        // 获取activity的类型，转换成TypeMirror，用于判断
        val activityType = elementUtils.getTypeElement(Const.Activity).asType()
        // 获取fragment的类型，转换成TypeMirror，用于判断
        val fragmentType = elementUtils.getTypeElement(Const.Fragment).asType()

        elements.forEach {
            val className = it.simpleName.toString()
            mMessage.printMessage(Diagnostic.Kind.NOTE, "类名：${className}")
            // 获取注解的group,path变量
            val route = it.getAnnotation(Route::class.java)
            val path =  route.path
            mMessage.printMessage(Diagnostic.Kind.NOTE, "注解->path:${path}")
            // 严谨性，进行判空
            if (path.isEmpty()) {
                mMessage.printMessage(Diagnostic.Kind.NOTE, "${className}中path不能为空")
                throw RuntimeException("${className}中path不能为空")
            }

            // 严谨性，进行判空
            if(moduleName.isNullOrEmpty()) {
                mMessage.printMessage(Diagnostic.Kind.NOTE,
                    """
                        |请在build.gradle中进行配置
                        |kapt {
                        |    arguments {
                        |        arg("moduleName", project.getName())
                        |     }
                        |}
                    """.trimMargin())
            }

            // 生成RouteBean
            val meta = RouteMeta(
                routeType = when{
                    types.isSubtype(it.asType(),
                        activityType) -> RouteType.Activity
                    types.isSubtype(it.asType(),
                        fragmentType) -> RouteType.Fragment
                    else -> RouteType.Unknown
                },moduleName,path).apply {
                this.element = it
            }
            mPathMap[meta.path] = meta

        }
//        // 打印map
//        mMessage.printMessage(Diagnostic.Kind.NOTE, "$mPathMap")
        generatedPathFile(mPathMap)
        generatedGroupFile()
        return true
    }

    protected fun generatedPathFile(mPathMap: LinkedHashMap<String,RouteMeta>){

//        class AppIRourePath : IRouterPath {
//            override fun getRouteMetaByPath(): LinkedHashMap<String, RouteMeta> {
//                val map = LinkedHashMap<String, RouteMeta>()
//                val meta = RouteMeta(RouteType.Activity, "app", "/app/main")
//                meta.clazz = MainActivity::class.java
//                map["/app/main"] = meta
//                return map
//            }
//        }

        if(mPathMap.isNullOrEmpty()){
            mMessage.printMessage(Diagnostic.Kind.NOTE, "${moduleName}没有地方使用注解")
            return
        }

        // 方法返回类型，泛型为String，RouteBean
        val returnType = LinkedHashMap::class.java.asClassName().parameterizedBy(
            String::class.java.asTypeName().javaToKotlinType(),
            RouteMeta::class.asTypeName().javaToKotlinType()
        ).javaToKotlinType()

        // 创建方法，方法名为 getPath
        val funcSpecBuilder = FunSpec.builder(Const.RouterPathMethod)
            // override关键字
            .addModifiers(KModifier.OVERRIDE)
            // 返回map
            .returns(returnType)
            .addStatement(
                "val %N = LinkedHashMap<%T, %T>()",
                Const.MapName,
                String::class.java.asTypeName().javaToKotlinType(),
                RouteMeta::class.java
            )
        mPathMap.forEach{
            val key = it.key
            val meta = it.value
            funcSpecBuilder.addStatement(
                """
                    |%N[%S] = %T(%T.%L, %S, %S).apply { 
                    |   clazz = %T::class.java
                    |}
                    |
                """.trimMargin(),
                Const.MapName,
                key,
                RouteMeta::class.java,
                RouteType::class.java,
                meta.routeType,
                meta.group,
                meta.path,
                meta.element!!.asType().asTypeName(),
            )
        }
        funcSpecBuilder.addStatement("return %N", Const.MapName)
        // --------------------------- 类创建开始 --------------------------- //
        val superInter = ClassName(Const.IRouterPackage, "IRouterPath")
        val fileName = CharsUtils.upCaseKeyFirstChar("${moduleName}RouterPath")
        val typeSpec = TypeSpec.classBuilder(fileName)
            // 类中添加方法
            .addFunction(funcSpecBuilder.build())
            // 实现IRouterPath
            .addSuperinterface(superInter)
            .build()
        // 创建文件
        FileSpec.builder(Const.GenerateRouterPackage, fileName)
            .addType(typeSpec)
            .build()
            // 写入文件
            .writeTo(mFiler)
    }


    fun generatedGroupFile(){
        /*override fun getRouterPathByGroup(): LinkedHashMap<String, Class<out IRouterPath?>> {
            val map = LinkedHashMap<String, Class<out IRouterPath?>>()
            map["app"] = AppIRourePath::class.java
            return map
        }*/
// 方法返回类型，泛型为String，RouteBean

        val routePathInter = ClassName(Const.IRouterPackage, "IRouterPath")

        val returnType = LinkedHashMap::class.java.asClassName().parameterizedBy(
            String::class.java.asTypeName().javaToKotlinType(),
            Class::class.java.asClassName().parameterizedBy(
                WildcardTypeName.producerOf(routePathInter)
            )
        ).javaToKotlinType()

        // path对应的类名
        val putClazz = ClassName(Const.GenerateRouterPackage, CharsUtils.upCaseKeyFirstChar("${moduleName}RouterPath"))

        val funSpec = FunSpec.builder(Const.RouterGroupMethod)
            .returns(returnType)
            .addModifiers(KModifier.OVERRIDE)
            .addStatement(
                "val %N = LinkedHashMap<%T, %T>()",
                Const.MapName,
                String::class.java.asTypeName().javaToKotlinType(),
                Class::class.java.asClassName().parameterizedBy(
                    WildcardTypeName.producerOf(routePathInter)
                )
            )
            .addStatement(
                "%N[%S] = %T::class.java",
                Const.MapName,
                moduleName,
                putClazz
            )
            .addStatement("return %N", Const.MapName)
            .build()

        val superInter = ClassName(Const.IRouterPackage, "IRouterGroup")
        val fileName = CharsUtils.upCaseKeyFirstChar("${moduleName}RouteGroup")

        val typeSpec = TypeSpec.classBuilder(fileName)
            .addSuperinterface(superInter)
            .addFunction(funSpec)
            .build()

        FileSpec.builder(Const.GenerateRouterPackage, fileName)
            .addType(typeSpec)
            .build()
            .writeTo(mFiler)
    }

    /*private fun upCaseKeyFirstChar(key: String): String {
        return if (Character.isUpperCase(key[0])) {
            key
        } else {
            StringBuilder().append(Character.toUpperCase(key[0])).append(key.substring(1)).toString()
        }
    }*/
}