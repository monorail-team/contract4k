package contract4k.annotation

import contract4k.contract.Contract4kDsl
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Contract4kWith(
    val value: KClass<out Contract4kDsl<*, *>>
)
