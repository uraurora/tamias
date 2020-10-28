package com.sei.tamias.core.global

import com.sei.tamias.db.service.UserService
import com.sei.tamias.db.service.impl.UserServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
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
 * 上下文数据类
 */
data class tamiasContext(val datetime: LocalDateTime = LocalDateTime.now())

fun main() {
}