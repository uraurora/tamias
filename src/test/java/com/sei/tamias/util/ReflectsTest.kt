package com.sei.tamias.util

import com.sei.tamias.core.listener.println
import com.sei.tamias.util.Reflects.GenericsType
import net.bytebuddy.ByteBuddy
import net.bytebuddy.implementation.FixedValue
import net.bytebuddy.matcher.ElementMatchers
import org.joor.Reflect
import org.junit.Test

internal class ReflectsTest {

    @Test
    fun reflectTest(){
        Reflects.fieldsMap(TestNode(age = 12, name = "tom", scores = listOf(1, 2, 3, 4), set = setOf(1, 22, 2, 2, 6))).println()
    }

    @Test
    fun reflectTest2(){
        Reflects.fieldsSet(TestNode::class.java).println()
    }

    @Test
    fun createTest(){
        val world: String = Reflect.onClass("java.lang.String") // Like Class.forName()
                .create("Hello World") // Call most specific matching constructor
                .call("substring", 6) // Call most specific matching substring() method
                .call("toString") // Call toString()
                .get()
        world.println()
    }

    @Test
    fun genericTest(){
        val a: GenericsType<List<String>> = object : GenericsType<List<String>>() {}
        println(a.type)
    }

    @Test
    fun byteTest(){
        val dynamicType = ByteBuddy()
                .subclass(Object::class.java)
                        .method(ElementMatchers.named("toString"))
                        .intercept(FixedValue.value("Hello World"))
                        .make()
                        .load(HelloWorldBuddy::class.java.classLoader)
                        .loaded

        val instance = dynamicType.newInstance()
        val toString = instance.toString()
        println(toString)
        println(instance.getClass().canonicalName)
    }

    @Test
    fun proxyTest(){
        val list = Reflect.onClass(MutableList::class.java).create().get<Any>()
        println(list)
    }
}

data class TestNode(var age: Int,
                    var name: String,
                    var scores: List<Int>,
                    var set: Set<*>)

class HelloWorldBuddy