package sample

import condition.applyGroup
import condition.util.*
import contract.Contract4KDsl
import contract.conditions
import contract.softConditions

object ApproveOrderContract : Contract4KDsl<Pair<Order, Customer>, Order> {

    override fun validateInvariant(input: Pair<Order, Customer>, output: Order) {
        val (order, customer) = input
        conditions {
            "주문은 null이 될 수 없습니다" means { order isNot nil }
            "소비자는 null이 될 수 없습니다" means { customer isNot nil }

        }
    }

    override fun validatePre(input: Pair<Order, Customer>) {
        val (order, customer) = input
        softConditions {
            "주문 가격은 1..10000 사이여야 합니다" means { order.amount between (1..10_000) }
            "상품 목록은 비어있으면 안 됩니다" means { order.items isNot  empty}
            "상품 목록 크기는 1..5 사이여야 합니다" means { order.items hasCountInRange (1..5) }
            "상품 목록에 중복이 없어야 합니다" means { order.items.containsDuplicates()}
            "상품 목록에 A, B가 모두 포함되어야 합니다" means { order.items hasAll listOf("A", "B") }
            "상품 목록에 C가 없어야 합니다" means { order.items doesNotHave  "C" }

            applyGroup(customer, CommonCustomerConditions)

//            "고객 이름에 'A'가 포함되어야 합니다" means { customer.name hasSub "A" }
//            "이메일 형식이어야 합니다" means { customer.email matchesForm EMAIL }
        }
    }

    override fun validatePost(input: Pair<Order, Customer>, output: Order) {
        conditions {
            "반환된 주문 상태는 APPROVED 여야 합니다" means { output.status == OrderStatus.APPROVED }
        }
    }
}