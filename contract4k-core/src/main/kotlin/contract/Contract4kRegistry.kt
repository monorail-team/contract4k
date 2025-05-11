package contract

import kotlin.reflect.KClass

object Contract4kRegistry {
    private val cache = mutableMapOf<KClass<out Contract4KDsl<*, *>>, Contract4KDsl<*, *>>()

    fun <T : Any?, R : Any?> getOrCreate(clazz: KClass<out Contract4KDsl<T, R>>): Contract4KDsl<T, R> {
        return cache.getOrPut(clazz) {
            clazz.java.getDeclaredConstructor().newInstance()
        } as Contract4KDsl<T, R>
    }
}