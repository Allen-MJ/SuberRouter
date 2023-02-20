package com.suber.arouter.test

import com.suber.router.inface.IRouterGroup
import com.suber.router.inface.IRouterPath
import java.util.*

class AppRouterGroup : IRouterGroup {
    override fun getRouterPathByGroup(): LinkedHashMap<String, Class<out IRouterPath?>> {
        val map = LinkedHashMap<String, Class<out IRouterPath?>>()
        map["app"] = AppIRourePath::class.java
        return map
    }
}