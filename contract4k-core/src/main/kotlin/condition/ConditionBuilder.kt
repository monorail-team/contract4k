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

    // 1. checkAll 및 checkAllSoft 수정: ValidationException에 List<ValidationCondition> 전달
    fun checkAll() {
        val failedVCs = mutableListOf<ValidationCondition>()
        val warningVCs = mutableListOf<ValidationCondition>() // 경고도 ValidationCondition으로 수집

        for (vc in conditions) {
            if (!vc.predicate()) { // 전체 조건(그룹 포함) 실패 시
                when (vc.level) {
                    ValidationLevel.ERROR -> failedVCs.add(vc)
                    ValidationLevel.WARNING -> warningVCs.add(vc)
                }
            }
        }

        if (failedVCs.isNotEmpty()) {
            throw ValidationException(failedVCs) // ValidationCondition 리스트 전달
        }

        if (warningVCs.isNotEmpty()) {
            println("Warning:") // 이 부분도 ValidationReporter를 사용하도록 확장 가능
            warningVCs.forEach { vc ->
                // 간단히 상위 메시지만 출력하거나, ValidationException처럼 상세 출력 가능
                print("- ${vc.message}")
                if (vc.quickFix != null) {
                    print(" (빠른 수정: ${vc.quickFix.suggestion})")
                }
                println()
                // 경고에 대한 하위 조건 출력은 선택 사항
            }
        }
    }

    fun checkAllSoft(): Result<Unit> {
        val failedVCs = conditions.filter { it.level == ValidationLevel.ERROR && !it.predicate() }
        return if (failedVCs.isEmpty()) {
            Result.success(Unit)
        } else {
            Result.failure(ValidationException(failedVCs)) // ValidationCondition 리스트 전달
        }
    }

    // 2. 일반 조건 정의 함수 (means, mustBe, mayBe)는 addOrUpdateCondition 호출
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

    // 3. QuickFixHolder를 inner class로 변경 (ConditionBuilder의 멤버 접근 용이)
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

        // QuickFixHolder에 대한 meansAnyOf
        infix fun meansAnyOf(block: SubConditionCollector.() -> Unit) {
            val collector = SubConditionCollector()
            collector.block() // 블록 실행 (내부에서 "msg".meansNested 또는 QuickFixHolder.meansNested 호출)

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
                if (overallSuccess) { // AnyOf 성공 시: 성공에 기여한 조건들만 (요구사항)
                    relevantSubDetailsForReport = evaluatedSubConditions.filter { it.success }
                } else { // AnyOf 실패 시: 모든 하위 조건들 (실패 원인 파악용 - 사용자 예시)
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

        // QuickFixHolder에 대한 meansAllOf
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
                if (!overallSuccess) { // AllOf 실패 시: 실패에 기여한 조건들만 (요구사항)
                    relevantSubDetailsForReport = evaluatedSubConditions.filter { !it.success }
                } else { // AllOf 성공 시: (선택) 모든 하위 조건 (모두 성공) 또는 빈 리스트
                    relevantSubDetailsForReport = emptyList() // 성공 시에는 하위 상세 불필요 (요구사항에 따름)
                    // 또는 evaluatedSubConditions (모든 성공한 하위 조건)
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
    // String.quickFix는 QuickFixHolder(inner class) 인스턴스 반환
    infix fun String.quickFix(fixMessage: String): QuickFixHolder {
        return QuickFixHolder(this, fixMessage)
    }


    // 4. meansAnyOf / meansAllOf (String 수신자) 수정
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
            if (overallSuccess) { // AnyOf 성공 시: 성공에 기여한 조건들
                relevantSubDetailsForReport = evaluatedSubConditions.filter { it.success }
            } else { // AnyOf 실패 시: 모든 하위 조건들
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
            if (!overallSuccess) { // AllOf 실패 시: 실패에 기여한 조건들
                relevantSubDetailsForReport = evaluatedSubConditions.filter { !it.success }
            } else { // AllOf 성공 시
                relevantSubDetailsForReport = emptyList() // 성공 시 하위 상세 불필요
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

    // 5. requireThat 대신 addOrUpdateCondition 사용 (ValidationCondition 구조 변경 반영)
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
            code = code, // 코드는 여전히 생성 (필요 없다면 제거 가능)
            message = message,
            predicate = predicate,
            level = level,
            quickFix = quickFix,
            subConditionsDetails = subConditionsDetails,
            groupingType = groupingType
        )
    }

    // 6. generateCodeFromMessage (코드 부분 제거 원하면 수정 또는 사용 안 함)
    private fun generateCodeFromMessage(message: String): String {
        // 코드 부분이 출력에 필요 없다면, 여기서 빈 문자열을 반환하거나 아예 사용하지 않을 수 있음.
        // 또는, 유니크 ID 생성 등으로 대체 가능.
        // 현재는 메시지 기반 코드 생성 유지.
        return message
            .trim()
            .uppercase()
            .replace(Regex("[^A-Z0-9ㄱ-ㅎㅏ-ㅣ가-힣]+"), "_")
            .replace(Regex("_$"), "")
    }
}
