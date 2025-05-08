package contract4k.sample

fun main() {
    val order = Order("CREATED", 2500)
    val customer = Customer("sungil", 18)

    val result = OrderService().approveOrder(order, customer)

    println("✅ 결과: $result")
}