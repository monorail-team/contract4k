package report

import exception.ErrorCode

object ConsoleValidationReporter : ValidationReporter {

    override fun report(failures: List<ErrorCode>) {
        val plural = if (failures.size == 1) "warning" else "warnings"
        println("Validation resulted in ${failures.size} $plural:")
        failures.forEach { errorCode ->

            val codePrefix = if (errorCode.isCodeExplicitlySet && !errorCode.code.isNullOrBlank()) {
                "[${errorCode.code}] "
            } else {
                ""
            }

            val quickFixSuffix = errorCode.quickFix?.let { qf -> " (QuickFix: ${qf.suggestion})" } ?: ""
            println("- ${codePrefix}${errorCode.message}${quickFixSuffix}")
        }
    }
}
