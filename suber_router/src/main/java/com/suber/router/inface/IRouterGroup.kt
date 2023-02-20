package com.suber.router.inface

interface IRouterGroup {
    fun getRouterPathByGroup() : LinkedHashMap<String,Class<out IRouterPath>>
}