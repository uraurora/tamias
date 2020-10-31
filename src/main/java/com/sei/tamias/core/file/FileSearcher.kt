package com.sei.tamias.core.file

import com.sei.tamias.core.global.ALL_PATTERN
import com.sei.tamias.core.global.TEXT_PATTERN
import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.regex.Pattern

/**
 * [dir]: 文件路径
 * [pattern]: 文件名正则
 * [filter]: 过滤器
 */
fun search(dir: Path, pattern: String= ALL_PATTERN, filter: (Path)->Boolean={true}): List<FileInfo> {
    return Files.newDirectoryStream(dir) {
        Files.isRegularFile(it) && Pattern.matches(pattern, it.fileName.toString()) && filter(it)
    }
            .asSequence()
            .map{
                FileInfo(dir=it, attributes= Files.readAttributes(it, BasicFileAttributes::class.java))
            }
            .toList()
}

/**
 * [dir]: 文件路径
 * [pattern]: 文件名正则
 * [filter]: 过滤器
 */
fun searchRecursive(dir: Path, pattern: String= ALL_PATTERN, filter: (Path)->Boolean={true}): List<FileInfo>{
    return mutableListOf<FileInfo>().apply {
        Files.walkFileTree(dir, object : SimpleFileVisitor<Path>() {
            override fun visitFile(file: Path?, attrs: BasicFileAttributes?): FileVisitResult {
                if (file != null && attrs !=null) {
                    file.takeIf {
                        Files.isRegularFile(it) &&
                                Pattern.matches(pattern, it.fileName.toString()) &&
                                filter(it)
                    }?.let { add(FileInfo(attributes=attrs, dir=file)) }
                }
                return FileVisitResult.CONTINUE
            }
        })
    }
}

data class FileInfo(val dir: Path, val attributes: BasicFileAttributes)

fun main() {
    search(Paths.get("/Users/gaoxiaodong/data")).forEach { println(it) }
    println("=================")
    val list = searchRecursive(Paths.get("/Users/gaoxiaodong/data"),pattern = TEXT_PATTERN)
    list.forEach { println(it) }
    println(list.size)
}