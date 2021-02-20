package com.sei.tamias.core.file;

import com.sei.tamias.core.value.WatchEventContext;
import io.reactivex.rxjava3.core.Observable;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;

import static com.sei.tamias.global.ConstantsKt.ALL_PATTERN;

/**
 * @author : gaoxiaodong04
 * @program : tamias
 * @date : 2020-10-23 11:11
 * @description :
 */
@Slf4j
public class PathObservable extends AbstractPathListener{

    private PathObservable(PathObservableBuilder builder){
        this.directory = builder.directory;
        this.recursive = builder.recursive;
        this.pattern = builder.pattern;
    }

    /**
     * 返回Observable，见{@link Observable}，subscriber是文件变更发射的事件
     * @return Observable
     */
    public Observable<WatchEventContext<Path>> toObservable(){
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
                        @SuppressWarnings("unchecked")
                        final WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
                        final WatchEventContext<Path> contextEvent = new WatchEventContext<>();
                        contextEvent.setEvent(pathEvent);
                        contextEvent.setDir(pathEvent.context());

                        subscriber.onNext(contextEvent);
                        registerNewDirectory(subscriber, dir, watcher, contextEvent);
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
        private boolean recursive = true;
        /**
         * 文件名正则
         */
        private String pattern = ALL_PATTERN;

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

        public PathObservableBuilder withPattern(String pattern) {
            this.pattern = pattern;
            return this;
        }


        public PathObservable build() {
            return new PathObservable(this);
        }
    }

}
