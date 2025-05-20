<!-- Table of Contents -->

- [소개](#introduction)
- [설치](#설치)
- [빠른 시작 (Quick Start)](#빠른-시작-quick-start)
- [핵심 개념](#핵심-개념)
  - [Contract4KDsl 인터페이스](#contract4kdsl-인터페이스)
  - [@Contract4kWith 어노테이션](#contract4kwith-어노테이션)
- [DSL 사용법](#dsl-사용법)
  - [사전/불변/사후 조건 정의](#사전불변사후-조건-정의)
- [검증 DSL 헬퍼](#검증-DSL-헬퍼)
- [예외 처리](#예외-처리)
  - [ValidationException](#validationexception)
  - [ErrorCode](#errorcode)
- [고급 기능](#고급-기능)

---

<a id="introduction"></a>

## 소개

**Contract4K**

> 코틀린 DSL로 작성한 “계약서”가 그대로 메서드의 사전·불변·사후 조건을 문서화하고 검증합니다.  
> 비즈니스 로직과 검증 코드를 완전히 분리해, 더 깔끔하고 유지보수하기 쉬운 코드를 만들어 줍니다.

### 해결하는 문제

- **반복되는 방어적 코드**  
  메서드 시작 부분마다 `null` 체크, 범위 검사 등이 비즈니스 로직을 가리는 경우가 많습니다.

- **강제되지 않는 규칙**  
  주석이나 `require`/`check` 로만 명시된 규칙이 코드 레벨에서 강제되지 않아, 개발자의 실수에 의존하게 됩니다.

- **암묵적 가정의 위험**  
  메서드·클래스 사용 시 명시되지 않은 가정에 의존하면, 협업이 어려워지고 예기치 않은 버그가 발생할 수 있습니다.

- **기존 라이브러리 한계**  
  Guava, Cofoja 등은 사전 조건만 지원하거나 유지보수가 중단된 상태입니다.  
  Contract4K는 **어노테이션 + 별도 ‘계약서’ 클래스** 방식으로, 메서드와 검증 로직을 깔끔히 분리합니다.

### 핵심 기능

1. **어노테이션 기반 적용**

   - `@Contract4kWith(MyContract::class)` 을 서비스 메서드에 붙이면  
     해당 계약 클래스의 `pre`/`invariant`/`post` 검증이 자동 실행됩니다.

2. **가독성 높은 DSL**

   - `"메시지" means { 조건 }` 형태로, 코드가 그대로 문서가 됩니다.
   - `meansAnyOf`, `meansAllOf` 같은 편의 메서드와 에러코드 직접 정의 지원.

3. **풍부한 헬퍼 함수 제공**
   - 숫자 범위, 컬렉션 검사, 정규식 등 자주 쓰이는 검증 헬퍼를 infix 확장 함수로 제공합니다.

---

## 설치 <a id="설치"></a>

아래와 같이 Gradle 설정을 추가하면 Contract4K 라이브러리를 사용할 수 있습니다:

```kotlin
plugins {
    kotlin("jvm") version "2.0.21"
    id("io.freefair.aspectj.post-compile-weaving") version "8.4"
}

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    // Contract4K AOP weaving 의존성
    aspect("com.github.monorail-team:contract4k:v1.0.0")
    // AspectJ 런타임
    implementation("org.aspectj:aspectjrt:1.9.21")
    // Kotlin 리플렉션
    implementation(kotlin("reflect"))
}
```

---

## 빠른 시작 (Quick Start)

```kotlin
1) 도메인 모델
data class Order(val id: Long?, val amount: Int)

2) 계약서 정의
object ApproveOrderContract : Contract4KDsl<Pair<Order, Unit>, Order> {
  override fun validatePre(input: Pair<Order, Unit>) = conditions {
    "주문 금액은 1 이상이어야 합니다" means { input.first.amount >= 1 }
  }
}

3) 서비스 사용
class OrderService {
  @Contract4kWith(ApproveOrderContract::class)
  fun placeOrder(order: Order): Order = order
}

4) 실행 예시
fun main() {
  OrderService().placeOrder(Order(null, 0))
  // → Validation failed with 1 errors:
  //  - 주문 금액은 1 이상이어야 합니다
}
```

---

## 핵심 개념 <a id="핵심-개념"></a>

### Contract4KDsl 인터페이스 <a id="contract4kdsl-인터페이스"></a>

`Contract4KDsl<I, O>` 은 “계약서” 역할을 하는 DSL 진입점입니다.  
제네릭 파라미터:

- `I`: 메서드 호출 시점의 입력값 타입 (파라미터가 여러 개면 `and` 연산자를 사용해 묶음)
- `O`: 메서드 실행 결과 타입

주요 메서드:

```kotlin
interface Contract4KDsl<I, O> {
  /** ① 사전(pre) 조건 검사 — 비즈니스 로직 실행 전 */
  fun validatePre(input: I)

  /** ② 불변식(invariant) 검사 — 로직 중에도 항상 지켜져야 할 조건 */
  fun validateInvariant(input: I, output: O)

  /** ③ 사후(post) 조건 검사 — 로직 실행 후 결과 검증 */
  fun validatePost(input: I, result: O)
}
```

### @ContractWith 어노테이션 <a id="contract4kwith-어노테이션"></a>

```
@Service
class OrderService {
  @Contract4kWith(ApproveOrderContract::class)
  fun placeOrder(...) = …
}
```

---

## DSL 사용법 <a id="dsl-사용법"></a>

Contract4K 의 핵심은 **“메시지” means { 조건 }** 형태의 Kotlin DSL 로 원하는 검증 로직을 깔끔하게 작성할 수 있다는 점입니다.  
아래처럼 **사전(pre)**, **불변(invariant)**, **사후(post)** 3단계로 나누어 블록 안에 조건을 선언하면, AOP 가 자동으로 해당 단계에서 실행해 줍니다.

---

### 사전/불변/사후 조건 정의 <a id="사전불변사후-조건-정의"></a>

```kotlin
object ApproveOrderContract : Contract4KDsl<Pair<Order, Customer>, Order> {

  // ① 사전(pre) 조건: 메서드 진입 직전에 실행
  override fun validatePre(input: Pair<Order, Customer>) = conditions {
    // 방법 1
    val (order, customer) = input
    "주문 객체는 null일 수 없습니다" means { order isNot nil }
    "고객 객체는 null일 수 없습니다" means { customer isNot nil }
    //방법 2
    "주문 객체는 null일 수 없습니다" means { input.first isNot nil }
    "고객 객체는 null일 수 없습니다" means { input.second isNot nil }


  }

  // ② 불변(invariant) 조건: 비즈니스 로직 중에도 유지되어야 할 제약
  override fun validateInvariant(input: Pair<Order, Customer>, output: Order) = conditions {
    "주문 ID는 항상 존재해야 합니다" means { output.id isNot nil }
  }

  // ③ 사후(post) 조건: 메서드 종료 후 최종 상태 검증
  override fun validatePost(input: Pair<Order, Customer>, result: Order) = conditions {
    "최종 상태는 COMPLETED 여야 합니다" means { result.status == "COMPLETED" }
  }
}
```

---

## 조건 빌더 유틸리티 <a id="검증-DSL-헬퍼"></a>

ConditionBuilder 에서 자주 쓰이는 주요 헬퍼 함수:

- **숫자 검사**

  - `between(range: IntRange)`
    ```kotlin
    order.amount between (1..10_000)
    ```
  - `is positive` / `isNot negative`
    ```kotlin
    count is positive
    balance isNot negative
    ```

- **컬렉션 검사**

  - `hasCountInRange(range: IntRange)`
    ```kotlin
    list hasCountInRange (1..5)
    ```
  - `hasNoDuplicates()`
    ```kotlin
    items hasNoDuplicates()
    ```
  - `allSatisfy { predicate }`
    ```kotlin
    users allSatisfy { it.isActive }
    ```

- **문자열 검사**

  - `hasExactLength(length: Int)`
    ```kotlin
    password hasExactLength 8
    ```
  - `doesNotStartWith(prefix: String)`
    ```kotlin
    token doesNotStartWith "ERR_"
    ```

- **날짜·시간 검사**
  - `isBefore(other: Temporal)`
    ```kotlin
    startDate isBefore endDate
    ```
  - `isAfter(other: Temporal)`
    ```kotlin
    dueDate isAfter now
    ```

> **전체 헬퍼 목록** 은 [헬퍼 함수 문서](./src/main/kotlin/condition/util/README.md)를 참고하세요.

---

## 예외 처리 <a id="예외-처리"></a>

- **`ValidationException`** <a id="validationexception"></a>

  - 계약(pre/invariant/post) 중 하나라도 실패하면 던져집니다.
  - `RuntimeException` 을 상속하며, 메시지에 어떤 조건이 왜 실패했는지 한눈에 보여 줍니다.
  - 예시:
    ```kotlin
    try {
      orderService.placeOrder(invalidOrder, customer)
    } catch (e: ValidationException) {
      println(e.message)
      // → Validation failed with 1 error:
      //    - 주문 금액은 1 이상이어야 합니다.
    }
    ```

- **ErrorCode** <a id="errorcode"></a>
  - 예외 메시지 안에서 `[ERROR_CODE] 메시지` 형태로 표시됩니다.
  - 사용자는 메시지만 보고도 “무슨 조건”이 “왜” 실패했는지 바로 알 수 있습니다.

---

## 고급 기능 <a id="고급-기능"></a>

### 1. 조건 그룹화 (OR / AND)

- **meansAnyOf { … }**  
  여러 조건 중 하나만 만족해도 OK인 그룹화

  ```
  conditions {
    meansAnyOf {
      "A 상품이 포함되어야 합니다" means { "A" in order.items }
      "B 상품이 포함되어야 합니다" means { "B" in order.items }
    }
  }
  ```

- **meansAllOf { … }**  
  모든 조건을 동시에 만족해야 하는 그룹화

  ```
  conditions {
    meansAllOf {
      "금액은 양수여야 합니다" means { order.amount > 0 }
      "고객 나이는 18세 이상이어야 합니다" means { customer.age >= 18 }
    }
  }
  ```

### 2. 공통 조건 묶음 재사용 (ConditionGroup)

- 자주 쓰이는 조건을 `ConditionGroup`으로 정의하고, 여러 계약서에서 재사용 가능

  ```
  object CommonCustomerConditions : ConditionGroup<Pair<Order, Customer>> {
    override fun apply(builder: ConditionBuilder, input: Pair<Order, Customer>) {
      val (_, customer) = input
      "고객 이름은 비어 있으면 안 됩니다" means { customer.name isNot nil}
      "고객 나이는 0 초과여야 합니다" means { customer.age > 0 }
    }
  }

  conditions {
    applyGroup(input, CommonCustomerConditions)
    // 추가 커스텀 조건...
  }
  ```

### 3. 경고 수준 조건 (softConditions)

- 예외가 아닌 **경고**로만 처리
  ```
  softConditions {
    "장기 미이용 고객입니다" means { daysSinceLastLogin > 365 }
  }
  ```

### 4. QuickFix 제안

- 조건에 수정 제안 추가
  ```
  conditions {
    "주문 금액은 1,000원 이상이어야 합니다"
      quickFix "금액을 1,000원 이상으로 설정하세요"
      means { order.amount >= 1_000 }
  }
  ```

### 5. 사용자 지정 에러 코드

- `means(code, message) { … }` 또는 `quickFix(code, message, fix) means { … }` 사용

  ```
  conditions {
    means(
      code    = "ERR_INVALID_AMOUNT",
      message = "주문 금액은 1 이상이어야 합니다"
    ) { order.amount >= 1 }

    quickFix(
      code       = "ERR_NULL_ORDER",
      message    = "주문 객체는 null일 수 없습니다",
      fixMessage = "올바른 주문 객체를 전달하세요"
    ) means { order != null }
  }
  ```

---
