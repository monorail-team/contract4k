package contract4k.condition

data class ErrorCode(
    val code: String,
    val message: String,
    val quickFix: QuickFix? = null
)