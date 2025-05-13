package condition.util

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
object distinctElements

/** 컬렉션에 중복된 요소가 있는 상태를 나타내는 마커. */
object containingDuplicates

/** 컬렉션에 null 요소가 없는 상태를 나타내는 마커. */
object allElementsNotNull

/** 객체에 null 상태를 나타내는 마커 */
object nil
