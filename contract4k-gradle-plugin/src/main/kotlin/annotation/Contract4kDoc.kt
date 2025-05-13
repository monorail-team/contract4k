package annotation

import base.Contract4kDocSpec
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Contract4kDoc(
    val value: KClass<out Contract4kDocSpec>
)