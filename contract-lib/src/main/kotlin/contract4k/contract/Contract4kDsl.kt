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

inline fun <I, O> contract(
    contract: Contract4kDsl<I, O>,
    input: I,
    block: (I) -> O
): O {

    // 사전 조건 검증
    contract.validatePre(input)

    // 본 로직 실행
    val result = block(input)

    // 사후 조건 검증
    contract.validatePost(input, result)

    return result
}