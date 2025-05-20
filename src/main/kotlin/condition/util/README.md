# Kotlin 조건 유틸리티 README

이 라이브러리는 Kotlin 확장 함수 모음을 제공하여, 가독성 높고 표현력이 풍부한 방식으로 일반적인 조건 검사를 중위 표기법(infix notation)을 통해 손쉽게 수행할 수 있도록 돕습니다.

## 목차

1.  [조건 마커](#조건-마커)
2.  [표준 정규식 패턴](#표준-정규식-패턴)
3.  [문자열 조건 유틸리티](#문자열-조건-유틸리티)
4.  [컬렉션 조건 유틸리티](#컬렉션-조건-유틸리티)
5.  [숫자 조건 유틸리티](#숫자-조건-유틸리티)
6.  [날짜/시간 조건 유틸리티](#날짜시간-조건-유틸리티)
7.  [객체 조건 유틸리티](#객체-조건-유틸리티)

---

## 조건 마커

이 마커 객체들은 `is` 및 `isNot` 중위 함수와 함께 사용되어 서술적인 조건 검사를 제공합니다.

- `empty`: 비어있는 상태를 나타냅니다 (예: 빈 컬렉션, 빈 문자열).
- `notEmpty`: 비어있지 않은 상태를 나타냅니다.
- `blank`: 문자열이 null이거나, 비어있거나, 공백만으로 구성된 상태를 나타냅니다.
- `notBlank`: 문자열이 `blank`가 아닌 상태를 나타냅니다.
- `positive`: 숫자가 양수인 상태를 나타냅니다.
- `negative`: 숫자가 음수인 상태를 나타냅니다.
- `zero`: 숫자가 0인 상태를 나타냅니다.
- `allUpperCase`: 문자열이 모두 대문자인 상태를 나타냅니다.
- `allLowerCase`: 문자열이 모두 소문자인 상태를 나타냅니다.
- `alphaNumeric`: 문자열이 알파벳 또는 숫자로만 구성된 상태를 나타냅니다.
- `inThePast`: 날짜/시간이 현재 시간 기준으로 과거인 상태를 나타냅니다.
- `inTheFuture`: 날짜/시간이 현재 시간 기준으로 미래인 상태를 나타냅니다.
- `distinctElements`: 컬렉션의 요소들이 모두 고유한(중복 없는) 상태를 나타냅니다.
- `containingDuplicates`: 컬렉션에 중복된 요소가 있는 상태를 나타냅니다.
- `allElementsNotNull`: 컬렉션에 null 요소가 없는 상태를 나타냅니다.
- `nil`: 객체가 null 상태임을 나타냅니다.

---

## 표준 정규식 패턴

`Patterns` 객체는 일반적인 유효성 검사 작업을 위한 미리 정의된 `Regex` 객체를 제공합니다.

- `Patterns.EMAIL`: 이메일 주소 유효성 검사용 (RFC 5322).
- `Patterns.URL`: HTTP/HTTPS URL 유효성 검사용.
- `Patterns.UUID`: UUID 유효성 검사용 (RFC 4122).
- `Patterns.PHONE`: 전화번호 유효성 검사용 (국제/국내).
- `Patterns.DATE`: 날짜 유효성 검사용 (YYYY-MM-DD).

---

## 문자열 조건 유틸리티

이 확장 함수들은 `String?` 타입에 대해 동작합니다.

### `containsText`

문자열이 특정 텍스트(부분 문자열)를 포함하는지 확인합니다.

- **사용법**:
  ```kotlin
  "Hello Kotlin" containsText "Kotlin" // 결과: true
  "Hello Kotlin" containsText ("kotlin" to true) // 결과: true (ignoreCase)
  "Hello Kotlin".containsText(Pair("kotlin", true)) // 결과: true (ignoreCase)
  ```
  - 두 번째 파라미터는 `String`이거나 `Pair<String, Boolean>` (Boolean 값은 `ignoreCase` 여부)일 수 있습니다.

### `doesNotContainText`

문자열이 특정 텍스트(부분 문자열)를 포함하지 않는지 확인합니다.

- **사용법**:
  ```kotlin
  "Hello World" doesNotContainText "Kotlin" // 결과: true
  "Hello World" doesNotContainText ("kotlin" to true) // 결과: true (ignoreCase)
  ```
  - 두 번째 파라미터는 `String`이거나 `Pair<String, Boolean>` (Boolean 값은 `ignoreCase` 여부)일 수 있습니다.

### `matchesPattern`

문자열이 특정 정규식 패턴과 일치하는지 확인합니다.

- **사용법**:
  ```kotlin
  "user@example.com" matchesPattern Patterns.EMAIL // 결과: true
  ```

### `doesNotMatchPattern`

문자열이 특정 정규식 패턴과 일치하지 않는지 확인합니다.

- **사용법**:
  ```kotlin
  "invalid-email" doesNotMatchPattern Patterns.EMAIL // 결과: true
  ```

### `startsWith`

문자열이 특정 접두사로 시작하는지 확인합니다.

- **사용법**:
  ```kotlin
  "prefix_text" startsWith "prefix_" // 결과: true
  "Prefix_text" startsWith ("prefix_" to true) // 결과: true (ignoreCase)
  ```
  - 두 번째 파라미터는 `String`이거나 `Pair<String, Boolean>` (Boolean 값은 `ignoreCase` 여부)일 수 있습니다.

### `doesNotStartWith`

문자열이 특정 접두사로 시작하지 않는지 확인합니다.

- **사용법**:
  ```kotlin
  "text" doesNotStartWith "prefix_" // 결과: true
  "Text" doesNotStartWith ("prefix_" to true) // 결과: true (ignoreCase)
  ```
  - 두 번째 파라미터는 `String`이거나 `Pair<String, Boolean>` (Boolean 값은 `ignoreCase` 여부)일 수 있습니다.

### `endsWith`

문자열이 특정 접미사로 끝나는지 확인합니다.

- **사용법**:
  ```kotlin
  "text_suffix" endsWith "_suffix" // 결과: true
  "text_Suffix" endsWith ("_suffix" to true) // 결과: true (ignoreCase)
  ```
  - 두 번째 파라미터는 `String`이거나 `Pair<String, Boolean>` (Boolean 값은 `ignoreCase` 여부)일 수 있습니다.

### `doesNotEndWith`

문자열이 특정 접미사로 끝나지 않는지 확인합니다.

- **사용법**:
  ```kotlin
  "text" doesNotEndWith "_suffix" // 결과: true
  "Text" doesNotEndWith ("_suffix" to true) // 결과: true (ignoreCase)
  ```
  - 두 번째 파라미터는 `String`이거나 `Pair<String, Boolean>` (Boolean 값은 `ignoreCase` 여부)일 수 있습니다.

### `hasExactLength`

문자열이 정확히 주어진 길이를 가지는지 확인합니다.

- **사용법**:
  ```kotlin
  "secret" hasExactLength 6 // 결과: true
  ```

### `doesNotHaveExactLength`

문자열이 정확히 주어진 길이가 아닌지 확인합니다.

- **사용법**:
  ```kotlin
  "longtext" doesNotHaveExactLength 3 // 결과: true
  ```

### `lengthInRange`

문자열의 길이가 주어진 범위 내에 있는지 확인합니다.

- **사용법**:
  ```kotlin
  "short" lengthInRange (1..5) // 결과: true
  ```

### `lengthIsOutsideRange`

문자열의 길이가 주어진 범위 밖에 있는지 확인합니다.

- **사용법**:
  ```kotlin
  "toolongforthis" lengthIsOutsideRange (1..5) // 결과: true
  ```

### `is blank` / `isNot blank`

문자열이 'blank' (null, 비어있거나 공백만 있는) 상태인지 확인합니다.

- **사용법**:

  ```kotlin
  val userInput: String? = " "
  userInput is blank // 결과: true

  val password: String? = "pass"
  password isNot blank // 결과: true
  ```

### `is notBlank` / `isNot notBlank`

문자열이 'not blank' (null이 아니고, 공백 아닌 문자를 포함하는) 상태인지 확인합니다.

- **사용법**:

  ```kotlin
  val title: String? = "Title"
  title is notBlank // 결과: true

  val optionalField: String? = ""
  optionalField isNot notBlank // 결과: true (즉, blank 상태임)
  ```

### `is allUpperCase` / `isNot allUpperCase`

문자열이 모두 대문자인지 확인합니다. (null이거나 비어있으면 `is allUpperCase`는 false).

- **사용법**:
  ```kotlin
  "CODE" is allUpperCase // 결과: true
  "MixedCase" isNot allUpperCase // 결과: true
  ```

### `is allLowerCase` / `isNot allLowerCase`

문자열이 모두 소문자인지 확인합니다. (null이거나 비어있으면 `is allLowerCase`는 false).

- **사용법**:
  ```kotlin
  "username" is allLowerCase // 결과: true
  "MixedCase" isNot allLowerCase // 결과: true
  ```

### `is alphaNumeric` / `isNot alphaNumeric`

문자열이 알파벳 또는 숫자로만 구성되어 있는지 확인합니다. (null이거나 비어있으면 `is alphaNumeric`는 false).

- **사용법**:
  ```kotlin
  "Key123" is alphaNumeric // 결과: true
  "Key-123" isNot alphaNumeric // 결과: true
  ```

---

## 컬렉션 조건 유틸리티

이 확장 함수들은 `Iterable<T>?` 또는 `Collection<T>?` 타입에 대해 동작합니다.

### `has`

컬렉션(Iterable)에 특정 요소가 포함되어 있는지 확인합니다.

- **사용법**:
  ```kotlin
  listOf("A", "B") has "A" // 결과: true
  ```

### `doesNotHave`

컬렉션(Iterable)에 특정 요소가 포함되어 있지 않은지 확인합니다.

- **사용법**:
  ```kotlin
  listOf("A", "B") doesNotHave "C" // 결과: true
  ```

### `hasAll`

컬렉션(Iterable)이 다른 컬렉션의 모든 요소를 포함하는지 확인합니다.

- **사용법**:
  ```kotlin
  listOf("A", "B", "C") hasAll listOf("A", "B") // 결과: true
  ```

### `doesNotHaveAll`

컬렉션(Iterable)이 다른 컬렉션의 모든 요소를 포함하지는 않는지 확인합니다. (하나라도 빠져있으면 true).

- **사용법**:
  ```kotlin
  listOf("A", "C") doesNotHaveAll listOf("A", "B") // 결과: true
  ```

### `hasCountInRange`

컬렉션(Iterable)의 크기(요소 개수)가 주어진 범위 내에 있는지 확인합니다.

- **사용법**:
  ```kotlin
  listOf("A", "B") hasCountInRange (1..2) // 결과: true
  ```

### `countIsOutsideRange`

컬렉션(Iterable)의 크기(요소 개수)가 주어진 범위 밖에 있는지 확인합니다.

- **사용법**:
  ```kotlin
  listOf("A") countIsOutsideRange (2..3) // 결과: true
  ```

### `allSatisfy`

컬렉션의 모든 요소가 주어진 술어를 만족하는지 확인합니다. (null 컬렉션은 false, 빈 컬렉션은 항상 true).

- **사용법**:
  ```kotlin
  val numbers = listOf(1, 2, 3)
  numbers allSatisfy { it > 0 } // 결과: true
  ```

### `notAllSatisfy`

컬렉션의 모든 요소가 주어진 술어를 만족하지는 않는지 확인합니다 (즉, 술어를 만족하지 않는 요소가 하나라도 있으면 true. null 컬렉션은 true).

- **사용법**:
  ```kotlin
  // data class Item(val isValid: Boolean)
  val items = listOf(Item(isValid = true), Item(isValid = false))
  items notAllSatisfy { it.isValid } // 결과: true
  ```

### `anySatisfies`

컬렉션의 요소 중 하나라도 주어진 술어를 만족하는지 확인합니다 (null 컬렉션 또는 빈 컬렉션은 false).

- **사용법**:
  ```kotlin
  // data class User(val isAdmin: Boolean)
  val users = listOf(User(isAdmin = false), User(isAdmin = true))
  users anySatisfies { it.isAdmin } // 결과: true
  ```

### `noneSatisfy`

컬렉션의 어떤 요소도 주어진 술어를 만족하지 않는지 확인합니다 (null 컬렉션 또는 빈 컬렉션은 true).

- **사용법**:
  ```kotlin
  // data class Task(val isUrgent: Boolean)
  val tasks = listOf(Task(isUrgent = false), Task(isUrgent = false))
  tasks noneSatisfy { it.isUrgent } // 결과: true
  ```

### `is empty` / `isNot empty`

컬렉션(Iterable)이 null이 아니고 비어있는지 확인합니다.

- **사용법**:

  ```kotlin
  val errorsList: List<String>? = listOf()
  errorsList is empty // 결과: true

  val results: List<Int>? = listOf(1)
  results isNot empty // 결과: true
  ```

### `is notEmpty` / `are notEmpty` / `isNot notEmpty` / `areNot notEmpty`

컬렉션(Iterable)이 null이 아니고 비어있지 않은지 확인합니다. (`are`는 복수형 가독성을 위함).

- **사용법**:

  ```kotlin
  val warningsList: List<String>? = listOf("Warning 1")
  warningsList is notEmpty // 결과: true
  warningsList are notEmpty // 결과: true

  val optionalItems: List<Any>? = null
  optionalItems isNot notEmpty // 결과: true (null이므로 "notEmpty"가 아님)
  optionalItems areNot notEmpty // 결과: true
  ```

### `are distinctElements` / `is distinctElements` / `areNot distinctElements` / `isNot distinctElements`

컬렉션의 모든 요소가 고유한지 (중복이 없는지) 확인합니다. (null이거나 비어있으면 true).

- **사용법**:

  ```kotlin
  val userRoles = listOf("Admin", "Editor")
  userRoles are distinctElements // 결과: true

  val duplicateEntriesList = listOf(1, 2, 2, 3)
  duplicateEntriesList areNot distinctElements // 결과: true
  duplicateEntriesList isNot distinctElements // 결과: true
  ```

### `are allElementsNotNull` / `is allElementsNotNull` / `areNot allElementsNotNull` / `isNot allElementsNotNull`

컬렉션에 null 요소가 없는지 확인합니다. (null이거나 비어있으면 true).

- **사용법**:

  ```kotlin
  val parameters: List<String?>? = listOf("param1", "param2")
  parameters are allElementsNotNull // 결과: true

  val mixedList: List<String?>? = listOf("item", null, "anotherItem")
  mixedList areNot allElementsNotNull // 결과: true
  mixedList isNot allElementsNotNull // 결과: true
  ```

### `are containingDuplicates` / `is containingDuplicates` / `areNot containingDuplicates` / `isNot containingDuplicates`

컬렉션에 중복된 요소가 있는지 확인합니다. (null 컬렉션은 중복 요소가 없는 것으로 간주).

- **사용법**:

  ```kotlin
  val duplicatedList = listOf("a", "b", "a")
  duplicatedList are containingDuplicates // 결과: true

  val uniqueList = listOf(1, 2, 3)
  uniqueList areNot containingDuplicates // 결과: true (고유함)
  uniqueList isNot containingDuplicates // 결과: true
  ```

---

## 숫자 조건 유틸리티

이 확장 함수들은 `Number` 타입에 대해 동작합니다.

### `between`

숫자가 주어진 범위 내에 있는지 확인합니다 (`IntRange` 또는 `ClosedFloatingPointRange<Double>`).

- **사용법**:
  ```kotlin
  10 between (0..100) // 결과: true
  10.5 between (10.0..11.0) // 결과: true
  ```

### `isOutside`

숫자가 주어진 범위 밖에 있는지 확인합니다.

- **사용법**:
  ```kotlin
  101 isOutside (0..100) // 결과: true
  9.9 isOutside (10.0..11.0) // 결과: true
  ```

### `isCloseTo`

두 숫자가 주어진 허용 오차 내에서 가까운지 확인합니다. 두 번째 인자는 다른 숫자와 허용 오차를 담은 `Pair<Number, Double>`입니다.

- **사용법**:
  ```kotlin
  3.1415 isCloseTo (3.14 to 0.01) // 결과: true
  3.1415.isCloseTo(Pair(3.14, 0.01)) // 결과: true
  ```

### `isNotCloseTo`

두 숫자가 주어진 허용 오차 내에서 가깝지 않은지 확인합니다. 두 번째 인자는 `Pair<Number, Double>`입니다.

- **사용법**:
  ```kotlin
  5.0 isNotCloseTo (10.0 to 1.0) // 결과: true
  5.0.isNotCloseTo(Pair(10.0, 1.0)) // 결과: true
  ```

### `is positive` / `isNot positive`

숫자가 양수인지 확인합니다.

- **사용법**:

  ```kotlin
  val count = 10
  count is positive // 결과: true

  val balance = -5
  balance isNot positive // 결과: true
  ```

### `is negative` / `isNot negative`

숫자가 음수인지 확인합니다.

- **사용법**:

  ```kotlin
  val temperature = -5
  temperature is negative // 결과: true

  val score = 0
  score isNot negative // 결과: true (0이므로 음수가 아님)
  ```

### `is zero` / `isNot zero`

숫자가 0인지 확인합니다.

- **사용법**:

  ```kotlin
  val remainingStock = 0
  remainingStock is zero // 결과: true

  val changeAmount = 0.5
  changeAmount isNot zero // 결과: true
  ```

---

## 날짜/시간 조건 유틸리티

이 함수들은 `java.time` 객체들(예: `LocalDate`, `LocalDateTime`)과 함께 사용됩니다.

### `LocalDate.between`

`LocalDate` 객체가 주어진 날짜 범위 (시작일, 종료일 포함) 내에 있는지 확인합니다. 범위는 `Pair<LocalDate, LocalDate>`입니다.

- **사용법**:
  ```kotlin
  val date = LocalDate.of(2023, 10, 15)
  val startDate = LocalDate.of(2023, 10, 1)
  val endDate = LocalDate.of(2023, 10, 31)
  date between (startDate to endDate) // 결과: true
  ```

### `LocalDate.isOutsideDateRange`

`LocalDate` 객체가 주어진 날짜 범위 (시작일, 종료일 포함) 밖에 있는지 확인합니다. 범위는 `Pair<LocalDate, LocalDate>`입니다.

- **사용법**:
  ```kotlin
  val date = LocalDate.of(2023, 11, 15)
  val startDate = LocalDate.of(2023, 10, 1)
  val endDate = LocalDate.of(2023, 10, 31)
  date isOutsideDateRange (startDate to endDate) // 결과: true
  ```

### `Temporal.isBefore`

`Temporal` (LocalDate, LocalDateTime 등) 객체가 다른 `Temporal` 객체보다 이전인지 확인합니다.

- **사용법**:
  ```kotlin
  val startDate = LocalDate.of(2023, 1, 1)
  val endDate = LocalDate.of(2023, 1, 2)
  startDate isBefore endDate // 결과: true
  ```

### `Temporal.isNotBefore`

`Temporal` (LocalDate, LocalDateTime 등) 객체가 다른 `Temporal` 객체보다 이전이 아닌지 (같거나 이후인지) 확인합니다.

- **사용법**:
  ```kotlin
  val effectiveDate = LocalDate.of(2023, 1, 1)
  val startDate = LocalDate.of(2023, 1, 1)
  startDate isNotBefore effectiveDate // 결과: true (같은 날짜임)
  ```

### `Temporal.isAfter`

`Temporal` (LocalDate, LocalDateTime 등) 객체가 다른 `Temporal` 객체보다 이후인지 확인합니다.

- **사용법**:
  ```kotlin
  val endDate = LocalDate.of(2023, 1, 2)
  val startDate = LocalDate.of(2023, 1, 1)
  endDate isAfter startDate // 결과: true
  ```

### `Temporal.isNotAfter`

`Temporal` (LocalDate, LocalDateTime 등) 객체가 다른 `Temporal` 객체보다 이후가 아닌지 (같거나 이전인지) 확인합니다.

- **사용법**:
  ```kotlin
  val paymentDate = LocalDate.of(2023, 1, 15)
  val dueDate = LocalDate.of(2023, 1, 15)
  dueDate isNotAfter paymentDate // 결과: true (같은 날짜임)
  ```

### `LocalDateTime is inThePast` / `LocalDateTime isNot inThePast`

`LocalDateTime` 객체가 현재 시간 기준으로 과거인지 확인합니다.

- **사용법**:

  ```kotlin
  val creationTime = LocalDateTime.now().minusDays(1)
  creationTime is inThePast // 결과: true

  val scheduledTime = LocalDateTime.now().plusHours(1)
  scheduledTime isNot inThePast // 결과: true
  ```

### `LocalDateTime is inTheFuture` / `LocalDateTime isNot inTheFuture`

`LocalDateTime` 객체가 현재 시간 기준으로 미래인지 확인합니다.

- **사용법**:

  ```kotlin
  val expiryDate = LocalDateTime.now().plusDays(1)
  expiryDate is inTheFuture // 결과: true

  val lastLoginTime = LocalDateTime.now().minusMinutes(5)
  lastLoginTime isNot inTheFuture // 결과: true
  ```

---

## 객체 조건 유틸리티

이 함수들은 `Any?` 타입에 대해 일반적인 객체 검사를 수행합니다.

### `isNot nil`

객체가 null이 아닌지 확인합니다.

- **사용법**:
  ```kotlin
  val myObject: String? = "Hello"
  myObject isNot nil // 결과: true
  ```

### `is nil`

객체가 null인지 확인합니다.

- **사용법**:
  ```kotlin
  val myObject: String? = null
  myObject is nil // 결과: true
  ```

### `isInstanceOf`

객체가 특정 클래스의 인스턴스인지 확인합니다.

- **사용법**:
  ```kotlin
  "string" isInstanceOf String::class // 결과: true
  123 isInstanceOf Int::class // 결과: true
  ```

### `isNotInstanceOf`

객체가 특정 클래스의 인스턴스가 아닌지 확인합니다.

- **사용법**:
  ```kotlin
  123 isNotInstanceOf String::class // 결과: true
  "string" isNotInstanceOf Int::class // 결과: true
  ```

### `isOneOf`

값이 주어진 컬렉션 내의 요소 중 하나인지 확인합니다.

- **사용법**:
  ```kotlin
  "B" isOneOf listOf("A", "B", "C") // 결과: true
  // enum class Status { PENDING, APPROVED }
  // Status.PENDING isOneOf setOf(Status.PENDING, Status.APPROVED) // 결과: true
  ```

### `isNoneOf`

값이 주어진 컬렉션 내의 요소 중 아무것도 아닌지 확인합니다.

- **사용법**:
  ```kotlin
  "D" isNoneOf listOf("A", "B", "C") // 결과: true
  // enum class Status { PENDING, APPROVED, REJECTED }
  // Status.REJECTED isNoneOf setOf(Status.PENDING, Status.APPROVED) // 결과: true
  ```

---
