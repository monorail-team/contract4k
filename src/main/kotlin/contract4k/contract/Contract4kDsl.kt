package contract4k.contract;

import contract4k.condition.ConditionBuilder

interface Contract4KDsl<I, O> {
    fun validatePre(input: I)
    fun validatePost(input: I, result: O)
}

inline fun <I> Contract4KDsl<I, *>.preConditions(
    block: ConditionBuilder.() -> Unit
) {
    val builder = ConditionBuilder()
    builder.block()
    builder.checkAll()
}

inline fun <I> Contract4KDsl<I, *>.postConditions(
    block: ConditionBuilder.() -> Unit
) {
    val builder = ConditionBuilder()
    builder.block()
    builder.checkAll()
}

inline fun <I> Contract4KDsl<I, *>.invariants(
    block: ConditionBuilder.() -> Unit
) {
    val builder = ConditionBuilder()
    builder.block()
    builder.checkAll()
}
