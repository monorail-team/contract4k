package condition

data class ValidationCondition(
    val code: String,
    val message: String,
    val quickFix: QuickFix?,
    val level: ValidationLevel,
    val predicate: () -> Boolean
)