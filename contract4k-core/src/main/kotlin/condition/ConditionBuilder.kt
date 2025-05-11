package condition

import annotation.Contract4kDsl
import exception.ErrorCode
import exception.ValidationException

@Contract4kDsl
class ConditionBuilder {

    private val conditions = mutableListOf<ValidationCondition>()

    fun checkAll() {
        val failedErrors = mutableListOf<ErrorCode>()
        val failedWarnings = mutableListOf<ErrorCode>()

        for ((code, message, quickFix, level, predicate) in conditions) {
            if (!predicate()) {
                val error = ErrorCode(code, message, quickFix)
                when (level) {
                    ValidationLevel.ERROR -> failedErrors += error
                    ValidationLevel.WARNING -> failedWarnings += error
                }
            }
        }

        if (failedErrors.isNotEmpty()) {
            throw ValidationException(failedErrors)
        }

        if (failedWarnings.isNotEmpty()) {
            println("Warning:")
            failedWarnings.forEach { println("- [${it.code}] ${it.message}") }
        }
    }

    fun checkAllSoft(): Result<Unit> {
        val failedErrors = mutableListOf<ErrorCode>()

        for ((code, message, quickFix, level, predicate) in conditions) {
            if (!predicate() && level == ValidationLevel.ERROR) {
                failedErrors += ErrorCode(code, message, quickFix)
            }
        }

        return if (failedErrors.isEmpty()) {
            Result.success(Unit)
        } else {
            Result.failure(ValidationException(failedErrors))
        }
    }

    infix fun String.means(predicate: () -> Boolean) {
        requireThat(
            code = generateCodeFromMessage(this),
            message = this,
            condition = predicate
        )
    }

    infix fun String.mustBe(predicate: () -> Boolean) {
        requireThat(
            code = generateCodeFromMessage(this),
            message = this,
            level = ValidationLevel.ERROR,
            condition = predicate
        )
    }

    infix fun String.mayBe(predicate: () -> Boolean) {
        requireThat(
            code = generateCodeFromMessage(this),
            message = this,
            level = ValidationLevel.WARNING,
            condition = predicate
        )
    }

    infix fun String.quickFix(fixMessage: String): QuickFixHolder {
        return QuickFixHolder(this, fixMessage)
    }

    class QuickFixHolder(
        val message: String,
        val fix: String
    )

    infix fun QuickFixHolder.means(predicate: () -> Boolean) {
        requireThat(
            code = generateCodeFromMessage(this.message),
            message = this.message,
            quickFix = this.fix,
            level = ValidationLevel.ERROR,
            condition = predicate
        )
    }

    private fun requireThat(
        code: String,
        message: String,
        quickFix: String? = null,
        level: ValidationLevel = ValidationLevel.ERROR,
        condition: () -> Boolean
    ) {
        conditions += ValidationCondition(
            code = code,
            message = message,
            quickFix = quickFix?.let { QuickFix(it) },
            level = level,
            predicate = condition
        )
    }

    private fun generateCodeFromMessage(message: String): String {
        return message
            .trim()
            .uppercase()
            .replace(Regex("[^A-Z0-9]+"), "_")
    }
}
