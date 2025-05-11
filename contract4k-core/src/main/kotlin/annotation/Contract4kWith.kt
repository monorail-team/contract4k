package annotation

import contract.Contract4KDsl
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Contract4kWith(
    val value: KClass<out Contract4KDsl<*, *>>
)
