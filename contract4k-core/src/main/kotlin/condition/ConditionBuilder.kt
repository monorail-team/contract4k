package condition

import annotation.Contract4kDsl
import exception.ErrorCode
import exception.ValidationException

/**
 * 계약 조건을 구성하고 검증하는 빌더 클래스입니다.
 * DSL을 통해 사전 조건, 사후 조건, 불변식 등을 정의할 수 있습니다.
 *
 * 사용 예시 (Contract4KDsl 인터페이스를 구현하는 클래스 내부):
 * ```kotlin
 * conditions {
 * "메시지 1" means { /* 조건 람다 1 */ }
 * ("메시지 3" quickFix "빠른 수정 제안") means { /* 조건 람다 3 */ }
 * }
 * ```
 */
@Contract4kDsl
class ConditionBuilder {

    private val conditions = mutableListOf<ValidationCondition>()

    /**
     * 등록된 모든 조건을 검증합니다.
     * ERROR 레벨의 조건 중 하나라도 실패하면 ValidationException을 발생시킵니다.
     * WARNING 레벨의 조건이 실패하면 콘솔에 경고 메시지를 출력합니다.
     */
    fun checkAll() {
        val failedErrors = mutableListOf<ErrorCode>()
        val failedWarnings = mutableListOf<ErrorCode>()

        for ((code, message, quickFix, level, predicate) in conditions) {
            if (!predicate()) {
                val error = ErrorCode(code, message, quickFix)
                when (level) {
                    ValidationLevel.ERROR -> failedErrors += error
                    ValidationLevel.WARNING -> failedWarnings += error
                }
            }
        }

        if (failedErrors.isNotEmpty()) {
            throw ValidationException(failedErrors)
        }

        if (failedWarnings.isNotEmpty()) {
            println("Warning:")
            failedWarnings.forEach { println("- [${it.code}] ${it.message}") }
        }
    }

    /**
     * 등록된 ERROR 레벨 조건들만 검증하여 Result 객체를 반환합니다.
     * ValidationException을 직접 발생시키지 않고, 성공 또는 실패 결과를 반환합니다.
     * @return 조건 검증 성공 시 Result.success(Unit), 실패 시 Result.failure(ValidationException)
     */
    fun checkAllSoft(): Result<Unit> {
        val failedErrors = mutableListOf<ErrorCode>()

        for ((code, message, quickFix, level, predicate) in conditions) {
            if (!predicate() && level == ValidationLevel.ERROR) {
                failedErrors += ErrorCode(code, message, quickFix)
            }
        }

        return if (failedErrors.isEmpty()) {
            Result.success(Unit)
        } else {
            Result.failure(ValidationException(failedErrors))
        }
    }

    /**
     * 주어진 메시지와 조건 람다를 사용하여 계약 조건을 정의합니다. (기본 레벨: ERROR)
     * "메시지"는 조건이 실패했을 때 사용자에게 보여줄 설명이며, 에러 코드 자동 생성에도 사용됩니다.
     * 조건 람다는 Boolean을 반환해야 하며, true이면 조건을 만족한 것입니다.
     *
     * @param predicate 조건 검증 로직을 담은 람다 함수.
     * @receiver 조건 실패 시 메시지 및 에러 코드 생성을 위한 문자열.
     * 사용: `"주문 수량은 0보다 커야 합니다" means { order.quantity > 0 }`
     */
    infix fun String.means(predicate: () -> Boolean) {
        requireThat(
            code = generateCodeFromMessage(this),
            message = this,
            condition = predicate
        )
    }


    /**
     * 주어진 메시지와 조건 람다를 사용하여 ERROR 레벨의 계약 조건을 명시적으로 정의합니다.
     * `means`와 유사하지만, 이 조건은 항상 ERROR 레벨로 처리됩니다.
     *
     * @param predicate 조건 검증 로직을 담은 람다 함수.
     * @receiver 조건 실패 시 메시지 및 에러 코드 생성을 위한 문자열.
     * 사용: `"사용자 ID는 null이 아니어야 합니다" mustBe { userId != null }`
     */
    infix fun String.mustBe(predicate: () -> Boolean) {
        requireThat(
            code = generateCodeFromMessage(this),
            message = this,
            level = ValidationLevel.ERROR,
            condition = predicate
        )
    }

    /**
     * 주어진 메시지와 조건 람다를 사용하여 WARNING 레벨의 계약 조건을 정의합니다.
     * 이 조건이 실패하더라도 ValidationException은 발생하지 않지만, 경고로 보고됩니다.
     *
     * @param predicate 조건 검증 로직을 담은 람다 함수.
     * @receiver 조건 실패 시 메시지 및 에러 코드 생성을 위한 문자열.
     * 사용: `"오래된 API 버전 사용 중입니다" mayBe { apiVersion > 2 }`
     */
    infix fun String.mayBe(predicate: () -> Boolean) {
        requireThat(
            code = generateCodeFromMessage(this),
            message = this,
            level = ValidationLevel.WARNING,
            condition = predicate
        )
    }

    /**
     * 계약 조건 메시지에 대한 빠른 수정 제안(QuickFix)을 연결합니다.
     * 반환된 `QuickFixHolder` 객체에 `means`, `mustBe`, `mayBe` 등을 사용하여 실제 조건을 연결할 수 있습니다.
     *
     * @param fixMessage 조건 실패 시 제공될 빠른 수정 제안 문자열.
     * @receiver 조건 실패 시 메시지 및 에러 코드 생성을 위한 문자열.
     * @return 메시지와 빠른 수정 제안을 담고 있는 QuickFixHolder 객체.
     * 사용: `("잘못된 이메일 형식입니다" quickFix "이메일 주소를 다시 확인해주세요") means { email matchesPattern Patterns.EMAIL }`
     */
    infix fun String.quickFix(fixMessage: String): QuickFixHolder {
        return QuickFixHolder(this, fixMessage)
    }

    /**
     * 메시지와 빠른 수정 제안(QuickFix)을 함께 가지고 있는 홀더 클래스입니다.
     * 이 클래스의 인스턴스에 `means` 조건 정의 함수를 체이닝할 수 있습니다.
     */
    class QuickFixHolder(
        val message: String,
        val fix: String
    )

    /**
     * QuickFixHolder에 ERROR 레벨의 계약 조건을 명시적으로 연결합니다.
     *
     * @param predicate 조건 검증 로직을 담은 람다 함수.
     * @receiver 메시지와 QuickFix 정보를 담고 있는 QuickFixHolder 인스턴스.
     */
    infix fun QuickFixHolder.mustBe(predicate: () -> Boolean) {
        requireThat(
            code = generateCodeFromMessage(this.message),
            message = this.message,
            quickFix = this.fix,
            level = ValidationLevel.ERROR,
            condition = predicate
        )
    }
    /**
     * QuickFixHolder에 실제 계약 조건을 연결합니다. (기본 레벨: ERROR)
     *
     * @param predicate 조건 검증 로직을 담은 람다 함수.
     * @receiver 메시지와 QuickFix 정보를 담고 있는 QuickFixHolder 인스턴스.
     * 사용: `val check = "메시지" quickFix "제안"; check means { 조건 }`
     */
    infix fun QuickFixHolder.means(predicate: () -> Boolean) {
        requireThat(
            code = generateCodeFromMessage(this.message),
            message = this.message,
            quickFix = this.fix,
            level = ValidationLevel.ERROR,
            condition = predicate
        )
    }
    /**
     * QuickFixHolder에 WARNING 레벨의 계약 조건을 연결합니다.
     *
     * @param predicate 조건 검증 로직을 담은 람다 함수.
     * @receiver 메시지와 QuickFix 정보를 담고 있는 QuickFixHolder 인스턴스.
     */
    infix fun QuickFixHolder.mayBe(predicate: () -> Boolean) {
        requireThat(
            code = generateCodeFromMessage(this.message),
            message = this.message,
            quickFix = this.fix,
            level = ValidationLevel.WARNING,
            condition = predicate
        )
    }

    /**
     * 내부적으로 사용되는 함수로, 모든 조건 정의는 이 함수를 통해 ValidationCondition 객체로 변환되어 리스트에 추가됩니다.
     * @param code 에러 코드 (메시지로부터 자동 생성됨).
     * @param message 조건 실패 시 사용자에게 보여줄 메시지.
     * @param quickFix 빠른 수정 제안 (optional).
     * @param level 검증 레벨 (ERROR 또는 WARNING).
     * @param condition 실제 조건 검증 로직을 담은 람다 함수.
     */
    private fun requireThat(
        code: String,
        message: String,
        quickFix: String? = null,
        level: ValidationLevel = ValidationLevel.ERROR,
        condition: () -> Boolean
    ) {
        conditions += ValidationCondition(
            code = code,
            message = message,
            quickFix = quickFix?.let { QuickFix(it) },
            level = level,
            predicate = condition
        )
    }

    /**
     * 메시지 문자열로부터 에러 코드를 생성합니다.
     * 메시지를 대문자로 변환하고, 비 영숫자 문자를 밑줄(_)로 대체합니다.
     * 예: "Order amount must be positive!" -> "ORDER_AMOUNT_MUST_BE_POSITIVE_"
     * @param message 에러 코드를 생성할 기반 메시지 문자열.
     * @return 생성된 에러 코드 문자열.
     */
    private fun generateCodeFromMessage(message: String): String {
        return message
            .trim()
            .uppercase()
            .replace(Regex("[^A-Z0-9]+"), "_")
    }
}
