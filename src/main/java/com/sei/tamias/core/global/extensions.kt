package com.sei.tamias.core.global

import com.sei.tamias.config.dateTimeFormatter
import com.sei.tamias.util.Options
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * 将map转成pair列表的形式方便排序筛选等操作
 */
fun <K, V> Map<K, V>.toPair(): List<Pair<K, V>>{
    return if(isEmpty()) emptyList() else {
        val res = mutableListOf<Pair<K, V>>()
        this.forEach { (k, v) ->
            res.add(k to v)
        }
        res
    }
}

fun nowDateTimeString(): String{
    return dateTimeFormatter.format(LocalDateTime.now(ZoneOffset.of("+8")))
}

fun <E> Collection<E>.limit(limit: Int): List<E> {
    return Options.limitedListOf(this, limit)
}

fun <E> List<E>.limitWithRemoveFirst(limit:Int): List<E>{
    return Options.limitedListWithRemoveFirstOf(this, limit)
}