package report

import exception.ErrorCode
import org.slf4j.Logger

class LogbackValidationReporter(private val logger: Logger) : ValidationReporter {

    override fun report(failures: List<ErrorCode>) {
        logger.warn("Validation failed with ${failures.size} error(s):")
        failures.forEach {
            logger.warn("- [${it.code}] ${it.message}" + (it.quickFix?.let { fix -> " (QuickFix: $fix)" } ?: ""))
        }
    }
}
