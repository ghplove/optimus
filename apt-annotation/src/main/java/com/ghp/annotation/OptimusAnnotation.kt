package com.ghp.annotation

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import kotlin.reflect.KClass

@Retention(RetentionPolicy.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
annotation class OptimusAnnotation(
    /** Returns the interfaces implemented by this service provider.  */
    val value: KClass<*>
)
