package condition

import annotation.Contract4kDsl
import exception.ValidationException

@Contract4kDsl
class SubConditionCollector {
    internal val subPredicates = mutableListOf<Triple<String, () -> Boolean, String?>>()

    infix fun String.meansNested(predicate: () -> Boolean) {
        subPredicates.add(Triple(this, predicate, null))
    }

    infix fun ConditionBuilder.QuickFixHolder.meansNested(predicate: () -> Boolean) {
        this@SubConditionCollector.subPredicates.add(Triple(this.message, predicate, this.fix))
    }
}

@Contract4kDsl
class ConditionBuilder {

    internal val conditions = mutableListOf<ValidationCondition>()

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

        val evaluatedSubConditions = collector.subPredicates.map { (msg, pred, qfMsg) ->
            SubConditionDetail(msg, pred(), qfMsg?.let { QuickFix(it) })
        }
        val overallSuccess: Boolean
        val relevantSubDetailsForReport: List<SubConditionDetail>
        if (collector.subPredicates.isEmpty()) {
            overallSuccess = false
            relevantSubDetailsForReport = emptyList()
        } else {
            overallSuccess = evaluatedSubConditions.any { it.success }
            relevantSubDetailsForReport = if (overallSuccess) {
                evaluatedSubConditions.filter { it.success }
            } else {
                evaluatedSubConditions
            }
        }

        addOrUpdateCondition(
            code = null,
            message = this,
            level = ValidationLevel.ERROR,
            isCodeExplicitlySet = false,
            predicate = { overallSuccess },
            subConditionsDetails = relevantSubDetailsForReport,
            groupingType = GroupingType.ANY_OF
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
        val evaluatedSubConditions = collector.subPredicates.map { (msg, pred, qfMsg) ->
            SubConditionDetail(msg, pred(), qfMsg?.let { QuickFix(it) })
        }
        val overallSuccess: Boolean
        val relevantSubDetailsForReport: List<SubConditionDetail>
        if (collector.subPredicates.isEmpty()) {
            overallSuccess = true // ALL_OF는 비어있으면 true
            relevantSubDetailsForReport = emptyList()
        } else {
            overallSuccess = evaluatedSubConditions.all { it.success }
            relevantSubDetailsForReport = if (!overallSuccess) {
                evaluatedSubConditions.filter { !it.success }
            } else {
                emptyList()
            }
        }

        addOrUpdateCondition(
            code = null,
            message = this,
            level = ValidationLevel.ERROR,
            isCodeExplicitlySet = false,
            predicate = { overallSuccess },
            subConditionsDetails = relevantSubDetailsForReport,
            groupingType = GroupingType.ALL_OF
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

        val evaluatedSubConditions = subPredicates.map { (msg, pred, qfSuggestion) ->
            SubConditionDetail(msg, pred(), qfSuggestion?.let { QuickFix(it) })
        }

        val overallSuccess = when (groupingType) {
            GroupingType.ANY_OF -> evaluatedSubConditions.any { it.success }
            GroupingType.ALL_OF -> evaluatedSubConditions.all { it.success }
            GroupingType.NONE -> true
        }

        val relevantSubDetailsForReport: List<SubConditionDetail> = when {
            overallSuccess && groupingType == GroupingType.ANY_OF ->
                evaluatedSubConditions.filter { it.success }
            !overallSuccess && groupingType == GroupingType.ANY_OF ->
                evaluatedSubConditions
            !overallSuccess && groupingType == GroupingType.ALL_OF ->
                evaluatedSubConditions.filter { !it.success }
            else -> emptyList()
        }
        return overallSuccess to relevantSubDetailsForReport
    }


    private fun addOrUpdateCondition(
        code: String?, // Nullable로 변경
        message: String,
        level: ValidationLevel = ValidationLevel.ERROR, // 기본값을 ERROR로 설정
        quickFix: QuickFix? = null,
        isCodeExplicitlySet: Boolean,
        predicate: () -> Boolean,
        subConditionsDetails: List<SubConditionDetail>? = null,
        groupingType: GroupingType = GroupingType.NONE
    ) {
        conditions += ValidationCondition(
            code = code.takeIf { it?.isNotBlank() == true }, // 빈 문자열 code는 null로 처리
            message = message,
            predicate = predicate,
            level = level,
            quickFix = quickFix,
            subConditionsDetails = subConditionsDetails,
            groupingType = groupingType,
            isCodeExplicitlySet = isCodeExplicitlySet
        )
    }

}