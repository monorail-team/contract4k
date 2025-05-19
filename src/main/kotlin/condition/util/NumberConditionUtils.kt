package condition.util

/**
 * 숫자가 주어진 범위 내에 있는지 확인합니다. (IntRange 또는 ClosedFloatingPointRange<Double>)
 * @param range 확인할 범위.
 * @receiver 대상 숫자.
 * @return 범위 내에 있으면 true, 아니면 false.
 * 사용: `10 between (0..100)`
 * `10.5 between (10.0..11.0)`
 */
infix fun Number.between(range: IntRange): Boolean = this.toDouble() in range.start.toDouble()..range.endInclusive.toDouble()
infix fun Number.between(range: ClosedFloatingPointRange<Double>): Boolean = this.toDouble() in range

/**
 * 숫자가 주어진 범위 밖에 있는지 확인합니다.
 * @param range 확인할 범위.
 * @receiver 대상 숫자.
 * @return 범위 밖에 있으면 true, 아니면 false.
 * 사용: `101 isOutside (0..100)`
 */
infix fun Number.isOutside(range: IntRange): Boolean = this.toDouble() !in range.start.toDouble()..range.endInclusive.toDouble()
infix fun Number.isOutside(range: ClosedFloatingPointRange<Double>): Boolean = this.toDouble() !in range


/**
 * 두 숫자가 주어진 허용 오차 내에서 가까운지 확인합니다. (부동 소수점 비교 시 유용)
 * @param other 비교할 다른 숫자.
 * @param tolerance 허용 오차.
 * @receiver 대상 숫자.
 * @return 허용 오차 내에서 가까우면 true, 아니면 false.
 * 사용: `3.1415 isCloseTo (3.14, tolerance = 0.01)`
 */
infix fun Number.isCloseTo(pair: Pair<Number, Double>): Boolean {
    val (other, tolerance) = pair
    return kotlin.math.abs(this.toDouble() - other.toDouble()) <= tolerance
}


/**
 * 두 숫자가 주어진 허용 오차 내에서 가깝지 않은지 확인합니다.
 * @param other 비교할 다른 숫자.
 * @param tolerance 허용 오차.
 * @receiver 대상 숫자.
 * @return 허용 오차 내에서 가깝지 않으면 true, 아니면 false.
 * 사용: `5.0 isNotCloseTo (10.0, tolerance = 1.0)`
 */
infix fun Number.isNotCloseTo(pair: Pair<Number, Double>): Boolean {
    val (other, tolerance) = pair
    return kotlin.math.abs(this.toDouble() - other.toDouble()) > tolerance
}


/**
 * 숫자가 양수인지 확인합니다.
 * 사용: `count is positive`
 */
infix fun Number.`is`(marker: positive): Boolean = this.toDouble() > 0
/**
 * 숫자가 양수가 아닌지 (0 또는 음수인지) 확인합니다.
 * 사용: `balance isNot positive`
 */
infix fun Number.isNot(marker: positive): Boolean = this.toDouble() <= 0

/**
 * 숫자가 음수인지 확인합니다.
 * 사용: `temperature is negative`
 */
infix fun Number.`is`(marker: negative): Boolean = this.toDouble() < 0
/**
 * 숫자가 음수가 아닌지 (0 또는 양수인지) 확인합니다.
 * 사용: `score isNot negative`
 */
infix fun Number.isNot(marker: negative): Boolean = this.toDouble() >= 0

/**
 * 숫자가 0인지 확인합니다.
 * 사용: `remainingStock is zero`
 */
infix fun Number.`is`(marker: zero): Boolean = this.toDouble() == 0.0
/**
 * 숫자가 0이 아닌지 확인합니다.
 * 사용: `changeAmount isNot zero`
 */
infix fun Number.isNot(marker: zero): Boolean = this.toDouble() != 0.0
