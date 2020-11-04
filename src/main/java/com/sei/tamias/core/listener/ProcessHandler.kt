package com.sei.tamias.core.listener

import com.sei.tamias.core.global.WatchEventContext

/**
 * @program : tamias
 * @author : sei
 * @date : 2020-11-01 15:26
 * @description :
 */
interface ProcessHandler<T, R> {
    var next:ProcessHandler<T, R>
    
    fun validate(context: T):Boolean

    fun handle(context: T): R

    fun execute(context: T){
        if(validate(context)) handle(context)
    }
}