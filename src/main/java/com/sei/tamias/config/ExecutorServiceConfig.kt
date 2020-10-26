package com.sei.tamias.config

import com.google.common.util.concurrent.ThreadFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.ExecutorService
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

@Configuration
open class ExecutorServiceConfig {
    @Bean(name = ["executorService"])
    open fun executorService(): ExecutorService {
        val namedThreadFactory = ThreadFactoryBuilder().setNameFormat("common-pool-%d").build()
        return ThreadPoolExecutor(
                4,
                40,
                0L,
                TimeUnit.MILLISECONDS,
                LinkedBlockingQueue(1024 * 1000),
                namedThreadFactory,
                ThreadPoolExecutor.AbortPolicy()
        )
    }

    @Bean(name = ["crawlerExecutorService"])
    open fun crawlerExecutorService(): ExecutorService {
        val namedThreadFactory = ThreadFactoryBuilder().setNameFormat("crawler-pool-%d").build()
        return ThreadPoolExecutor(
                10,
                70,
                0L,
                TimeUnit.MILLISECONDS,
                LinkedBlockingQueue(2048 * 1000),
                namedThreadFactory,
                ThreadPoolExecutor.AbortPolicy()
        )
    }

    @Bean(name = ["mailExecutorService"])
    open fun mailExecutorService(): ExecutorService {
        val namedThreadFactory = ThreadFactoryBuilder().setNameFormat("mail-pool-%d").build()
        return ThreadPoolExecutor(
                5,
                5,
                0L,
                TimeUnit.MILLISECONDS,
                LinkedBlockingQueue(1024 * 1000),
                namedThreadFactory,
                ThreadPoolExecutor.AbortPolicy()
        )
    }

    @Bean(name = ["recommendDownGradeExecutorService"])
    open fun recommendDownGradeExecutorService(): ExecutorService {
        val namedThreadFactory = ThreadFactoryBuilder().setNameFormat("recommendDowngrade-pool-%d").build()
        return ThreadPoolExecutor(
                4,
                40,
                0L,
                TimeUnit.MILLISECONDS,
                LinkedBlockingQueue(1024 * 1000),
                namedThreadFactory,
                ThreadPoolExecutor.AbortPolicy()
        )
    }

    @Bean(name = ["saveToDBExecutorService"])
    open fun saveToDBExecutorService(): ExecutorService {
        val namedThreadFactory = ThreadFactoryBuilder().setNameFormat("saveToDB-pool-%d").build()
        return ThreadPoolExecutor(
                4,
                40,
                0L,
                TimeUnit.MILLISECONDS,
                LinkedBlockingQueue(1024 * 1000),
                namedThreadFactory,
                ThreadPoolExecutor.AbortPolicy()
        )
    }

    @Bean(name = ["httpclientExecutorService"])
    open fun httpclientExecutorService(): ExecutorService {
        val namedThreadFactory = ThreadFactoryBuilder().setNameFormat("httpclient-pool-%d").build()
        return ThreadPoolExecutor(
                10,
                10,
                0L,
                TimeUnit.MILLISECONDS,
                LinkedBlockingQueue(1024 * 1000),
                namedThreadFactory,
                ThreadPoolExecutor.AbortPolicy()
        )
    }
}