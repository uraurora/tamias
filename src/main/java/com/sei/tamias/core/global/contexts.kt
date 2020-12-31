package com.sei.tamias.core.global

import java.nio.file.Path
import java.nio.file.WatchEvent
import java.time.LocalDateTime

/**
 * app的全局属性参数存放，全局单例，使用INSTANCE来进行操作
 */
class AppContext: MutableMap<String, Any> by context.get(){

    companion object{
        private val context: ThreadLocal<MutableMap<String, Any>> = ThreadLocal.withInitial { mutableMapOf() }

        val INSTANCE:AppContext = AppContext()
    }
}
