package contract4k.sample

import contract4k.annotation.Contract4kWith

class OrderService {

    @Contract4kWith(ApproveOrderContract::class)
    fun approveOrder(order: Order, customer: Customer): Order {
        println("💼 주문 승인 중...")
        order.status = "APPROVED"
        println("✅ 주문 승인 완료!")
        return order
    }
}