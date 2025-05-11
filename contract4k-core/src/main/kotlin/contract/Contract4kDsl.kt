package contract;

import condition.ConditionBuilder

interface Contract4KDsl<I, O> {

    fun validateInvariant(input: I, output: O) {}

    fun validatePre(input: I) {}

    fun validatePost(input: I, result: O) {}
}

inline fun <I> Contract4KDsl<I, *>.conditions(
    block: ConditionBuilder.() -> Unit
) {
    val builder = ConditionBuilder()
    builder.block()
    builder.checkAll()
}

inline fun <I> Contract4KDsl<I, *>.softConditions(
    block: ConditionBuilder.() -> Unit
): Result<Unit> {
    val builder = ConditionBuilder()
    builder.block()
    return builder.checkAllSoft()
}
