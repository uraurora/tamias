package com.sei.tamias.core.file

import com.sei.tamias.core.global.ALL_PATTERN
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.util.regex.Pattern

/**
 * 搜索dir路劲下的所有文件，但不包含子目录的文件
 * [dir]: 文件路径
 * [pattern]: 文件名正则
 * [filter]: 过滤器
 */
fun search(dir: Path, pattern: String = ALL_PATTERN, filter: (Path) -> Boolean = { true }): List<FileInfoContext> {
    return Files.newDirectoryStream(dir) {
        Files.isRegularFile(it) && Pattern.matches(pattern, it.fileName.toString()) && filter(it)
    }
            .asSequence()
            .map{
                FileInfoContext(dir = it, attributes = Files.readAttributes(it, BasicFileAttributes::class.java))
            }
            .toList()
}

/**
 * 递归搜索dir目录下的文件，包括子目录下的文件
 * [dir]: 文件路径
 * [pattern]: 文件名正则
 * [filter]: 过滤器
 */
fun searchRecursive(dir: Path, pattern: String = ALL_PATTERN, filter: (Path) -> Boolean = { true }): List<FileInfoContext>{
    return mutableListOf<FileInfoContext>().apply {
        Files.walkFileTree(dir, object : SimpleFileVisitor<Path>() {
            override fun visitFile(file: Path?, attrs: BasicFileAttributes?): FileVisitResult {
                if (file != null && attrs != null) {
                    file.takeIf {
                        Files.isRegularFile(it) &&
                                Pattern.matches(pattern, it.fileName.toString()) &&
                                filter(it)
                    }?.let { add(FileInfoContext(attributes = attrs, dir = file)) }
                }
                return FileVisitResult.CONTINUE
            }
        })
    }
}

/**
 * 判断sub是否与parent相等或在其之下<br></br>
 * parent必须存在，且必须是directory,否则抛出[IllegalArgumentException]
 * @param parent
 * @param sub
 * @return
 * @throws IOException
 */
@Throws(IOException::class)
fun sameOrSub(parent: Path, sub: Path?): Boolean {
    var subPath = sub
    require(Files.exists(parent) && Files.isDirectory(parent)) { java.lang.String.format("the parent not exist or not directory %s", parent) }
    while (null != subPath) {
        if (Files.exists(subPath) && Files.isSameFile(parent, subPath)) return true
        subPath = subPath.parent
    }
    return false
}

/**
 * 判断sub是否在parent之下的文件或子文件夹<br></br>
 * parent必须存在，且必须是directory,否则抛出[IllegalArgumentException]
 * @param parent
 * @param sub
 * @return
 * @throws IOException
 * @see {@link .sameOrSub
 */
@Throws(IOException::class)
fun isSub(parent: Path, sub: Path?): Boolean {
    return if (null == sub) false else sameOrSub(parent, sub.parent)
}

data class FileInfoContext(val dir: Path, val attributes: BasicFileAttributes)