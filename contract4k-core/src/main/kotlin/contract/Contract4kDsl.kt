package contract

import condition.ConditionBuilder
import exception.ErrorCode
import exception.ValidationException
import report.ValidationReporter
import config.Contract4kConfig

interface Contract4KDsl<I, O> {

    fun validateInvariant(input: I, output: O) {}

    fun validatePre(input: I) {}

    fun validatePost(input: I, result: O) {}
}

inline fun conditions(
    block: ConditionBuilder.() -> Unit
) {
    val builder = ConditionBuilder()
    builder.block()
    builder.checkAll()
}

inline fun softConditions(
    reporter: ValidationReporter = Contract4kConfig.defaultSortConditionRepoter,
    block: ConditionBuilder.() -> Unit
): Result<Unit> {
    val builder = ConditionBuilder()
    builder.block()
    val result = builder.checkAllSoft()
    if (result.isFailure) {
        val exception = result.exceptionOrNull() as? ValidationException
        exception?.let { ex ->
            val errorCodesForReporter = ex.failedRootConditions.map { vc ->
                ErrorCode(vc.code, vc.message, vc.quickFix)
            }
            reporter.report(errorCodesForReporter)
        }
    }

    return result
}