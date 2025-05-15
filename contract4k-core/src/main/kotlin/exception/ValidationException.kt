package exception

import condition.ValidationCondition
import condition.GroupingType


class ValidationException(
    val failedRootConditions: List<ValidationCondition>
) : RuntimeException() {

    override val message: String = buildDetailedValidationMessage()

    private fun buildDetailedValidationMessage(): String {
        val builder = StringBuilder()
        val header = if (failedRootConditions.size > 1) {
            "Validation failed with ${failedRootConditions.size} errors:\n"
        } else if (failedRootConditions.isNotEmpty()) {
            "Validation failed with 1 error:\n"
        } else {
            "Validation succeeded (no errors)."
        }
        builder.append(header)

        failedRootConditions.forEach { vc ->
            val codePrefix = if (vc.isCodeExplicitlySet) "[${vc.code}] " else ""
            builder.append("- ${codePrefix}${vc.message}")
            if (vc.quickFix != null) {
                builder.append(" (빠른 수정: ${vc.quickFix.suggestion})")
            }
            builder.append("\n")

            vc.subConditionsDetails?.let { details ->
                when (vc.groupingType) {
                    GroupingType.ANY_OF -> {
                        if (!vc.predicate()) {
                            details.forEach { sub ->
                                builder.append("    - \"${sub.message}\" (${if (sub.success) "성공" else "실패"})\n")
                            }
                        }
                    }
                    GroupingType.ALL_OF -> {
                        if (!vc.predicate()) {
                            details.filter { !it.success }.forEach { sub ->
                                builder.append("    - \"${sub.message}\"\n")
                            }
                        }
                    }
                    GroupingType.NONE -> {

                    }
                }
            }
        }
        return builder.toString().trimEnd()
    }
}