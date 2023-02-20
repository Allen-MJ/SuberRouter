package com.suber.annotation.enums

/**
 * Fragment统一采用androidx.fragment.app.Fragment路径,取消android.app.Fragment路径
 */
enum class RouteType/*(var id: Int, var className: String)*/ {
    Activity/*(0, "android.app.Activity")*/, Fragment/*(1, "androidx.fragment.app.Fragment")*/, /*Fragment(
        2,
        "android.app.Fragment"
    ),*/
    Unknown/*(-1, "Unknown route type")*/;

    /*companion object {
        fun parse(name: String): RouteType {
            for (routeType in values()) {
                if (routeType.className == name) {
                    return routeType
                }
            }
            return Unknown
        }
    }*/

    /*override fun toString(): String {
        return "RouteType(id=$id, className='$className')"
    }*/

}