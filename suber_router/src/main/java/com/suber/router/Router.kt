package com.suber.router

import android.app.Activity
import android.content.Intent
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import com.suber.router.inface.IRouterGroup
import com.suber.router.inface.IRouterPath
import com.suber.router.tools.CharsUtils
import java.io.Serializable

class Router private constructor(builder: Builder) {
    private val group: String?
    private val path: String?
    val intent: Intent?

    class Builder {
        var group: String? = null
        var path: String? = null
        var intent = Intent()

        fun build(mGroup: String,mPath: String): Builder{
            group = mGroup
            path = mPath
            return this
        }

        fun <T> putExtra(key: String, value: T): Builder{
            when(value){
                Int -> intent.putExtra(key, value as Int)
                String -> intent.putExtra(key, value as String)
                Long -> intent.putExtra(key, value as Long)
                is Serializable -> intent.putExtra(key, value as Serializable)
                Double -> intent.putExtra(key, value as Double)
                Float -> intent.putExtra(key, value as Float)
                is Parcelable -> intent.putExtra(key, value as Parcelable)
            }
            return this
        }
        fun build() = Router(this)
    }

    companion object{
        inline fun build(block: Builder.() -> Unit) = Builder().apply(block).build()
    }

    fun navition(): Class<*>? {
        val groupClazz = Class.forName("com.suber.route.generate.${group?.let {
            CharsUtils.upCaseKeyFirstChar(
                it
            )
        }}RouteGroup")
        val groupInstance = groupClazz.newInstance() as IRouterGroup
        // 通过组找到路径的map
        val pathInstance = groupInstance.getRouterPathByGroup()[group]!!.newInstance() as IRouterPath
        // 通过路径的map找到组对应的routeMeta
        val routeMeta = pathInstance.getRouteMetaByPath()[path]
        Log.d("debug",routeMeta?.routeType.toString())
        // 找到对应的class进行跳转
        return routeMeta?.clazz
    }

    fun navition(activity: Activity){
        val groupClazz = Class.forName("com.suber.route.generate.${group?.let {
            CharsUtils.upCaseKeyFirstChar(
                it
            )
        }}RouteGroup")
        val groupInstance = groupClazz.newInstance() as IRouterGroup
        // 通过组找到路径的map
        val pathInstance = groupInstance.getRouterPathByGroup()[group]!!.newInstance() as IRouterPath
        // 通过路径的map找到组对应的routeBean
        val routeMeta = pathInstance.getRouteMetaByPath()[path]
        Log.d("debug",routeMeta?.routeType.toString())
        // 找到对应的class进行跳转
        activity.startActivity(Intent(activity, routeMeta?.clazz))
    }

    fun navition(fragment: Fragment){
        val groupClazz = Class.forName("com.suber.route.generate.${group?.let {
            CharsUtils.upCaseKeyFirstChar(
                it
            )
        }}RouteGroup")
        val groupInstance = groupClazz.newInstance() as IRouterGroup
        // 通过组找到路径的map
        val pathInstance = groupInstance.getRouterPathByGroup()[group]!!.newInstance() as IRouterPath
        // 通过路径的map找到组对应的routeBean
        val routeMeta = pathInstance.getRouteMetaByPath()[path]
        Log.d("debug",routeMeta?.routeType.toString())
        // 找到对应的class进行跳转
        fragment.startActivity(Intent(fragment.context, routeMeta?.clazz))
    }

    fun navition(activity: Activity, requestCode: Int){
        val groupClazz = Class.forName("com.suber.route.generate.${group?.let {
            CharsUtils.upCaseKeyFirstChar(
                it
            )
        }}RouteGroup")
        val groupInstance = groupClazz.newInstance() as IRouterGroup
        // 通过组找到路径的map
        val pathInstance = groupInstance.getRouterPathByGroup()[group]!!.newInstance() as IRouterPath
        // 通过路径的map找到组对应的routeBean
        val routeMeta = pathInstance.getRouteMetaByPath()[path]
        Log.d("debug",routeMeta?.routeType.toString())
        // 找到对应的class进行跳转
        routeMeta?.clazz?.let { intent?.setClass(activity, it) }
        activity.startActivityForResult(intent,requestCode)
    }

    fun navition(fragment: Fragment, requestCode: Int){
        val groupClazz = Class.forName("com.suber.route.generate.${group?.let {
            CharsUtils.upCaseKeyFirstChar(
                it
            )
        }}RouteGroup")
        val groupInstance = groupClazz.newInstance() as IRouterGroup
        // 通过组找到路径的map
        val pathInstance = groupInstance.getRouterPathByGroup()[group]!!.newInstance() as IRouterPath
        // 通过路径的map找到组对应的routeBean
        val routeMeta = pathInstance.getRouteMetaByPath()[path]
        Log.d("debug",routeMeta?.routeType.toString())
        // 找到对应的class进行跳转
        routeMeta?.clazz?.let { fragment.context?.let { it1 -> intent?.setClass(it1, it) } }
        fragment.startActivityForResult(intent,requestCode)
    }

    fun startActivity(activity: Activity){
        activity.startActivity(intent)
    }

    init {
        group = builder.group
        path = builder.path
        intent = builder.intent
    }

}