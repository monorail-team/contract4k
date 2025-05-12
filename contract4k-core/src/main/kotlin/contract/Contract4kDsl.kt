package contract;

import condition.ConditionBuilder
import exception.ValidationException
import report.ConsoleValidationReporter
import report.ValidationReporter

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
    reporter: ValidationReporter = ConsoleValidationReporter,
    block: ConditionBuilder.() -> Unit
): Result<Unit> {
    val builder = ConditionBuilder()
    builder.block()
    val result = builder.checkAllSoft()

    if (result.isFailure) {
        val exception = result.exceptionOrNull() as? ValidationException
        exception?.failures?.let { reporter.report(it) }
    }

    return result
}
