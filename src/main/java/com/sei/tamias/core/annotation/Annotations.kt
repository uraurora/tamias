package com.sei.tamias.core.annotation

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.reflect.MethodSignature
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method


const val MESSAGE_ANNOTATION: String = "com.sei.tamias.core.annotation.WithMessage"

const val SQLITE_INSERT_ANNOTATION: String = "com.sei.tamias.core.annotation.SqliteInsert"

const val SQLITE_UPDATE_ANNOTATION: String = "com.sei.tamias.core.annotation.SqliteUpdate"

const val SQLITE_DELETE_ANNOTATION: String = "com.sei.tamias.core.annotation.SqliteDelete"


@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class WithMessage()

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class SqliteUpdate()

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class SqliteInsert()

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class SqliteDelete()

@Throws(IllegalAccessException::class, NoSuchMethodException::class, InvocationTargetException::class)
fun getFirstMethodArgByAnnotationValueMethodValue(joinPoint: JoinPoint, argAnnotationClass: Class<*>, annotationMethodValue: String?): Any? {
    return getJoinPointArgsByAnnotation(joinPoint, argAnnotationClass, "value", annotationMethodValue)[0].second
}

@Throws(IllegalAccessException::class, NoSuchMethodException::class, InvocationTargetException::class)
fun getMethodArgsByAnnotationValueMethodValue(joinPoint: JoinPoint, argAnnotationClass: Class<*>): List<Pair<Int, *>>? {
    return getJoinPointArgsByAnnotation(joinPoint, argAnnotationClass, null, null)
}

@Throws(InvocationTargetException::class, IllegalAccessException::class, NoSuchMethodException::class)
fun getJoinPointArgsByAnnotation(joinPoint: JoinPoint,
                                 argAnnotationClass: Class<*>,
                                 annotationMethodName: String?,
                                 annotationMethodValue: String?
): List<Pair<Int, *>> {
    val args: Array<Any> = joinPoint.args
    val signature: MethodSignature = joinPoint.signature as MethodSignature
    val method: Method = signature.method
    val parameterAnnotations = method.parameterAnnotations
    assert(args.size == parameterAnnotations.size)
    return mutableListOf<Pair<Int, *>>().apply {
        for (argIndex in args.indices) {
            parameterAnnotations[argIndex].forEach { annotation ->
                if (annotation.javaClass != argAnnotationClass) return@forEach
                if (annotationMethodName != null && annotationMethodValue != null) {
                    val m = argAnnotationClass.getMethod(annotationMethodName)
                    if (annotationMethodValue != m.invoke(annotation)) return@forEach
                }
                add(argIndex to args[argIndex])
            }
        }
    }
}

@Throws(NoSuchMethodException::class, InvocationTargetException::class, IllegalAccessException::class)
fun setJoinPointArgsByAnnotation(joinPoint: JoinPoint,
                                 targetValue: Any,
                                 argAnnotationClass: Class<*>,
                                 annotationMethodName: String?,
                                 annotationMethodValue: String?
) {
    val args: Array<Any> = joinPoint.args
    val signature: MethodSignature = joinPoint.signature as MethodSignature
    val method: Method = signature.method
    val parameterAnnotations = method.parameterAnnotations
    assert(args.size == parameterAnnotations.size)
    for (argIndex in args.indices) {
        parameterAnnotations[argIndex].forEach { annotation ->
            if (annotation.javaClass != argAnnotationClass) {
                return@forEach
            }
            if (annotationMethodName != null && annotationMethodValue != null) {
                val m = argAnnotationClass.getMethod(annotationMethodName)
                if (annotationMethodValue != m.invoke(annotation)) {
                    return@forEach
                }
            }
            args[argIndex] = targetValue
        }
    }
}