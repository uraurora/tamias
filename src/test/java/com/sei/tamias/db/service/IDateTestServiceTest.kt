package com.sei.tamias.db.service

import com.sei.tamias.TamiasApplication
import com.sei.tamias.db.entity.DateTest
import com.sei.tamias.service.IDateTestService
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import javax.annotation.Resource

@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest(classes = [TamiasApplication::class])
internal class IDateTestServiceTest{

    @Resource
    lateinit var dateService: IDateTestService

    @Test
    fun insertTest(){
        for (i in 1..4) {
            dateService.save(DateTest())
        }

    }

    @Test
    fun queryTest(){
        dateService.list().forEach { println(it) }
    }


    @Test
    fun deleteTest(){
        dateService.removeByIds(listOf(8,9,10))
    }

    @Test
    fun updateTest(){
        val (id, time) = dateService.getById(11)
        println(id)
        println(time)
    }
}