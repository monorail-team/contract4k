package report

import exception.ErrorCode

object ConsoleValidationReporter : ValidationReporter {

    override fun report(failures: List<ErrorCode>) {
        println("Validation failed with ${failures.size} errors:")
        failures.forEach {
            println("- ${it.message}" + (it.quickFix?.let { qf -> " (QuickFix: $qf)" } ?: ""))
        }
    }
}
