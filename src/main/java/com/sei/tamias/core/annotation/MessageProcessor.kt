package com.sei.tamias.core.annotation

import lombok.extern.slf4j.Slf4j
import org.aspectj.lang.annotation.AfterReturning
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component

@Component
@Aspect
@Slf4j
@Order(1)
open class MessageProcessor {

    @Pointcut(value = "@annotation(${MESSAGE_ANNOTATION})||@within(${MESSAGE_ANNOTATION})")
    open fun pointCut() {}

    @AfterReturning(pointcut = "Pointcut()", returning = "result")
    open fun process(result: Any){

    }




}