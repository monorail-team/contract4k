package condition.util

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
infix fun <T> Collection<T>?.are(marker: distinctElements): Boolean = this == null || this.size == this.toSet().size
infix fun <T> Collection<T>?.`is`(marker: distinctElements): Boolean = this.are(marker)
/**
 * 컬렉션에 중복된 요소가 있는지 확인합니다.
 * @receiver 대상 컬렉션
 * 사용: `duplicateEntriesList areNot distinctElements` (또는 `isNot`)
 */
infix fun <T> Collection<T>?.areNot(marker: distinctElements): Boolean = !(this == null || this.size == this.toSet().size)
infix fun <T> Collection<T>?.isNot(marker: distinctElements): Boolean = this.areNot(marker)


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


/**
 * 컬렉션에 중복된 요소가 있는지 확인합니다.
 * @receiver 대상 컬렉션
 * @param marker 'containingDuplicates' 마커 객체
 * @return 중복된 요소가 있으면 true, 없거나 컬렉션이 null이면 false.
 * 사용: `duplicatedList are containingDuplicates`
 */
infix fun <T> Collection<T>?.are(marker: containingDuplicates): Boolean =
    this != null && this.size != this.toSet().size

infix fun <T> Collection<T>?.`is`(marker: containingDuplicates): Boolean = this.are(marker)

/**
 * 컬렉션에 중복된 요소가 없는지 확인합니다. (즉, 모든 요소가 고유한지)
 * @receiver 대상 컬렉션
 * @param marker 'containingDuplicates' 마커 객체
 * @return 중복된 요소가 없거나 컬렉션이 null이면 true. (이것은 'are uniqueElements'와 동일)
 * 사용: `uniqueList areNot containingDuplicates`
 */
infix fun <T> Collection<T>?.areNot(marker: containingDuplicates): Boolean =
    !(this != null && this.size != this.toSet().size)

infix fun <T> Collection<T>?.isNot(marker: containingDuplicates): Boolean = this.areNot(marker)
