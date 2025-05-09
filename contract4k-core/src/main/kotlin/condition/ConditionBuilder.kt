package condition

import annotation.Contract4kDsl

@Contract4kDsl
class ConditionBuilder {
    companion object {
        @PublishedApi
        internal lateinit var current: ConditionBuilder
    }

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
            println("⚠️ Warning(s):")
            failedWarnings.forEach { println("- [${it.code}] ${it.message}") }
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
        current.requireThat(
            code = generateCodeFromMessage(this),
            message = this,
            level = ValidationLevel.ERROR,
            condition = predicate
        )
    }

    infix fun String.mayBe(predicate: () -> Boolean) {
        current.requireThat(
            code = generateCodeFromMessage(this),
            message = this,
            level = ValidationLevel.WARNING,
            condition = predicate
        )
    }

    infix fun String.quickFix(fixMessage: String): QuickFixHolder {
        return QuickFixHolder(this, fixMessage)
    }

    infix fun QuickFixHolder.means(predicate: () -> Boolean) {
        current.requireThat(
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

    class QuickFixHolder(
        val message: String,
        val fix: String
    )
}