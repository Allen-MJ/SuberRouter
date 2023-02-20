package com.suber.router.complier.utils

object CharsUtils {

    fun upCaseKeyFirstChar(key: String): String {
        return if (Character.isUpperCase(key[0])) {
            key
        } else {
            StringBuilder().append(Character.toUpperCase(key[0])).append(key.substring(1)).toString()
        }
    }
}