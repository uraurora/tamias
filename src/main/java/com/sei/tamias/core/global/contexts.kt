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

/**
 * 文件变更上下文数据类，包含文件变更类型，文件名，时间，和具体路径
 */
data class WatchEventContext<T>(var event: WatchEvent<T>?, var dir: Path?, var datetime: LocalDateTime = LocalDateTime.now())

