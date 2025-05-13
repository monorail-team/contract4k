package sample

import annotation.Contract4kDoc
import annotation.Contract4kWith

class OrderService {

    @Contract4kDoc(ApproveOrderDoc::class)
    @Contract4kWith(ApproveOrderContract::class)
    fun approveOrder(order: Order, customer: Customer): Order {
        println("💼 주문 승인 중...")
        order.status = OrderStatus.APPROVED
        println("✅ 주문 승인 완료!")
        return order
    }

    @Contract4kWith(ApproveOrderContract::class)
    suspend fun approveOrderSuspend(order: Order, customer: Customer): Order {
        println("💼 주문 승인 중...")
        order.status = OrderStatus.APPROVED
        println("✅ 주문 승인 완료!")
        return order
    }
}