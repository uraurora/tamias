package com.sei.tamias.core.file

import com.google.common.collect.Iterables
import com.sei.tamias.core.listener.println
import org.junit.jupiter.api.Test

/**
 * @program : tamias
 * @author : sei
 * @date : 2020-11-13 15:19
 * @description :
 */
internal class ClassTest {

    @Test
    fun classTest(){
        val l = listOf(1, 2, 3)
        l.javaClass.typeName.println()

        val p = object : T<String>(){}

        p.javaClass.genericSuperclass.typeName.println()
    }

    @Test
    fun formatTest(){
        println(String.format("%s,i am your %s", *Iterables.toArray(listOf("tom", "father"), String::class.java)))
    }
}

open class T<E>{
    fun t(e: E){
        println(e)
    }
}