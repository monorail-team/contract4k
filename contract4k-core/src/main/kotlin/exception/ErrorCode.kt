package exception

import condition.QuickFix

data class ErrorCode(
    val code: String? = null,
    val message: String,
    val quickFix: QuickFix? = null,
    val isCodeExplicitlySet: Boolean = false
)