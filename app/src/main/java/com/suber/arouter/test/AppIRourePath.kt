package com.suber.arouter.test

import com.suber.annotation.enums.RouteType
import com.suber.annotation.model.RouteMeta
import com.suber.arouter.MainActivity
import com.suber.router.inface.IRouterPath
import java.util.*

class AppIRourePath : IRouterPath {
    override fun getRouteMetaByPath(): LinkedHashMap<String, RouteMeta> {
        val map = LinkedHashMap<String, RouteMeta>()

        map["/app/main"] = RouteMeta(RouteType.Activity, "app", "/app/main").apply {
            clazz = MainActivity::class.java
        }

        return map
    }
}