package com.sei.tamias.db.service.impl

import com.sei.tamias.TamiasApplication
import com.sei.tamias.db.entity.Tag
import com.sei.tamias.db.service.ITagService
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import javax.annotation.Resource

@RunWith(SpringJUnit4ClassRunner::class)
@SpringBootTest(classes = [TamiasApplication::class])
internal class TagServiceImplTest {
    @Resource
    lateinit var tagService: ITagService

    @Test fun test(){
        val tag = Tag()
        tag.name = "tom"
        tagService.save(tag)
    }

    @Test
    fun deleteTest(){
        tagService.removeById(1)
    }

    @Test
    fun updateTest(){
        tagService.lambdaUpdate()
                .eq({it.name}, "tom")
                .set({it.name}, "mary")
    }
}