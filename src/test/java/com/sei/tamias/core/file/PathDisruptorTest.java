package com.sei.tamias.core.file;

import com.lmax.disruptor.EventHandler;
import com.sei.tamias.core.value.WatchEventContext;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

class PathDisruptorTest {

    @Test
    public void pathDisruptorTest() {
        PathDisruptor disruptor = PathDisruptor.builder()
                .withDirectory(Paths.get("/Users/gaoxiaodong/data"))
                .withRingBufferSize(1024)
                .withHandler(new Handler())
                .build();
        disruptor.start();
    }

    @Test
    public void pathObservableTest() throws InterruptedException {
        PathObservable.builder()
                .withDirectory(Paths.get("/Users/gaoxiaodong/data"))
                .build()
                .toObservable()
                .subscribe(System.out::println);
        //Thread.sleep(30000);
    }

}


class Handler implements EventHandler<WatchEventContext<Path>>{

    @Override
    public void onEvent(WatchEventContext<Path> event, long sequence, boolean endOfBatch) throws Exception {
        System.out.println(event);
    }
}