package contract4k.aspect

import contract4k.annotation.Contract4kWith
import contract4k.contract.Contract4kDsl
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature

@Aspect
class Contract4kAspect {

    @Around("@annotation(contract4k.annotation.Contract4kWith) && execution(* *(..))")
    fun around(joinPoint: ProceedingJoinPoint): Any? {
        val method = (joinPoint.signature as MethodSignature).method
        val annotation = method.getAnnotation(Contract4kWith::class.java)
        val contractClass = annotation.value.java
        val contract = contractClass.getDeclaredConstructor().newInstance() as Contract4kDsl<Any?, Any?>

        val input = joinPoint.args.let {
            when (it.size) {
                1 -> it[0]
                2 -> it[0] to it[1]
                else -> it.toList()
            }
        }

        contract.validatePre(input)
        val result = joinPoint.proceed()
        contract.validatePost(input, result)

        return result
    }
}