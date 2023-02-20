package com.suber.router

import com.suber.annotation.model.RouteMeta
import com.suber.router.inface.IRouterPath

object CacheMap {
    val routerPathByGroup: LinkedHashMap<String,Class<out IRouterPath>> = LinkedHashMap()
    val routeMetaByPath: LinkedHashMap<String, RouteMeta> = LinkedHashMap()
}