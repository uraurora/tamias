package com.sei.tamias.core.listener

import io.reactivex.rxjava3.core.Observable
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.springframework.boot.test.context.SpringBootTest

fun <T> Observable<T>.println() {
    this.subscribe { println(it) }
}

fun <T> Observable<T>.print() {
    this.subscribe { print(it) }
}

infix fun <T> T.shouldBe(other: T): Unit{
    assertEquals(this, other)
}

@SpringBootTest
internal class PathObservableTest{

    @Test fun justTest(){
        val l = mutableListOf<Int>()
        val cache = Observable.just(1, 2, 3, 4, 5).cache()
        cache.println()
        cache.subscribe {l.add(it)}
        l shouldBe listOf(1,2,3,4,5)
    }

    /**
     * 使用create方法时要注意判断isDisposed，如果为true则不要发射数据了
     */
    @Test
    fun createTest(){
        Observable.create<Int> { emitter ->
            try {
                emitter.takeIf { !it.isDisposed }
                        ?.let {
                            for (i in 1..5) {
                                it.onNext(i)
                            }
                            it.onComplete()
                        }
            } catch (e: Exception) {
                emitter.onError(e)
            }
        }.println()
    }

    @Test fun fromTest(){
        Observable.fromIterable(1..5).println()
    }

    @Test fun mergeTest(){

    }

    @Test fun contactTest(){

    }


}