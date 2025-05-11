package aspect

import annotation.Contract4kWith
import contract.Contract4kDsl
import contract.Contract4kRegistry
import contract.and
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import kotlin.reflect.KClass

@Aspect
class Contract4kAspect {

    @Around("@annotation(annotation.Contract4kWith) && execution(* *(..))")
    fun around(joinPoint: ProceedingJoinPoint): Any? {
        val method = (joinPoint.signature as MethodSignature).method
        val annotation = method.getAnnotation(Contract4kWith::class.java)
        val contractClass = annotation.value as KClass<out Contract4kDsl<Any?, Any?>>
        val contract = contractClass.objectInstance ?: Contract4kRegistry.getOrCreate(contractClass)

        val input = joinPoint.args.let {
            when (it.size) {
                1 -> it[0]
                2 -> it[0] and it[1]
                3 -> it[0] and it[1] and it[2]
                4 -> it[0] and it[1] and it[2] and it[3]
                5 -> it[0] and it[1] and it[2] and it[3] and it[4]
                else -> it.toList()
            }
        }

        contract.validatePre(input)
        val result = joinPoint.proceed()
        contract.validatePost(input, result)

        return result
    }
}