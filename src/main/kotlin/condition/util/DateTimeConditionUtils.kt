package condition.util

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.Temporal


/**
 * LocalDate 객체가 주어진 날짜 범위 (시작일, 종료일 포함) 내에 있는지 확인합니다.
 * @param pair 시작일과 종료일을 담은 Pair.
 * @receiver 대상 날짜.
 * @return 범위 내에 있으면 true, 아니면 false.
 * 사용: `LocalDate.now() between (startDate to endDate)`
 */
infix fun LocalDate.between(pair: Pair<LocalDate, LocalDate>): Boolean {
    val (start, end) = pair
    return !this.isBefore(start) && !this.isAfter(end)
}

/**
 * LocalDate 객체가 주어진 날짜 범위 (시작일, 종료일 포함) 밖에 있는지 확인합니다.
 * @param pair 시작일과 종료일을 담은 Pair.
 * @receiver 대상 날짜.
 * @return 범위 밖에 있으면 true, 아니면 false.
 * 사용: `LocalDate.now() isOutsideDateRange (futureDate to veryFutureDate)`
 */
infix fun LocalDate.isOutsideDateRange(pair: Pair<LocalDate, LocalDate>): Boolean {
    val (start, end) = pair
    return this.isBefore(start) || this.isAfter(end)
}

/**
 * Temporal(LocalDate, LocalDateTime 등) 객체가 다른 Temporal 객체보다 이전인지 확인합니다.
 * @param other 비교할 다른 Temporal 객체.
 * @receiver 대상 Temporal 객체.
 * @return 이전이면 true, 아니면 false.
 * 사용: `startDate isBefore endDate`
 */
infix fun Temporal.isBefore(other: Temporal): Boolean {
    return when {
        this is LocalDate && other is LocalDate -> this.isBefore(other)
        this is LocalDateTime && other is LocalDateTime -> this.isBefore(other)
        // 다른 Temporal 타입 조합에 대한 지원이 필요하면 여기에 추가
        else -> throw IllegalArgumentException("Unsupported Temporal types for 'isBefore' comparison: ${this::class.simpleName} and ${other::class.simpleName}")
    }
}

/**
 * Temporal(LocalDate, LocalDateTime 등) 객체가 다른 Temporal 객체보다 이전이 아닌지 (같거나 이후인지) 확인합니다.
 * @param other 비교할 다른 Temporal 객체.
 * @receiver 대상 Temporal 객체.
 * @return 이전이 아니면 (같거나 이후이면) true, 아니면 false.
 * 사용: `startDate isOnOrAfter effectiveDate` (이전의 isNotBefore와 동일)
 */
infix fun Temporal.isNotBefore(other: Temporal): Boolean = !this.isBefore(other)


/**
 * Temporal(LocalDate, LocalDateTime 등) 객체가 다른 Temporal 객체보다 이후인지 확인합니다.
 * @param other 비교할 다른 Temporal 객체.
 * @receiver 대상 Temporal 객체.
 * @return 이후이면 true, 아니면 false.
 * 사용: `endDate isAfter startDate`
 */
infix fun Temporal.isAfter(other: Temporal): Boolean {
    return when {
        this is LocalDate && other is LocalDate -> this.isAfter(other)
        this is LocalDateTime && other is LocalDateTime -> this.isAfter(other)
        else -> throw IllegalArgumentException("Unsupported Temporal types for 'isAfter' comparison: ${this::class.simpleName} and ${other::class.simpleName}")
    }
}

/**
 * Temporal(LocalDate, LocalDateTime 등) 객체가 다른 Temporal 객체보다 이후가 아닌지 (같거나 이전인지) 확인합니다.
 * @param other 비교할 다른 Temporal 객체.
 * @receiver 대상 Temporal 객체.
 * @return 이후가 아니면 (같거나 이전이면) true, 아니면 false.
 * 사용: `dueDate isOnOrBefore paymentDate` (이전의 isNotAfter와 동일)
 */
infix fun Temporal.isNotAfter(other: Temporal): Boolean = !this.isAfter(other)

/**
 * LocalDateTime 객체가 현재 시간 기준으로 과거인지 확인합니다.
 * 사용: `creationTime is inThePast`
 */
infix fun LocalDateTime.`is`(marker: inThePast): Boolean = this.isBefore(LocalDateTime.now())
/**
 * LocalDateTime 객체가 현재 시간 기준으로 과거가 아닌지 (현재이거나 미래인지) 확인합니다.
 * 사용: `scheduledTime isNot inThePast`
 */
infix fun LocalDateTime.isNot(marker: inThePast): Boolean = !this.isBefore(LocalDateTime.now())

/**
 * LocalDateTime 객체가 현재 시간 기준으로 미래인지 확인합니다.
 * 사용: `expiryDate is inTheFuture`
 */
infix fun LocalDateTime.`is`(marker: inTheFuture): Boolean = this.isAfter(LocalDateTime.now())
/**
 * LocalDateTime 객체가 현재 시간 기준으로 미래가 아닌지 (현재이거나 과거인지) 확인합니다.
 * 사용: `lastLoginTime isNot inTheFuture`
 */
infix fun LocalDateTime.isNot(marker: inTheFuture): Boolean = !this.isAfter(LocalDateTime.now())
