package condition

import java.time.LocalDate

/**
 * 컬렉션에 특정 요소가 포함되어 있는지
 * 사용: people has "Jin"
 */
infix fun <T> Iterable<T>.has(element: T): Boolean =
    this.contains(element)

/**
 * 컬렉션이 다른 컬렉션의 모든 요소를 포함하는지
 * 사용: listA hasAll listB
 */
infix fun <T> Iterable<T>.hasAll(other: Iterable<T>): Boolean =
    this.toList().containsAll(other.toList())

/**
 * 문자열에 특정 부분 문자열이 포함되어 있는지
 * 사용: description hasSub "foo"
 */
infix fun String.hasSub(sub: String): Boolean =
    this.contains(sub)


/**
 * 숫자가 주어진 범위에 포함되는지 (Number.toDouble() 사용)
 * 사용: age between (0..150)
 */
infix fun Number.between(range: ClosedFloatingPointRange<Double>): Boolean =
    this.toDouble() in range

infix fun Number.between(range: IntRange): Boolean =
    this.toDouble() in range.start.toDouble()..range.endInclusive.toDouble()

/**
 * 날짜가 (startDate, endDate) 사이에 있는지 (포함)
 * 사용: orderDate between (start to end)
 */
infix fun LocalDate.between(pair: Pair<LocalDate, LocalDate>): Boolean {
    val (start, end) = pair
    return !this.isBefore(start) && !this.isAfter(end)
}


/**
 * 컬렉션 크기가 주어진 범위에 포함되는지
 * 사용: users sizeBetween (1..5)
 */
infix fun Iterable<*>?.sizeBetween(range: IntRange): Boolean =
    this != null && this.count() in range


/**
 * 주어진 문자열이 특정 Regex에 매치되는지
 * 사용: email matchesForm EMAIL
 */
infix fun String?.matchesForm(regex: Regex): Boolean =
    this != null && regex.matches(this)

/**
 * 컬렉션(Iterable)이 비어있지 않은지 Boolean을 반환
 * 호출: notEmpty(items)
 */
fun <T> notEmpty(items: Iterable<T>?): Boolean =
    items != null && items.any()

/**
 * 문자열이 비어있지 않은지 Boolean을 반환
 * 호출: notEmpty(str)
 */
fun notEmpty(str: String?): Boolean =
    str != null && str.isNotBlank()

/**
 * 컬렉션에 중복 요소가 없는지 Boolean 을 반환
 * 사용 예시: hasNoDuplicates(order.items)
 */
fun <T> hasNoDuplicates(items: Collection<T>?): Boolean =
    items != null && items.size == items.toSet().size

/**
 * 컬렉션에 null 요소가 없는지 Boolean 을 반환
 * 사용 예시: hasNoNullElements(order.items)
 */
fun <T> hasNoNullElements(items: Collection<T?>?): Boolean =
    items != null && items.all { it != null }