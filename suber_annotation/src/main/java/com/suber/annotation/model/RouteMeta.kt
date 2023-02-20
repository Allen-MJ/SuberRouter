package com.suber.annotation.model

import com.suber.annotation.enums.RouteType
import javax.lang.model.element.Element

data class RouteMeta(
    var routeType: RouteType, //类型（activity,fragment）
    var group: String, //路由分组
    var path: String, //路由路径
){
    var clazz: Class<*>? = null //类对象
    var element: Element? = null // 类节点信息
}