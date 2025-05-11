package sample

data class Order(
    val id: Long,
    val amount: Int,
    val items: List<String> = emptyList(),
    var status: OrderStatus
)