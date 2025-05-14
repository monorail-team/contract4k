package condition


data class SubConditionDetail(
    val message: String,
    val success: Boolean,
    val quickFix: QuickFix? = null
)


enum class GroupingType {
    NONE,
    ANY_OF,
    ALL_OF
}

data class ValidationCondition(
    val code: String,
    val message: String,
    val predicate: () -> Boolean,
    val level: ValidationLevel,
    val quickFix: QuickFix?,
    val subConditionsDetails: List<SubConditionDetail>? = null,
    val groupingType: GroupingType = GroupingType.NONE
)