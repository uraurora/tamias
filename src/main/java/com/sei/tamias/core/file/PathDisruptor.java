package com.sei.tamias.core.file;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.sei.tamias.core.global.WatchEventContext;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;

import static com.sei.tamias.util.Options.*;

/**
 * @author : gaoxiaodong04
 * @program : tamias
 * @date : 2020-12-29 14:46
 * @description :
 */
public final class PathDisruptor extends AbstractPathListener{

    private final Disruptor<WatchEventContext<Path>> disruptor;

    private final List<EventHandler<WatchEventContext<Path>>> handlers;

    private final WaitStrategy waitStrategy;

    private final int ringBufferSize;

    private PathDisruptor(PathDisruptorBuilder builder){
        this.handlers = builder.handlers;
        this.waitStrategy = builder.waitStrategy;
        this.ringBufferSize = builder.ringBufferSize;
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

        private int ringBufferSize;

        private WaitStrategy waitStrategy = new YieldingWaitStrategy();

        public PathDisruptorBuilder withHandlers(List<EventHandler<WatchEventContext<Path>>> handlers) {
            this.handlers.addAll(handlers);
            return this;
        }

        @SafeVarargs
        public final PathDisruptorBuilder withHandlers(EventHandler<WatchEventContext<Path>>... handlers) {
            this.handlers.addAll(listOf(handlers));
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

        public PathDisruptor build(){
            return new PathDisruptor(this);
        }
    }

    public void subscribe(){
        disruptor.start();
        disruptor.publishEvent(((event, sequence) -> {}));
    }

    static class PathEventFactory implements EventFactory<WatchEventContext<Path>>{
        @Override
        public WatchEventContext<Path> newInstance() {
            return new WatchEventContext<>(null, null, LocalDateTime.now());
        }
    }

    
}

