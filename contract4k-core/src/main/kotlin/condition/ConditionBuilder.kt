package condition

import annotation.Contract4kDsl
import exception.ErrorCode
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

    private val conditions = mutableListOf<ValidationCondition>()


    fun checkAll() {
        val failedVCs = mutableListOf<ValidationCondition>()
        val warningVCs = mutableListOf<ValidationCondition>()

        for (vc in conditions) {
            if (!vc.predicate()) {
                when (vc.level) {
                    ValidationLevel.ERROR -> failedVCs.add(vc)
                    ValidationLevel.WARNING -> warningVCs.add(vc)
                }
            }
        }

        if (failedVCs.isNotEmpty()) {
            throw ValidationException(failedVCs)
        }

        if (warningVCs.isNotEmpty()) {
            println("Warning:")
            warningVCs.forEach { vc ->
                print("- ${vc.message}")
                if (vc.quickFix != null) {
                    print(" (빠른 수정: ${vc.quickFix.suggestion})")
                }
                println()
            }
        }
    }

    fun checkAllSoft(): Result<Unit> {
        val failedVCs = conditions.filter { it.level == ValidationLevel.ERROR && !it.predicate() }
        return if (failedVCs.isEmpty()) {
            Result.success(Unit)
        } else {
            Result.failure(ValidationException(failedVCs))
        }
    }


    infix fun String.means(predicate: () -> Boolean) {
        addOrUpdateCondition(
            code = generateCodeFromMessage(this),
            message = this,
            predicate = predicate
        )
    }

    infix fun String.mustBe(predicate: () -> Boolean) {
        addOrUpdateCondition(
            code = generateCodeFromMessage(this),
            message = this,
            level = ValidationLevel.ERROR, // mustBe는 항상 ERROR
            predicate = predicate
        )
    }

    infix fun String.mayBe(predicate: () -> Boolean) {
        addOrUpdateCondition(
            code = generateCodeFromMessage(this),
            message = this,
            level = ValidationLevel.WARNING, // mayBe는 항상 WARNING
            predicate = predicate
        )
    }

    inner class QuickFixHolder(
        val message: String,
        val fix: String
    ) {
        infix fun means(predicate: () -> Boolean) {
            this@ConditionBuilder.addOrUpdateCondition(
                code = generateCodeFromMessage(this.message),
                message = this.message,
                quickFix = QuickFix(this.fix),
                predicate = predicate
            )
        }


        infix fun meansAnyOf(block: SubConditionCollector.() -> Unit) {
            val collector = SubConditionCollector()
            collector.block()

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
                if (overallSuccess) {
                    relevantSubDetailsForReport = evaluatedSubConditions.filter { it.success }
                } else {
                    relevantSubDetailsForReport = evaluatedSubConditions
                }
            }

            this@ConditionBuilder.addOrUpdateCondition(
                code = generateCodeFromMessage(this.message),
                message = this.message,
                quickFix = QuickFix(this.fix),
                predicate = { overallSuccess },
                subConditionsDetails = relevantSubDetailsForReport,
                groupingType = GroupingType.ANY_OF
            )
        }


        infix fun meansAllOf(block: SubConditionCollector.() -> Unit) {
            val collector = SubConditionCollector()
            collector.block()

            val evaluatedSubConditions = collector.subPredicates.map { (msg, pred, qfMsg) ->
                SubConditionDetail(msg, pred(), qfMsg?.let { QuickFix(it) })
            }

            val overallSuccess: Boolean
            val relevantSubDetailsForReport: List<SubConditionDetail>

            if (collector.subPredicates.isEmpty()) {
                overallSuccess = true
                relevantSubDetailsForReport = emptyList()
            } else {
                overallSuccess = evaluatedSubConditions.all { it.success }
                if (!overallSuccess) {
                    relevantSubDetailsForReport = evaluatedSubConditions.filter { !it.success }
                } else {
                    relevantSubDetailsForReport = emptyList()

                }
            }

            this@ConditionBuilder.addOrUpdateCondition(
                code = generateCodeFromMessage(this.message),
                message = this.message,
                quickFix = QuickFix(this.fix),
                predicate = { overallSuccess },
                subConditionsDetails = relevantSubDetailsForReport,
                groupingType = GroupingType.ALL_OF
            )
        }
    }

    infix fun String.quickFix(fixMessage: String): QuickFixHolder {
        return QuickFixHolder(this, fixMessage)
    }



    infix fun String.meansAnyOf(block: SubConditionCollector.() -> Unit) {
        val collector = SubConditionCollector()
        collector.block()

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
            if (overallSuccess) {
                relevantSubDetailsForReport = evaluatedSubConditions.filter { it.success }
            } else {
                relevantSubDetailsForReport = evaluatedSubConditions
            }
        }

        addOrUpdateCondition(
            code = generateCodeFromMessage(this),
            message = this,
            predicate = { overallSuccess },
            subConditionsDetails = relevantSubDetailsForReport,
            groupingType = GroupingType.ANY_OF
        )
    }

    infix fun String.meansAllOf(block: SubConditionCollector.() -> Unit) {
        val collector = SubConditionCollector()
        collector.block()

        val evaluatedSubConditions = collector.subPredicates.map { (msg, pred, qfMsg) ->
            SubConditionDetail(msg, pred(), qfMsg?.let { QuickFix(it) })
        }

        val overallSuccess: Boolean
        val relevantSubDetailsForReport: List<SubConditionDetail>

        if (collector.subPredicates.isEmpty()) {
            overallSuccess = true
            relevantSubDetailsForReport = emptyList()
        } else {
            overallSuccess = evaluatedSubConditions.all { it.success }
            if (!overallSuccess) {
                relevantSubDetailsForReport = evaluatedSubConditions.filter { !it.success }
            } else {
                relevantSubDetailsForReport = emptyList()
            }
        }

        addOrUpdateCondition(
            code = generateCodeFromMessage(this),
            message = this,
            predicate = { overallSuccess },
            subConditionsDetails = relevantSubDetailsForReport,
            groupingType = GroupingType.ALL_OF
        )
    }

    private fun addOrUpdateCondition(
        code: String,
        message: String,
        level: ValidationLevel = ValidationLevel.ERROR,
        quickFix: QuickFix? = null,
        predicate: () -> Boolean,
        subConditionsDetails: List<SubConditionDetail>? = null,
        groupingType: GroupingType = GroupingType.NONE
    ) {
        conditions += ValidationCondition(
            code = code,
            message = message,
            predicate = predicate,
            level = level,
            quickFix = quickFix,
            subConditionsDetails = subConditionsDetails,
            groupingType = groupingType
        )
    }


    private fun generateCodeFromMessage(message: String): String {
        return message
            .trim()
            .uppercase()
            .replace(Regex("[^A-Z0-9ㄱ-ㅎㅏ-ㅣ가-힣]+"), "_")
            .replace(Regex("_$"), "")
    }
}
