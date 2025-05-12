package exception

import condition.QuickFix

data class ErrorCode(
    val code: String,
    val message: String,
    val quickFix: QuickFix? = null
)