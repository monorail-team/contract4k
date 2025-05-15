package condition

import annotation.Contract4kDsl
import exception.ValidationException

@Contract4kDsl
class ConditionBuilder {

    internal val conditions = mutableListOf<ValidationCondition>()

    private fun addOrUpdateCondition(
        code: String?,
        message: String,
        level: ValidationLevel = ValidationLevel.ERROR,
        quickFix: QuickFix? = null,
        isCodeExplicitlySet: Boolean,
        predicate: () -> Boolean,
        subConditionsDetails: List<SubConditionDetail>? = null,
        groupingType: GroupingType = GroupingType.NONE
    ) {
        conditions += ValidationCondition(
            code = code.takeIf { it?.isNotBlank() == true },
            message = message,
            predicate = predicate,
            level = level,
            quickFix = quickFix,
            subConditionsDetails = subConditionsDetails,
            groupingType = groupingType,
            isCodeExplicitlySet = isCodeExplicitlySet
        )
    }

    fun checkAll() {
        val failedErrorVCs = conditions.filter {
            it.level == ValidationLevel.ERROR && !it.predicate()
        }

        if (failedErrorVCs.isNotEmpty()) {
            throw ValidationException(failedErrorVCs)
        }
    }

    fun checkAllSoft(): Result<Unit> {
        val failedErrorVCs = conditions.filter { it.level == ValidationLevel.ERROR && !it.predicate() }
        return if (failedErrorVCs.isEmpty()) {
            Result.success(Unit)
        } else {
            Result.failure(ValidationException(failedErrorVCs))
        }
    }

    @Contract4kDsl
    class SubConditionCollector {
        internal val subPredicates = mutableListOf<Triple<String, () -> Boolean, String?>>()

        infix fun String.means(predicate: () -> Boolean) {
            subPredicates.add(Triple(this, predicate, null))
        }

        infix fun ConditionBuilder.QuickFixHolder.means(predicate: () -> Boolean) {
            this@SubConditionCollector.subPredicates.add(Triple(this.message, predicate, this.fix))
        }
    }

    infix fun String.means(predicate: () -> Boolean) {
        addOrUpdateCondition(
            code = null,
            message = this,
            level = ValidationLevel.ERROR,
            isCodeExplicitlySet = false,
            predicate = predicate
        )
    }

    fun means(code: String, message: String, predicate: () -> Boolean) {
        addOrUpdateCondition(
            code = code,
            message = message,
            level = ValidationLevel.ERROR,
            isCodeExplicitlySet = true,
            predicate = predicate
        )
    }

    inner class QuickFixHolder(
        val message: String,
        val fix: String,
        val explicitCode: String? = null,
        val isCodeExplicitlySetForCode: Boolean = false
    ) {
        infix fun means(predicate: () -> Boolean) {
            this@ConditionBuilder.addOrUpdateCondition(
                code = this.explicitCode,
                message = this.message,
                level = ValidationLevel.ERROR,
                quickFix = QuickFix(this.fix),
                predicate = predicate,
                isCodeExplicitlySet = this.isCodeExplicitlySetForCode
            )
        }

        infix fun meansAnyOf(block: SubConditionCollector.() -> Unit) {
            val collector = SubConditionCollector().apply(block)
            val (overallSuccess, reportDetails) = evaluateGroupCondition(collector.subPredicates, GroupingType.ANY_OF)
            this@ConditionBuilder.addOrUpdateCondition(
                code = this.explicitCode,
                message = this.message,
                level = ValidationLevel.ERROR,
                quickFix = QuickFix(this.fix),
                predicate = { overallSuccess },
                subConditionsDetails = reportDetails,
                groupingType = GroupingType.ANY_OF,
                isCodeExplicitlySet = this.isCodeExplicitlySetForCode
            )
        }
        infix fun meansAllOf(block: SubConditionCollector.() -> Unit) {
            val collector = SubConditionCollector().apply(block)
            val (overallSuccess, reportDetails) = evaluateGroupCondition(collector.subPredicates, GroupingType.ALL_OF)
            this@ConditionBuilder.addOrUpdateCondition(
                code = this.explicitCode, // 이전 코드에서 generateCodeFromMessage 사용 부분 수정
                message = this.message,
                level = ValidationLevel.ERROR,
                quickFix = QuickFix(this.fix),
                predicate = { overallSuccess },
                subConditionsDetails = reportDetails,
                groupingType = GroupingType.ALL_OF,
                isCodeExplicitlySet = this.isCodeExplicitlySetForCode
            )
        }
    }
    infix fun String.quickFix(fixMessage: String): QuickFixHolder {
        return QuickFixHolder(
            message = this,
            fix = fixMessage,
            explicitCode = null,
            isCodeExplicitlySetForCode = false
        )
    }

    fun quickFix(code: String, message: String, fixMessage: String): QuickFixHolder {
        return QuickFixHolder(
            message = message,
            fix = fixMessage,
            explicitCode = code,
            isCodeExplicitlySetForCode = true
        )
    }


    infix fun String.meansAnyOf(block: SubConditionCollector.() -> Unit) {
        val collector = SubConditionCollector().apply(block)
        val (overall, details) = evaluateGroupCondition(collector.subPredicates, GroupingType.ANY_OF)
        addOrUpdateCondition(
            code = null,
            message = this,
            predicate = { overall },
            subConditionsDetails = details,
            groupingType = GroupingType.ANY_OF,
            isCodeExplicitlySet = false
        )
    }

    fun meansAnyOf(code: String, message: String, block: SubConditionCollector.() -> Unit) {
        val collector = SubConditionCollector().apply(block)
        val (overallSuccess, reportDetails) = evaluateGroupCondition(collector.subPredicates, GroupingType.ANY_OF)
        addOrUpdateCondition(
            code = code,
            message = message,
            level = ValidationLevel.ERROR,
            isCodeExplicitlySet = true,
            predicate = { overallSuccess },
            subConditionsDetails = reportDetails,
            groupingType = GroupingType.ANY_OF
        )
    }

    infix fun String.meansAllOf(block: SubConditionCollector.() -> Unit) {
        val collector = SubConditionCollector().apply(block)
        val (overall, details) = evaluateGroupCondition(collector.subPredicates, GroupingType.ALL_OF)
        addOrUpdateCondition(
            code = null,
            message = this,
            predicate = { overall },
            subConditionsDetails = details,
            groupingType = GroupingType.ALL_OF,
            isCodeExplicitlySet = false
        )
    }

    fun meansAllOf(code: String, message: String, block: SubConditionCollector.() -> Unit) {
        val collector = SubConditionCollector().apply(block)
        val (overallSuccess, reportDetails) = evaluateGroupCondition(collector.subPredicates, GroupingType.ALL_OF)
        addOrUpdateCondition(
            code = code,
            message = message,
            level = ValidationLevel.ERROR,
            isCodeExplicitlySet = true,
            predicate = { overallSuccess },
            subConditionsDetails = reportDetails,
            groupingType = GroupingType.ALL_OF
        )
    }

    private fun evaluateGroupCondition(
        subPredicates: List<Triple<String, () -> Boolean, String?>>,
        groupingType: GroupingType
    ): Pair<Boolean, List<SubConditionDetail>> {
        if (subPredicates.isEmpty()) {
            return when (groupingType) {
                GroupingType.ANY_OF -> false to emptyList()
                GroupingType.ALL_OF -> true to emptyList()
                GroupingType.NONE -> true to emptyList()
            }
        }
        val evaluated = subPredicates.map { (msg, pred, qf) ->
            SubConditionDetail(msg, pred(), qf?.let { QuickFix(it) })
        }
        val overall = when (groupingType) {
            GroupingType.ANY_OF -> evaluated.any { it.success }
            GroupingType.ALL_OF -> evaluated.all { it.success }
            GroupingType.NONE -> true
        }
        val details = when {
            groupingType == GroupingType.ANY_OF && overall -> evaluated.filter { it.success }
            groupingType == GroupingType.ANY_OF && !overall -> evaluated
            groupingType == GroupingType.ALL_OF && !overall -> evaluated.filter { !it.success }
            else -> emptyList()
        }
        return overall to details
    }
}