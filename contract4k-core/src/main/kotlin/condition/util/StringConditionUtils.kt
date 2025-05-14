package condition.util


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
