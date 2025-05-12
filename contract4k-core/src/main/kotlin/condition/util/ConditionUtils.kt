package condition.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.Temporal
import kotlin.reflect.KClass

/** 객체가 비어있는 상태를 나타내는 마커. (예: 빈 컬렉션, 빈 문자열) */
object empty

/** 객체가 비어있지 않은 상태를 나타내는 마커. */
object notEmpty

/** 문자열이 'blank' (null, 비어있거나 공백만 있는) 상태를 나타내는 마커. */
object blank

/** 문자열이 'not blank' 상태를 나타내는 마커. */
object notBlank

/** 숫자가 양수인 상태를 나타내는 마커. */
object positive

/** 숫자가 음수인 상태를 나타내는 마커. */
object negative

/** 숫자가 0인 상태를 나타내는 마커. */
object zero

/** 문자열이 모두 대문자인 상태를 나타내는 마커. */
object allUpperCase

/** 문자열이 모두 소문자인 상태를 나타내는 마커. */
object allLowerCase

/** 문자열이 알파벳 또는 숫자로만 구성된 상태를 나타내는 마커. */
object alphaNumeric

/** 날짜/시간이 과거인 상태를 나타내는 마커 (현재 시간 기준). */
object inThePast

/** 날짜/시간이 미래인 상태를 나타내는 마커 (현재 시간 기준). */
object inTheFuture

/** 컬렉션의 요소들이 모두 고유한(중복 없는) 상태를 나타내는 마커. */
object uniqueElements

/** 컬렉션에 null 요소가 없는 상태를 나타내는 마커. */
object allElementsNotNull

// -------- Collection/Iterable 유틸---------
/**
 * 컬렉션(Iterable)에 특정 요소가 포함되어 있는지 확인합니다.
 * @param element 확인할 요소.
 * @receiver 대상 컬렉션.
 * @return 포함되어 있으면 true, 아니면 false.
 * 사용: `listOf("A", "B") has "A"` (결과: true)
 */
infix fun <T> Iterable<T>?.has(element: T): Boolean = this?.contains(element) ?: false


/**
 * 컬렉션(Iterable)에 특정 요소가 포함되어 있지 않은지 확인합니다.
 * @param element 확인할 요소.
 * @receiver 대상 컬렉션.
 * @return 포함되어 있지 않으면 true, 아니면 false.
 * 사용: `listOf("A", "B") doesNotHave "C"` (결과: true)
 */
infix fun <T> Iterable<T>?.doesNotHave(element: T): Boolean = !(this?.contains(element) ?: false)

/**
 * 컬렉션(Iterable)이 다른 컬렉션의 모든 요소를 포함하는지 확인합니다.
 * @param other 포함 여부를 확인할 다른 컬렉션.
 * @receiver 대상 컬렉션.
 * @return 모든 요소를 포함하면 true, 아니면 false.
 * 사용: `listOf("A", "B", "C") hasAll listOf("A", "B")` (결과: true)
 */
infix fun <T> Iterable<T>?.hasAll(other: Iterable<T>): Boolean {
    if (this == null) return other.toList().isEmpty() // null은 빈 컬렉션의 모든 요소(없음)를 가짐
    if (other.toList().isEmpty()) return true // 모든 컬렉션은 빈 컬렉션의 모든 요소를 가짐
    return this.toList().containsAll(other.toList())
}

/**
 * 컬렉션(Iterable)이 다른 컬렉션의 모든 요소를 포함하지는 않는지 확인합니다. (하나라도 빠져있으면 true)
 * @param elements 포함 여부를 확인할 다른 컬렉션.
 * @receiver 대상 컬렉션.
 * @return 모든 요소를 포함하지 않으면 true, 아니면 false.
 * 사용: `listOf("A", "C") doesNotHaveAll listOf("A", "B")` (결과: true)
 */
infix fun <T> Iterable<T>?.doesNotHaveAll(elements: Iterable<T>): Boolean {
    if (elements.toList().isEmpty()) return false // 빈 컬렉션의 모든 요소는 항상 포함됨
    if (this == null) return true // null은 (비어있지 않은) 다른 컬렉션의 모든 요소를 가질 수 없음
    return !this.toList().containsAll(elements.toList())
}

/**
 * 컬렉션(Iterable)의 크기(요소 개수)가 주어진 범위 내에 있는지 확인합니다.
 * @param range 확인할 크기 범위 (예: `1..5`).
 * @receiver 대상 컬렉션.
 * @return 크기가 범위 내에 있으면 true, 아니면 false.
 * 사용: `listOf("A", "B") hasCountInRange (1..2)` (결과: true)
 */
infix fun Iterable<*>?.hasCountInRange(range: IntRange): Boolean =
    this != null && this.count() in range

/**
 * 컬렉션(Iterable)의 크기(요소 개수)가 주어진 범위 밖에 있는지 확인합니다.
 * @param range 확인할 크기 범위.
 * @receiver 대상 컬렉션.
 * @return 크기가 범위 밖에 있으면 true, 아니면 false.
 * 사용: `listOf("A") countIsOutsideRange (2..3)` (결과: true)
 */
infix fun Iterable<*>?.countIsOutsideRange(range: IntRange): Boolean =
    this == null || this.count() !in range


//-----------문자열 유틸---------------

/**
 * 문자열이 특정 텍스트(부분 문자열)를 포함하는지 확인합니다.
 * @param text 확인할 텍스트.
 * @param ignoreCase 대소문자 무시 여부 (기본값: false).
 * @receiver 대상 문자열.
 * @return 포함하면 true, 아니면 false.
 * 사용: `"Hello Kotlin" containsText "Kotlin"` (결과: true)
 * `"Hello Kotlin" containsText ("kotlin", ignoreCase = true)` (결과: true)
 */
infix fun String?.containsText(text: String): Boolean = this?.contains(text, false) ?: false
infix fun String?.containsText(pair: Pair<String, Boolean>): Boolean {
    val (text, ignoreCase) = pair
    return this?.contains(text, ignoreCase) ?: false
}


/**
 * 문자열이 특정 텍스트(부분 문자열)를 포함하지 않는지 확인합니다.
 * @param text 확인할 텍스트.
 * @param ignoreCase 대소문자 무시 여부 (기본값: false).
 * @receiver 대상 문자열.
 * @return 포함하지 않으면 true, 아니면 false.
 * 사용: `"Hello World" doesNotContainText "Kotlin"` (결과: true)
 */
infix fun String?.doesNotContainText(text: String): Boolean = !(this?.contains(text, false) ?: true)
infix fun String?.doesNotContainText(pair: Pair<String, Boolean>): Boolean {
    val (text, ignoreCase) = pair
    return !(this?.contains(text, ignoreCase) ?: true)
}

/**
 * 문자열이 특정 정규식 패턴과 일치하는지 확인합니다.
 * @param regex 검증할 정규식 객체.
 * @receiver 대상 문자열.
 * @return 정규식과 일치하면 true, 아니면 false.
 * 사용: `"user@example.com" matchesPattern Patterns.EMAIL` (결과: true)
 */
infix fun String?.matchesPattern(regex: Regex): Boolean =
    this != null && regex.matches(this)

/**
 * 문자열이 특정 정규식 패턴과 일치하지 않는지 확인합니다.
 * @param regex 검증할 정규식 객체.
 * @receiver 대상 문자열.
 * @return 정규식과 일치하지 않으면 true, 아니면 false.
 * 사용: `"invalid-email" doesNotMatchPattern Patterns.EMAIL` (결과: true)
 */
infix fun String?.doesNotMatchPattern(regex: Regex): Boolean =
    this == null || !regex.matches(this)

/**
 * 문자열이 특정 접두사로 시작하는지 확인합니다.
 * @param prefix 확인할 접두사.
 * @receiver 대상 문자열.
 * @return 접두사로 시작하면 true, 아니면 false.
 * 사용: `"prefix_text" startsWith "prefix_"`
 */
infix fun String?.startsWith(prefix: String): Boolean = this?.startsWith(prefix, false) ?: false
infix fun String?.startsWith(pair: Pair<String, Boolean>): Boolean {
    val (prefix, ignoreCase) = pair
    return this?.startsWith(prefix, ignoreCase) ?: false
}

/**
 * 문자열이 특정 접두사로 시작하지 않는지 확인합니다.
 * @param prefix 확인할 접두사.
 * @receiver 대상 문자열.
 * @return 접두사로 시작하지 않으면 true, 아니면 false.
 * 사용: `"text" doesNotStartWith "prefix_"`
 */
infix fun String?.doesNotStartWith(prefix: String): Boolean = !(this?.startsWith(prefix, false) ?: true)
infix fun String?.doesNotStartWith(pair: Pair<String, Boolean>): Boolean {
    val (prefix, ignoreCase) = pair
    return !(this?.startsWith(prefix, ignoreCase) ?: true)
}

/**
 * 문자열이 특정 접미사로 끝나는지 확인합니다.
 * @param suffix 확인할 접미사.
 * @receiver 대상 문자열.
 * @return 접미사로 끝나면 true, 아니면 false.
 * 사용: `"text_suffix" endsWith "_suffix"`
 */
infix fun String?.endsWith(suffix: String): Boolean = this?.endsWith(suffix, false) ?: false
infix fun String?.endsWith(pair: Pair<String, Boolean>): Boolean {
    val (suffix, ignoreCase) = pair
    return this?.endsWith(suffix, ignoreCase) ?: false
}

/**
 * 문자열이 특정 접미사로 끝나지 않는지 확인합니다.
 * @param suffix 확인할 접미사.
 * @receiver 대상 문자열.
 * @return 접미사로 끝나지 않으면 true, 아니면 false.
 * 사용: `"text" doesNotEndWith "_suffix"`
 */
infix fun String?.doesNotEndWith(suffix: String): Boolean = !(this?.endsWith(suffix, false) ?: true)
infix fun String?.doesNotEndWith(pair: Pair<String, Boolean>): Boolean {
    val (suffix, ignoreCase) = pair
    return !(this?.endsWith(suffix, ignoreCase) ?: true)
}

/**
 * 문자열이 정확히 주어진 길이를 가지는지 확인합니다.
 * @param length 확인할 길이.
 * @receiver 대상 문자열.
 * @return 길이가 일치하면 true, 아니면 false.
 * 사용: `"secret" hasExactLength 6`
 */
infix fun String?.hasExactLength(length: Int): Boolean = this?.length == length

/**
 * 문자열이 정확히 주어진 길이가 아닌지 확인합니다.
 * @param length 확인할 길이.
 * @receiver 대상 문자열.
 * @return 길이가 일치하지 않으면 true, 아니면 false.
 * 사용: `"longtext" doesNotHaveExactLength 3`
 */
infix fun String?.doesNotHaveExactLength(length: Int): Boolean = this?.length != length

/**
 * 문자열의 길이가 주어진 범위 내에 있는지 확인합니다.
 * @param range 확인할 길이 범위.
 * @receiver 대상 문자열.
 * @return 길이가 범위 내에 있으면 true, 아니면 false.
 * 사용: `"short" lengthInRange (1..5)`
 */
infix fun String?.lengthInRange(range: IntRange): Boolean = this != null && this.length in range

/**
 * 문자열의 길이가 주어진 범위 밖에 있는지 확인합니다.
 * @param range 확인할 길이 범위.
 * @receiver 대상 문자열.
 * @return 길이가 범위 밖에 있으면 true, 아니면 false.
 * 사용: `"toolongforthis" lengthIsOutsideRange (1..5)`
 */
infix fun String?.lengthIsOutsideRange(range: IntRange): Boolean = this == null || this.length !in range


//--------숫자 유틸 ----------

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


// ======== Date/Time Utilities ========

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

//--------- 일반 객체 유팅 -----------

/**
 * 객체가 특정 클래스의 인스턴스인지 확인합니다.
 * @param kClass 확인할 KClass 객체.
 * @receiver 대상 객체.
 * @return 해당 클래스의 인스턴스이면 true, 아니면 false.
 * 사용: `"string" isInstanceOf String::class`
 */
infix fun Any?.isInstanceOf(kClass: KClass<*>): Boolean = kClass.isInstance(this)

/**
 * 객체가 특정 클래스의 인스턴스가 아닌지 확인합니다.
 * @param kClass 확인할 KClass 객체.
 * @receiver 대상 객체.
 * @return 해당 클래스의 인스턴스가 아니면 true, 아니면 false.
 * 사용: `123 isNotInstanceOf String::class`
 */
infix fun Any?.isNotInstanceOf(kClass: KClass<*>): Boolean = !kClass.isInstance(this)

/**
 * 값이 주어진 컬렉션 내의 요소 중 하나인지 확인합니다.
 * @param elements 확인할 요소들을 담은 컬렉션.
 * @receiver 대상 값.
 * @return 컬렉션 내 요소 중 하나이면 true, 아니면 false.
 * 사용: `"B" isOneOf listOf("A", "B")`
 */
infix fun <T> T?.isOneOf(elements: Collection<T>): Boolean = elements.contains(this)

/**
 * 값이 주어진 컬렉션 내의 요소 중 아무것도 아닌지 확인합니다.
 * @param elements 확인할 요소들을 담은 컬렉션.
 * @receiver 대상 값.
 * @return 컬렉션 내 요소 중 아무것도 아니면 true, 아니면 false.
 * 사용: `"C" isNoneOf listOf("A", "B")`
 */
infix fun <T> T?.isNoneOf(elements: Collection<T>): Boolean = !elements.contains(this)

//---------스트링 마커 체크---------

/**
 * 문자열이 'blank' (null, 비어있거나 공백만 있는) 상태인지 확인합니다.
 * 사용: `userInput is blank`
 */
infix fun String?.`is`(marker: blank): Boolean = this.isNullOrBlank()
/**
 * 문자열이 'blank' 상태가 아닌지 확인합니다. (즉, 'notBlank' 상태인지)
 * 사용: `password isNot blank`
 */
infix fun String?.isNot(marker: blank): Boolean = !this.isNullOrBlank()

/**
 * 문자열이 'not blank' (null이 아니고, 공백 아닌 문자를 포함하는) 상태인지 확인합니다.
 * 사용: `title is notBlank`
 */
infix fun String?.`is`(marker: notBlank): Boolean = !this.isNullOrBlank()
/**
 * 문자열이 'not blank' 상태가 아닌지 확인합니다. (즉, 'blank' 상태인지)
 * 사용: `optionalField isNot notBlank`
 */
infix fun String?.isNot(marker: notBlank): Boolean = this.isNullOrBlank()

/**
 * 문자열이 모두 대문자인지 확인합니다. (null이거나 비어있으면 false)
 * 사용: `countryCode is allUpperCase`
 */
infix fun String?.`is`(marker: allUpperCase): Boolean = this != null && this.isNotEmpty() && this.all { it.isUpperCase() }
/**
 * 문자열이 모두 대문자가 아닌지 확인합니다. (소문자/특수문자 포함 또는 null/empty)
 * 사용: `mixedCaseString isNot allUpperCase`
 */
infix fun String?.isNot(marker: allUpperCase): Boolean = !(this != null && this.isNotEmpty() && this.all { it.isUpperCase() })

/**
 * 문자열이 모두 소문자인지 확인합니다. (null이거나 비어있으면 false)
 * 사용: `username is allLowerCase`
 */
infix fun String?.`is`(marker: allLowerCase): Boolean = this != null && this.isNotEmpty() && this.all { it.isLowerCase() }
/**
 * 문자열이 모두 소문자가 아닌지 확인합니다. (대문자/특수문자 포함 또는 null/empty)
 * 사용: `mixedCaseString isNot allLowerCase`
 */
infix fun String?.isNot(marker: allLowerCase): Boolean = !(this != null && this.isNotEmpty() && this.all { it.isLowerCase() })

/**
 * 문자열이 알파벳 또는 숫자로만 구성되어 있는지 확인합니다. (null이거나 비어있으면 false)
 * 사용: `activationKey is alphaNumeric`
 */
infix fun String?.`is`(marker: alphaNumeric): Boolean = this != null && this.isNotEmpty() && this.all { it.isLetterOrDigit() }
/**
 * 문자열이 알파벳 또는 숫자로만 구성되어 있지 않은지 확인합니다. (특수문자 포함 또는 null/empty)
 * 사용: `passwordWithSymbols isNot alphaNumeric`
 */
infix fun String?.isNot(marker: alphaNumeric): Boolean = !(this != null && this.isNotEmpty() && this.all { it.isLetterOrDigit() })

//---------숫자 마커 체크---------

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

// ======== Iterable/Collection 마커 체크 ========
/**
 * 컬렉션(Iterable)이 null이 아니고 비어있는지 확인합니다.
 * 사용: `errorsList is empty`
 */
infix fun <T> Iterable<T>?.`is`(marker: empty): Boolean = this != null && !this.any()
/**
 * 컬렉션(Iterable)이 (null이거나 또는) 비어있지 않은지 확인합니다.
 * 사용: `results isNot empty`
 */
infix fun <T> Iterable<T>?.isNot(marker: empty): Boolean = this == null || this.any()

/**
 * 컬렉션(Iterable)이 null이 아니고 비어있지 않은지 확인합니다.
 * 사용: `warningsList is notEmpty` (또는 `warningsList are notEmpty`)
 */
infix fun <T> Iterable<T>?.`is`(marker: notEmpty): Boolean = this != null && this.any()
infix fun <T> Iterable<T>?.are(marker: notEmpty): Boolean = this.`is`(marker) // 'are' for plural
/**
 * 컬렉션(Iterable)이 (null이거나 또는) 비어있는지 확인합니다.
 * 사용: `optionalItems isNot notEmpty` (또는 `optionalItems areNot notEmpty`)
 */
infix fun <T> Iterable<T>?.isNot(marker: notEmpty): Boolean = this == null || !this.any()
infix fun <T> Iterable<T>?.areNot(marker: notEmpty): Boolean = this.isNot(marker) // 'areNot' for plural

/**
 * 컬렉션의 모든 요소가 고유한지 (중복이 없는지) 확인합니다. (null이거나 비어있으면 true)
 * @receiver 대상 컬렉션
 * 사용: `userRoles are distinctElements`
 */
infix fun <T> Collection<T>?.are(marker: uniqueElements): Boolean = this == null || this.size == this.toSet().size
infix fun <T> Collection<T>?.`is`(marker: uniqueElements): Boolean = this.are(marker)
/**
 * 컬렉션에 중복된 요소가 있는지 확인합니다.
 * @receiver 대상 컬렉션
 * 사용: `duplicateEntriesList areNot distinctElements` (또는 `isNot`)
 */
infix fun <T> Collection<T>?.areNot(marker: uniqueElements): Boolean = !(this == null || this.size == this.toSet().size)
infix fun <T> Collection<T>?.isNot(marker: uniqueElements): Boolean = this.areNot(marker)


/**
 * 컬렉션에 null 요소가 없는지 확인합니다. (null이거나 비어있으면 true)
 * @receiver 대상 컬렉션 (nullable 요소 포함 가능)
 * 사용: `parameters are noNullElements`
 */
infix fun <T> Collection<T?>?.are(marker: allElementsNotNull): Boolean = this == null || this.all { it != null }
infix fun <T> Collection<T?>?.`is`(marker: allElementsNotNull): Boolean = this.are(marker)
/**
 * 컬렉션에 null 요소가 하나라도 있는지 확인합니다.
 * @receiver 대상 컬렉션 (nullable 요소 포함 가능)
 * 사용: `mixedList containsSomeNulls` (이 표현이 더 나을 수 있음, 아래 참조) 또는 `mixedList areNot noNullElements`
 */
infix fun <T> Collection<T?>?.areNot(marker: allElementsNotNull): Boolean = !(this == null || this.all { it != null })
infix fun <T> Collection<T?>?.isNot(marker: allElementsNotNull): Boolean = this.areNot(marker)

// 좀 더 명시적인 부정 표현을 위한 추가 함수
/**
 * 컬렉션에 중복된 요소가 있는지 확인합니다.
 * @receiver 대상 컬렉션
 * 사용: `idList containsDuplicates`
 */
fun <T> Collection<T>?.containsDuplicates(): Boolean = this != null && this.size != this.toSet().size

/**
 * 컬렉션에 null인 요소가 하나라도 있는지 확인합니다.
 * @receiver 대상 컬렉션 (nullable 요소 포함 가능)
 * 사용: `values containsSomeNulls`
 */
fun <T> Collection<T?>?.containsSomeNulls(): Boolean = this != null && this.any { it == null }

// -------- 날짜 시간 마커 체크 ----------

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

//-----------Advanced Collection 유틸 ------------

/**
 * 컬렉션의 모든 요소가 주어진 술어를 만족하는지 확인합니다.
 * (null 컬렉션은 조건을 만족하지 않는 것으로 간주, 빈 컬렉션은 항상 true)
 * @param predicate 각 요소에 적용할 술어.
 * @receiver 대상 컬렉션.
 * @return 모든 요소가 술어를 만족하면 true.
 * 사용: `numbers allSatisfy { it > 0 }`
 */
infix fun <T> Iterable<T>?.allSatisfy(predicate: (T) -> Boolean): Boolean {
    if (this == null) return false // null은 '모든 요소가 만족' 조건을 충족시키지 못함
    return this.all(predicate)
}

/**
 * 컬렉션의 모든 요소가 주어진 술어를 만족하지는 않는지 확인합니다.
 * (즉, 술어를 만족하지 않는 요소가 하나라도 있으면 true. null 컬렉션은 true)
 * @param predicate 각 요소에 적용할 술어.
 * @receiver 대상 컬렉션.
 * @return 술어를 만족하지 않는 요소가 있거나 컬렉션이 null이면 true.
 * 사용: `items notAllSatisfy { it.isValid }`
 */
infix fun <T> Iterable<T>?.notAllSatisfy(predicate: (T) -> Boolean): Boolean {
    if (this == null) return true
    return !this.all(predicate)
}

/**
 * 컬렉션의 요소 중 하나라도 주어진 술어를 만족하는지 확인합니다.
 * (null 컬렉션 또는 빈 컬렉션은 false)
 * @param predicate 각 요소에 적용할 술어.
 * @receiver 대상 컬렉션.
 * @return 술어를 만족하는 요소가 하나라도 있으면 true.
 * 사용: `users anySatisfies { it.isAdmin }`
 */
infix fun <T> Iterable<T>?.anySatisfies(predicate: (T) -> Boolean): Boolean {
    if (this == null) return false
    return this.any(predicate)
}

/**
 * 컬렉션의 어떤 요소도 주어진 술어를 만족하지 않는지 확인합니다. (none과 동일)
 * (null 컬렉션 또는 빈 컬렉션은 true)
 * @param predicate 각 요소에 적용할 술어.
 * @receiver 대상 컬렉션.
 * @return 어떤 요소도 술어를 만족하지 않으면 true.
 * 사용: `tasks noneSatisfy { it.isUrgent }`
 */
infix fun <T> Iterable<T>?.noneSatisfy(predicate: (T) -> Boolean): Boolean {
    if (this == null) return true
    return this.none(predicate)
}

