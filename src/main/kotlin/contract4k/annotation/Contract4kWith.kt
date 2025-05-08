package contract4k.annotation

import contract4k.contract.Contract4KDsl
import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class Contract4kWith(
    val value: KClass<out Contract4KDsl<*, *>>
)
