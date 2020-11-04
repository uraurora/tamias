package com.sei.tamias.core.listener

import java.lang.IllegalArgumentException

/**
 * @program : tamias
 * @author : sei
 * @date : 2020-11-01 15:40
 * @description :
 */
class HandlerChain<T, R>(private val handlers:MutableList<ProcessHandler<T, R>> = mutableListOf())
    :MutableList<ProcessHandler<T, R>> by handlers{

    fun process(context: T){
        this.handlers.forEach {
            it.execute(context)
        }
    }

}

class Partial<in P, out R>(private val validate:(P)->Boolean,
                           private val handle:(P)->R
):(P)->R{
    override fun invoke(p:P):R{
        if(validate(p)){
            return handle(p)
        }else{
            throw IllegalArgumentException("value: $p is not supported by this function")
        }
    }

    fun isValidated(p:P):Boolean = validate(p)

}

infix fun <P, R> Partial<P, R>.orElse(other: Partial<P, R>): Partial<P, R> {
    return Partial({this.isValidated(it) || other.isValidated(it)}){
        when{
            this.isValidated(it)->this(it)
            else->other(it)
        }
    }
}