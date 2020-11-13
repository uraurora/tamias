package com.sei.tamias.core.listener

import com.sei.tamias.core.global.ALL_PATTERN
import com.sun.org.apache.xpath.internal.operations.Bool
import java.nio.file.Path
import java.util.regex.Pattern

class PathObservableExtension(val handlerChain: HandlerChain<Path, Unit>,
                              dir: Path,
                              pattern: String,
                              recursive: Boolean
): PathObservable(dir = dir, pattern = pattern, recursive = recursive)
{
    fun run(){

    }
}