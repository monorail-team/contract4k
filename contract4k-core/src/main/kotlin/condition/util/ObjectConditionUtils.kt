package condition.util

import kotlin.reflect.KClass

/**
 * 모든 타입의 객체(Any?)가 null이 아닌지 확인합니다.
 * @receiver 확인할 객체.
 * @param marker 'nil' 마커 객체.
 * @return 객체가 null이 아니면 true, null이면 false.
 * 사용: `myObject isNot nil`
 */
infix fun Any?.isNot(marker: nil): Boolean = this != null

/**
 * 모든 타입의 객체(Any?)가 null인지 확인합니다.
 * @receiver 확인할 객체.
 * @param marker 'nil' 마커 객체.
 * @return 객체가 null이면 true, null이 아니면 false.
 * 사용: `myObject is nil`
 */
infix fun Any?.`is`(marker: nil): Boolean = this == null

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