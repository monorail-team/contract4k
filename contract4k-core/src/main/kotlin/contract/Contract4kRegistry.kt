package contract

import kotlin.reflect.KClass

object Contract4kRegistry {
    private val cache = mutableMapOf<KClass<out Contract4kDsl<*, *>>, Contract4kDsl<*, *>>()

    fun <T : Any?, R : Any?> getOrCreate(clazz: KClass<out Contract4kDsl<T, R>>): Contract4kDsl<T, R> {
        return cache.getOrPut(clazz) {
            clazz.java.getDeclaredConstructor().newInstance()
        } as Contract4kDsl<T, R>
    }
}