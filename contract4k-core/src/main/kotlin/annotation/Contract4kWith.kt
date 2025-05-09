package annotation

import contract.Contract4kDsl
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Contract4kWith(
    val value: KClass<out Contract4kDsl<*, *>>
)
