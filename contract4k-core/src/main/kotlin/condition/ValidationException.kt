package condition

class ValidationException(
    failures: List<ErrorCode>
) : RuntimeException(
    "Validation 실패:\n${failures.joinToString("\n") { "- ${it.code}: ${it.message}" }}"
)