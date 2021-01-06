package com.sei.tamias.core.file;

import com.google.common.collect.Maps;
import com.sei.tamias.core.global.WatchEventContext;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.ObservableEmitter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

/**
 * @author : gaoxiaodong04
 * @program : tamias
 * @date : 2020-12-29 15:54
 * @description :
 */
@Slf4j
public abstract class AbstractPathListener {

    /**
     * 文件目录
     */
    protected Path directory;
    /**
     * 是否递归
     */
    protected boolean recursive;

    /**
     * 文件匹配正则
     */
    protected String pattern;

    /**
     * 保存watchKey对根目录下所有监听目录的映射，用来快速组合文件的目录
     */
    protected final Map<WatchKey, Path> keys = Maps.newConcurrentMap();

    protected AbstractPathListener() {
    }

    /**
     * 递归文件子目录注册监听事件
     * @param path 根目录
     * @param watchService watchService
     * @throws IOException io异常
     */
    protected void registerDirectories(Path path, WatchService watchService) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                registerDirectory(dir, watchService);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * 指定文件注册监听事件：创建、删除和修改
     * @param dir 目标文件路径
     * @param watchService watcher
     * @throws IOException io异常
     */
    protected void registerDirectory(final Path dir, final WatchService watchService) throws IOException {
        if(Files.isDirectory(dir) || Pattern.matches(pattern, dir.getFileName().toString())){
            final WatchKey key = dir.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
            keys.put(key, dir);
        }
    }

    /**
     * register newly created directory to watching in recursive mode
     * @param subscriber 生产者
     * @param dir 目录地址
     * @param watcher watchService，见{@link WatchService}
     * @param event 文件变动事件，见{@link WatchEventContext<Path>}
     */
    protected void registerNewDirectory(
            final ObservableEmitter<WatchEventContext<Path>> subscriber,
            final Path dir,
            final WatchService watcher,
            final WatchEventContext<Path> event) {
        final WatchEvent.Kind<?> kind = Objects.requireNonNull(event.getEvent()).kind();
        if (recursive && kind.equals(ENTRY_CREATE)) {
            // Context for directory entry event is the file name of entry
            final Path child = dir.resolve(event.getEvent().context());
            try {
                if (Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS)) {
                    registerDirectories(child, watcher);
                }
            } catch (final IOException exception) {
                if(subscriber != null){
                    subscriber.onError(exception);
                }
                log.error("文件监听注册异常", exception);
            }
        }
    }
}
