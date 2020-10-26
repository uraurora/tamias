package com.sei.tamias;

import com.google.common.collect.Maps;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * @author : gaoxiaodong04
 * @program : tamias
 * @date : 2020-10-23 11:11
 * @description :
 */
@Slf4j
public class PathObservable {

    private final Path directory;

    private final boolean recursive;

    /**
     * 保存watchKey对根目录下所有监听目录的映射，用来快速组合文件的目录
     */
    private final Map<WatchKey, Path> keys = Maps.newConcurrentMap();

    private PathObservable(PathObservableBuilder builder){
        this.directory = builder.directory;
        this.recursive = builder.recursive;
    }

    private Observable<WatchEvent<?>> create(){
        return Observable.create(subscriber->{
            boolean errorFree = true;
            try(WatchService watcher = directory.getFileSystem().newWatchService()) {
                try {
                    if (recursive) {
                        registerDirectories(directory, watcher);
                    } else {
                        registerDirectory(directory, watcher);
                    }
                } catch (IOException exception) {
                    subscriber.onError(exception);
                    errorFree = false;
                    log.error("文件监听注册异常", exception);
                }
                while (errorFree && !subscriber.isDisposed()) {
                    final WatchKey key;
                    try {
                        key = watcher.take();
                    } catch (InterruptedException exception) {
                        if (!subscriber.isDisposed()) {
                            subscriber.onError(exception);
                        }
                        errorFree = false;
                        log.error("从队列中取出监听事件key异常", exception);
                        break;
                    }
                    final Path dir = keys.get(key);
                    for (final WatchEvent<?> event : key.pollEvents()) {
                        subscriber.onNext(event);
                        registerNewDirectory(subscriber, dir, watcher, event);
                    }
                    // reset key and remove from set if directory is no longer accessible
                    if (!key.reset()) {
                        keys.remove(key);
                        // nothing to be watched
                        if (keys.isEmpty()) {
                            break;
                        }
                    }
                }
            }

            if (errorFree) {
                subscriber.onComplete();
            }
        });
    }

    public static PathObservableBuilder builder() {
        return new PathObservableBuilder();
    }

    public static final class PathObservableBuilder {
        private Path directory;
        private boolean recursive = false;

        private PathObservableBuilder() {
        }

        public PathObservableBuilder withDirectory(Path directory) {
            this.directory = directory;
            return this;
        }

        public PathObservableBuilder withRecursive() {
            this.recursive = true;
            return this;
        }


        public Observable<WatchEvent<?>> build() {
            return new PathObservable(this).create();
        }
    }

    /**
     * 递归文件子目录注册监听事件
     * @param path 根目录
     * @param watchService watchService
     * @throws IOException io异常
     */
    private void registerDirectories(Path path, WatchService watchService) throws IOException {
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
    private void registerDirectory(final Path dir, final WatchService watchService) throws IOException {
        final WatchKey key = dir.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        keys.put(key, dir);
    }

    // register newly created directory to watching in recursive mode
    private void registerNewDirectory(
            final ObservableEmitter<WatchEvent<?>> subscriber,
            final Path dir,
            final WatchService watcher,
            final WatchEvent<?> event) {
        final WatchEvent.Kind<?> kind = event.kind();
        if (recursive && kind.equals(ENTRY_CREATE)) {
            // Context for directory entry event is the file name of entry
            @SuppressWarnings("unchecked")
            final WatchEvent<Path> eventWithPath = (WatchEvent<Path>) event;
            final Path child = dir.resolve(eventWithPath.context());
            try {
                if (Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS)) {
                    registerDirectories(child, watcher);
                }
            } catch (final IOException exception) {
                subscriber.onError(exception);
                log.error("文件监听注册异常", exception);
            }
        }
    }

    public static void main(String[] args) {
        PathObservable.builder()
                .withDirectory(Paths.get(""))
                .withRecursive()
                .build();
    }
}
