package com.sei.tamias.core.file

import com.sei.tamias.core.global.PICTURE_PATTERN
import com.sei.tamias.core.global.TEXT_PATTERN
import org.junit.Test
import java.nio.file.Paths
import java.util.regex.Pattern

internal class FileSearcherKtTest{

    @Test
    fun search(){
        search(Paths.get("/Users/gaoxiaodong/data")).forEach { println(it) }
        println("=================")
        val list = searchRecursive(Paths.get("/Users/gaoxiaodong/data"),pattern = TEXT_PATTERN)
        list.forEach { println(it) }
        println(list.size)
    }

}

internal class PatternTest{

    @Test fun pictureTest(){
        Pattern.matches(PICTURE_PATTERN, "asas.png.44") shouldBe false
        Pattern.matches(PICTURE_PATTERN, "asaas.png.44.png") shouldBe true
        Pattern.matches(PICTURE_PATTERN, "!!!撒asas.txt") shouldBe false
        Pattern.matches(PICTURE_PATTERN, "asaas.png.44.jpeg") shouldBe true
        Pattern.matches(PICTURE_PATTERN, "!!!撒asas44.bmp") shouldBe true

        Pattern.matches(TEXT_PATTERN, "!!!撒asas44.txt.....bjj") shouldBe false
        Pattern.matches(TEXT_PATTERN, "!!!撒asas44.txt.....bjj.txt") shouldBe true

    }
}