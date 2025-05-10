package contract4k.sample

fun main() {
    val order = Order("CREATED", 2500)
    val customer = Customer("sungil", 18)

    val result = OrderService().approveOrder(order, customer)

    val order2 = Order("CREATED", 2500)
    val result2 = OrderService().approveOrder(order2, customer)

    println("✅ 결과: $result")
    println("✅ 결과: $result2")
}