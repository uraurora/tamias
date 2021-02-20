package com.sei.tamias.core.file;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.sei.tamias.core.value.WatchEventContext;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.concurrent.Executors;

import static com.sei.tamias.global.constnts.Constants.ALL_PATTERN;
import static com.sei.tamias.util.Options.isEmpty;
import static com.sei.tamias.util.Options.listOf;

/**
 * @author : gaoxiaodong04
 * @program : tamias
 * @date : 2020-12-29 14:46
 * @description :
 */
@Slf4j
public final class PathDisruptor extends AbstractPathListener{

    private final Disruptor<WatchEventContext<Path>> disruptor;

    private final List<EventHandler<WatchEventContext<Path>>> handlers;

    private final WaitStrategy waitStrategy;

    private final int ringBufferSize;

    private PathDisruptor(PathDisruptorBuilder builder){
        this.handlers = builder.handlers;
        this.waitStrategy = builder.waitStrategy;
        this.ringBufferSize = builder.ringBufferSize;
        this.directory = builder.directory;
        this.recursive = builder.recursive;
        this.pattern = builder.pattern;
        if(directory == null || !Files.isDirectory(directory)){
            throw new IllegalArgumentException("the directory should not be null or file");
        }
        if(isEmpty(handlers)){
            throw new IllegalArgumentException("the handlers of disruptor should not be empty");
        }
        disruptor = new Disruptor<>(
                new PathEventFactory(),
                ringBufferSize,
                Executors.defaultThreadFactory(),
                ProducerType.SINGLE,
                waitStrategy
        );

        @SuppressWarnings("unchecked")
        final EventHandler<WatchEventContext<Path>>[] eventHandlers = handlers.toArray(new EventHandler[0]);
        disruptor.handleEventsWith(eventHandlers);
    }

    public static PathDisruptorBuilder builder(){
        return new PathDisruptorBuilder();
    }

    public static class PathDisruptorBuilder {
        private final List<EventHandler<WatchEventContext<Path>>> handlers = listOf();

        private int ringBufferSize = 1024 * 16;

        private WaitStrategy waitStrategy = new YieldingWaitStrategy();

        /**
         * 文件目录
         */
        private Path directory;
        /**
         * 是否递归
         */
        private boolean recursive = true;

        /**
         * 文件名正则
         */
        private String pattern = ALL_PATTERN;

        /**
         * 见{@link EventHandler}
         * @param handlers 消费者处理者
         * @return 返回构建者本身
         */
        public PathDisruptorBuilder withHandlers(List<EventHandler<WatchEventContext<Path>>> handlers) {
            this.handlers.addAll(handlers);
            return this;
        }

        @SafeVarargs
        public final PathDisruptorBuilder withHandlers(EventHandler<WatchEventContext<Path>>... handlers) {
            this.handlers.addAll(listOf(handlers));
            return this;
        }

        /**
         * 见{@link EventHandler}
         * @param handler 消费者处理者
         * @return 返回构建者本身
         */
        public final PathDisruptorBuilder withHandler(EventHandler<WatchEventContext<Path>> handler) {
            this.handlers.add(handler);
            return this;
        }

        public PathDisruptorBuilder withWaitStrategy(WaitStrategy waitStrategy) {
            this.waitStrategy = waitStrategy;
            return this;
        }

        public PathDisruptorBuilder withRingBufferSize(int ringBufferSize) {
            this.ringBufferSize = ringBufferSize;
            return this;
        }

        /**
         * 是否递归监听文件
         * @param recursive 是否递归
         * @return builder
         */
        public PathDisruptorBuilder withRecursive(boolean recursive) {
            this.recursive = recursive;
            return this;
        }

        public PathDisruptorBuilder withDirectory(Path directory) {
            this.directory = directory;
            return this;
        }

        public PathDisruptorBuilder withPattern(String pattern) {
            this.pattern = pattern;
            return this;
        }


        public PathDisruptor build(){
            return new PathDisruptor(this);
        }
    }

    public void start() {
        disruptor.start();
        boolean errorFree = true;
        try(WatchService watcher = directory.getFileSystem().newWatchService()) {
            try {
                if (recursive) {
                    registerDirectories(directory, watcher);
                } else {
                    registerDirectory(directory, watcher);
                }
            } catch (IOException exception) {

                errorFree = false;
                log.error("文件监听注册异常", exception);
            }
            while (errorFree) {
                final WatchKey key;
                try {
                    key = watcher.take();
                } catch (InterruptedException exception) {
                    errorFree = false;
                    log.error("从队列中取出监听事件key异常", exception);
                    break;
                }
                final Path dir = keys.get(key);
                for (final WatchEvent<?> event : key.pollEvents()) {
                    @SuppressWarnings("unchecked")
                    final WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
                    // disruptor发布事件
                    disruptor.publishEvent((event1, sequence) -> {
                        event1.setEvent(pathEvent);
                        event1.setDir(pathEvent.context());
                    });
                    final WatchEventContext<Path> contextEvent = new WatchEventContext<>();
                    contextEvent.setEvent(pathEvent);
                    contextEvent.setDir(pathEvent.context());
                    registerNewDirectory(null, dir, watcher, contextEvent);
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
        } catch (IOException e) {
            log.error("新建文件变更通知器失败");
        }

        if (errorFree) {
            log.info("文件变更事件发送完成");
        }
    }

    public void shutdown(){
        this.disruptor.shutdown();
    }

    /**
     * 等待策略，具体见{@link WaitStrategy}
     * @return 等待策略
     */
    public WaitStrategy getWaitStrategy() {
        return waitStrategy;
    }

    /**
     * 获取环形队列的大小，见{@link PathDisruptor#ringBufferSize}
     * @return 环形队列大小
     */
    public int getRingBufferSize() {
        return ringBufferSize;
    }

    static class PathEventFactory implements EventFactory<WatchEventContext<Path>>{
        @Override
        public WatchEventContext<Path> newInstance() {
            return new WatchEventContext<>();
        }
    }

    
}

