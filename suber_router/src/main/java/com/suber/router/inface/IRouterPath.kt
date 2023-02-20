package com.suber.router.inface

import com.suber.annotation.model.RouteMeta

interface IRouterPath {
    fun getRouteMetaByPath() : LinkedHashMap<String, RouteMeta>
}