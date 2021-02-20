package com.sei.tamias.db.service.impl

import com.sei.tamias.TamiasApplication
import com.sei.tamias.core.file.println
import com.sei.tamias.core.file.shouldBe
import com.sei.tamias.db.entity.FileInfo
import com.sei.tamias.db.entity.FileTag
import com.sei.tamias.service.IFileInfoService
import com.sei.tamias.service.IFileTagService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import javax.annotation.Resource

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [TamiasApplication::class])
internal class FileTagServiceImplTest {

    @Resource
    lateinit var tagService: IFileTagService

    @Resource
    lateinit var fileService: IFileInfoService

    @Test
    fun addTest(){
        for (i in 1..2) {
            tagService.save(FileTag().setName("测试标签$i").setUserId(0))
        }
    }

    @ExperimentalStdlibApi
    @Test
    fun addFileTest(){
        fileService.saveBatch(buildList {
            for (i in 1..10) {
                add(FileInfo().setPath("/test/$i"))
            }
        })
    }

    @Test
    fun saveTest(){
        val tag = FileTag().setUserId(12).setName("plus")
        tag.id shouldBe null
        tagService.save(tag)
        (tag.id != null) shouldBe true
        tag.id.println()
    }

    @Test
    fun listByFileInfoId() {
    }

    @Test
    fun mapByFileInfoIds() {
    }

    @Test
    fun listByFileInfo() {
    }

    @Test
    fun mapByFileInfos() {
    }

    @Test
    fun countByTag() {
    }

    @Test
    fun addTags(){
        fileService.addTag(1, FileTag().setId(5).setUserId(166).setName("巴雷拉")) shouldBe true
    }
}