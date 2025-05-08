package contract4k.condition

import java.util.regex.Pattern

/**
 * 널(null) 검증
 * 사용 예시:
 *   notNull(order.customer, "customer")
 */
fun <T> ConditionBuilder.notNull(value: T?, name: String = "value") {
    requireThat(
        code = "${name.uppercase()}_NOT_NULL",
        message = "$name must not be null"
    ) { value != null }
}

/**
 * 숫자 범위 검사
 * 사용 예시:
 *   inRange(amount, 1, 1000, "amount")
 */
fun ConditionBuilder.inRange(
    value: Number,
    min: Number,
    max: Number,
    name: String = "value"
) {
    requireThat(
        code = "${name.uppercase()}_IN_RANGE",
        message = "$name must be between $min and $max"
    ) { value.toDouble() in min.toDouble()..max.toDouble() }
}

/**
 * 컬렉션 또는 문자열 비어있지 않음 검사
 * 사용 예시:
 *   notEmpty(items, "items")
 */
fun ConditionBuilder.notEmpty(
    collection: Collection<*>?,
    name: String = "collection"
) {
    requireThat(
        code = "${name.uppercase()}_NOT_EMPTY",
        message = "$name must not be empty"
    ) { !collection.isNullOrEmpty() }
}
fun ConditionBuilder.notEmpty(
    str: String?,
    name: String = "string"
) {
    requireThat(
        code = "${name.uppercase()}_NOT_EMPTY",
        message = "$name must not be empty"
    ) { !str.isNullOrBlank() }
}

/**
 * 정규표현식 매칭 검사
 * 사용 예시:
 *   matchesRegex(email, EMAIL_REGEX, "email")
 */
fun ConditionBuilder.matchesRegex(
    str: String?,
    regex: Pattern,
    name: String = "value"
) {
    requireThat(
        code = "${name.uppercase()}_PATTERN_MISMATCH",
        message = "$name must match pattern ${regex.pattern()}"
    ) { str != null && regex.matcher(str).matches() }
}
