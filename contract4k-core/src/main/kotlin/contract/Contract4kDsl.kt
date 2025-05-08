package contract4k.contract

import contract4k.condition.ConditionBuilder

interface Contract4kDsl<I, O> {
    fun validatePre(input: I)
    fun validatePost(input: I, result: O)
}

inline fun preConditions(block: ConditionBuilder.() -> Unit) {
    val builder = ConditionBuilder()
    ConditionBuilder.current = builder
    builder.block()
    builder.checkAll()
}

inline fun postConditions(block: ConditionBuilder.() -> Unit) {
    val builder = ConditionBuilder()
    ConditionBuilder.current = builder
    builder.block()
    builder.checkAll()
}