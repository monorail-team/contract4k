package sample

fun main() {
    val contract = ApproveOrderContract

//    // 1) 올바른 데이터로 테스트
    val goodOrder = Order(amount = 3000, status = OrderStatus.CREATED, id = 1, items = listOf("A", "B"))
    val goodCustomer = Customer(name = "Alice", id = 3, email = "alice@example.com")
//    try {
//        ApproveOrderContract.validatePre(Pair(goodOrder, goodCustomer))
//        println("✅ Precondition 통과")
//    } catch (e: Exception) {
//        println("❌ Precondition 실패: ${e.message}")
//    }

    // 2) 잘못된 데이터로 테스트
    val badOrder = Order(amount = -10, status = OrderStatus.CREATED, id = 2)
    val badCustomer = Customer(name = "", id = 2, email = "")
    try {
        ApproveOrderContract.validatePre(Pair(badOrder, badCustomer))
        println("✅ (잘못된) Precondition 통과 – 예상밖 1")
    } catch (e: Exception) {
        println("❌ (잘못된) Precondition 실패 – 정상 동작: ${e.message}")
    }

//    // 사후조건도 동일하게
//    val approvedOrder = goodOrder.copy(status = OrderStatus.APPROVED)
//    try {
//        ApproveOrderContract.validatePost(Pair(goodOrder, goodCustomer), approvedOrder)
//        println("✅ Postcondition 통과")
//    } catch (e: Exception) {
//        println("❌ Postcondition 실패: ${e.message}")
//    }
//
    // 잘못된 상태로
    try {
        ApproveOrderContract.validatePost(Pair(goodOrder, goodCustomer), goodOrder)
        println("✅ (잘못된) Postcondition 통과 – 예상밖")
    } catch (e: Exception) {
        println("❌ (잘못된) Postcondition 실패 – 정상 동작: ${e.message}")
    }
}