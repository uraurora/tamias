package com.sei.tamias.core.listener

import com.google.common.collect.Maps
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.ObservableEmitter
import java.io.IOException
import java.nio.file.*
import java.nio.file.StandardWatchEventKinds.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.regex.Pattern

open class PathObservable(val dir: Path,
                          val pattern: String = ".+",
                          val recursive: Boolean = false
) {
    private val keys: MutableMap<WatchKey, Path> = Maps.newConcurrentMap()

    fun create(): Observable<WatchEvent<*>> {
        return Observable.create { subscriber: ObservableEmitter<WatchEvent<*>> ->
            var errorFree = true
            dir.fileSystem.newWatchService().use { watcher ->
                try {
                    when(recursive) {
                        true -> registerDirectories(dir, watcher)
                        false -> registerDirectory(dir, watcher)
                    }
                } catch (exception: IOException) {
                    subscriber.onError(exception)
                    errorFree = false
                }
                while (errorFree && !subscriber.isDisposed) {
                    val key: WatchKey
                    try {
                        key = watcher.take()
                    } catch (exception: InterruptedException) {
                        subscriber.takeIf { !it.isDisposed }?.onError(exception)
                        errorFree = false
                        break
                    }
                    key.pollEvents().forEach {
                        subscriber.onNext(it)
                        keys[key]?.let { dir->registerNewDirectory(subscriber, dir, watcher, it) }
                    }
                    // reset key and remove from set if directory is no longer accessible
                    if (!key.reset()) keys.remove(key)
                        // nothing to be watched
                        if (keys.isEmpty()) break
                }
            }
            if (errorFree) subscriber.onComplete()
        }
    }

    /**
     * 递归文件子目录注册监听事件
     * @param path 根目录
     * @param watchService watchService
     * @throws IOException io异常
     */
    @Throws(IOException::class)
    private fun registerDirectories(path: Path, watchService: WatchService) {
        Files.walkFileTree(path, object : SimpleFileVisitor<Path>() {
            @Throws(IOException::class)
            override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult {
                registerDirectory(dir, watchService)
                return FileVisitResult.CONTINUE
            }
        })
    }

    /**
     * 指定文件注册监听事件：创建、删除和修改
     * @param dir 目标文件路径
     * @param watchService watcher
     * @throws IOException io异常
     */
    @Throws(IOException::class)
    private fun registerDirectory(dir: Path, watchService: WatchService) {
        dir.takeIf { Files.isDirectory(it) || Pattern.matches(pattern, it.toString()) }
                ?.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY)
                ?.let { keys[it] = dir }
    }

    // register newly created directory to watching in recursive mode
    private fun registerNewDirectory(
            subscriber: ObservableEmitter<WatchEvent<*>>,
            dir: Path,
            watcher: WatchService,
            event: WatchEvent<*>) {
        val kind = event.kind()
        if (recursive && kind == ENTRY_CREATE) {
            // Context for directory entry event is the file name of entry
            val eventWithPath = event as WatchEvent<Path>
            val child = dir.resolve(eventWithPath.context())
            try {
                child.takeIf { Files.isDirectory(it, LinkOption.NOFOLLOW_LINKS) }
                        ?.let { registerDirectories(it, watcher) }
            } catch (exception: IOException) {
                subscriber.onError(exception)
            }
        }
    }
}