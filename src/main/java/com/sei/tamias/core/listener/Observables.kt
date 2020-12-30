package com.sei.tamias.core.listener

import com.sei.tamias.core.file.PathObservableKotlin
import java.nio.file.Path

class PathObservableExtension(val handlerChain: HandlerChain<Path, Unit>,
                              dir: Path,
                              pattern: String,
                              recursive: Boolean
): PathObservableKotlin(dir = dir, pattern = pattern, recursive = recursive)
{
    fun run(){

    }
}