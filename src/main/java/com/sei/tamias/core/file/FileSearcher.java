package com.sei.tamias.core.file;


import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static com.sei.tamias.util.Options.listOf;

/**
 * @author : gaoxiaodong04
 * @program : tamias
 * @date : 2021-01-23 16:30
 * @description :
 */
public abstract class FileSearcher {
    /**
     * 搜索文件夹下的文件信息
     * @param dir 文件夹目录
     * @param pattern 文件名正则
     * @param filter 过滤器
     * @return 文件信息列表
     * @throws IOException io异常
     */
    public static List<FileInfoContext> search(Path dir, String pattern, Predicate<? super Path> filter) throws IOException {
        final List<FileInfoContext> res = listOf();
        try(final DirectoryStream<Path> paths = Files.newDirectoryStream(dir)){
            for (Path path : paths) {
                final boolean b = Files.isRegularFile(path) && Pattern.matches(pattern, path.getFileName().toString()) && filter.test(path);
                if(b){
                    res.add(new FileInfoContext(path, Files.readAttributes(path, BasicFileAttributes.class)));
                }
            }
        }
        return res;
    }

    /**
     * 递归搜索dir目录下的文件，包括子目录下的文件
     * [dir]: 文件路径
     * [pattern]: 文件名正则
     * [filter]: 过滤器
     */
//    fun searchRecursive(dir: Path, pattern: String = ALL_PATTERN, filter: (Path) -> Boolean = { true }): List<FileInfoContext>{
//        return mutableListOf<FileInfoContext>().apply {
//            Files.walkFileTree(dir, object :SimpleFileVisitor<Path> () {
//                override fun visitFile(file: Path?, attrs: BasicFileAttributes?): FileVisitResult {
//                    if (file != null && attrs != null) {
//                        file.takeIf {
//                            Files.isRegularFile(it) &&
//                                    Pattern.matches(pattern, it.fileName.toString()) &&
//                                    filter(it)
//                        }?.let { add(FileInfoContext(attributes = attrs, dir = file)) }
//                    }
//                    return FileVisitResult.CONTINUE
//                }
//            })
//        }
//    }

    /**
     * 判断sub是否与parent相等或在其之下<br></br>
     * parent必须存在，且必须是directory,否则抛出[IllegalArgumentException]
     * @param parent 父级目录
     * @param sub 子级目录
     * @return 判断sub是否与parent相等或在其之下
     * @throws IOException io异常
     */
    public static boolean sameOrSub(Path parent, Path sub) throws IOException {
        Path subPath = sub;
        if(!Files.exists(parent) || !Files.isDirectory(parent)) {
            throw  new IllegalArgumentException(String.format("the parent not exist or not directory %s", parent));
        }
        while (null != subPath) {
            if (Files.exists(subPath) && Files.isSameFile(parent, subPath)) {
                return true;
            }
            subPath = subPath.getParent();
        }
        return false;
    }


    /**
     * 判断sub是否在parent之下的文件或子文件夹<br></br>
     * parent必须存在，且必须是directory,否则抛出[IllegalArgumentException]
     * @param parent 父级目录
     * @param sub 子级目录
     * @return sub是否是parent的子级目录
     * @throws IOException
     */
    public static boolean isSub(Path parent, Path sub) throws IOException {
        return (null != sub) && sameOrSub(parent, sub.getParent());
    }
}
