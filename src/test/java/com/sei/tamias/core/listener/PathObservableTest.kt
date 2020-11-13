package com.sei.tamias.core.listener

import com.sei.tamias.core.global.TEXT_PATTERN
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.springframework.boot.test.context.SpringBootTest
import java.net.URLDecoder
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.BlockingQueue
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue

fun <T> T.println(){
    println(this)
}

fun <T> Observable<T>.println() {
    this.subscribe { println(it) }
}

fun <T> Observable<T>.print() {
    this.subscribe { print(it) }
}

infix fun <T> T.shouldBe(other: T): Unit{
    assertEquals(this, other)
}

fun threadInfo(info: String){
    "${Thread.currentThread().name},info=$info".println()
}

@SpringBootTest
internal class ObservableTest{

    @Test fun justTest(){
        val l = mutableListOf<Int>()
        val cache = Observable.just(1, 2, 3, 4, 5).cache()
        cache.println()
        cache.subscribe {l.add(it)}
        l shouldBe listOf(1, 2, 3, 4, 5)
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

    @Test fun schedulerTest(){
        Observable
                .create<String> {
                    for (i in 1..10) {
                        it.onNext("RX_JAVA")
                    }
                    it.onComplete()
                }
                .subscribeOn(Schedulers.newThread())
                .doOnSubscribe { threadInfo(".doOnSubscribe()-1") }
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { threadInfo(".doOnSubscribe()-2") }
                .observeOn(Schedulers.from(Executors.newCachedThreadPool(Executors.defaultThreadFactory())))
                .subscribe {
                    threadInfo(".onNext()")
                    println("$it-onNext")
                }
        Thread.sleep(10000)
    }

    @Test fun schedulerTest2(){
        Observable.just(1,2,3,4)
                .flatMap { Observable.just(it*2) }
                .doOnNext { threadInfo(it.toString()) }
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .subscribe { threadInfo(it.toString()) }

        Thread.sleep(10000)
    }

    @Test fun schedulerTest3(){
        val l = mutableListOf<Int>()
        Observable.just(1,2,3,4)
                .flatMap { Observable.just(it*2) }
                .doOnNext { threadInfo(it.toString()) }
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .subscribe { l.add(it) }

        Thread.sleep(10000)
        println(l)
    }


    @Test fun contactTest(){

    }

    @Test fun decodeTest(){
        val s = URLDecoder.decode("dianping://web?url=https%3A%2F%2Fg.dianping.com%2Fapp%2Fapp-business-coupon-page%2Findex.html%3FlaunchId%3D10956384%26shopId%3DH3YEOaYbUYLGvbMP%26shopuuid%3D%23%26notitlebar%3D1&notitlebar=1", "utf-8")
        print(s)
    }


}

internal class PathObservableTest{
    @Test
    fun listenTest(){
        val c = PathObservable(dir = Paths.get("/Users/gaoxiaodong/data"), pattern = TEXT_PATTERN).create()
                .subscribeOn(Schedulers.single())
                .doOnSubscribe { threadInfo(".doOnSubscribe()-1") }
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { threadInfo(".doOnSubscribe()-2") }
                .observeOn(Schedulers.from(Executors.newCachedThreadPool(Executors.defaultThreadFactory())))
                .subscribe {
                    threadInfo(".onNext()")
                    println("$it-onNext")
                }
        //Thread.sleep(30000)
    }
}