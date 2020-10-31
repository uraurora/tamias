package com.sei.tamias.core.dispatcher

import com.lmax.disruptor.EventHandler
import com.lmax.disruptor.WorkHandler
import com.lmax.disruptor.YieldingWaitStrategy
import com.lmax.disruptor.dsl.Disruptor
import com.lmax.disruptor.dsl.ProducerType
import org.junit.jupiter.api.Test
import java.util.*
import java.util.concurrent.Executors

import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

internal class DispatcherTest {
    @Test
    fun start() {
        val d: Disruptor<OrderEvent> = Disruptor<OrderEvent>(
                { OrderEvent(UUID.randomUUID().toString()) },
                1024 * 1024,
                Executors.defaultThreadFactory(),
                ProducerType.SINGLE,
                YieldingWaitStrategy()
        )

        d.handleEventsWith(OrderEventHandler())
        d.start()
        for (i in 1..10) {
            d.publishEvent{ _: OrderEvent, _: Long -> }
        }

    }

    @Test
    fun launchTest() {
        runBlocking {
            repeat(100_00){
                launch{
                    delay(1000)
                    print(".")
                }
            }
        }
    }

    @Test
    fun executorTest(){
        val executor = Executors.newSingleThreadScheduledExecutor()
        val task = Runnable {
            print(".")
        }
        for (i in 1..100_00) {
            executor.schedule(task, 1, TimeUnit.SECONDS)
        }
    }
}

data class OrderEvent(val id: String)

class OrderEventHandler : EventHandler<OrderEvent>, WorkHandler<OrderEvent> {
    override fun onEvent(event: OrderEvent, sequence: Long, endOfBatch: Boolean) {
        val str = StringBuilder()
                .append("event: $event\n\r")
                .append("sequence : $sequence\n\r")
                .append("endOfBatch :$endOfBatch\n\r")
                .toString()
        println(str)
    }

    override fun onEvent(event: OrderEvent) {
        println("event :$event")
    }
}