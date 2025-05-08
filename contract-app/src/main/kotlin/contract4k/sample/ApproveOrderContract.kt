package contract4k.sample

import contract4k.contract.Contract4kDsl
import contract4k.contract.preConditions
import contract4k.contract.postConditions

class ApproveOrderContract : Contract4kDsl<Pair<Order, Customer>, Order> {

    override fun validatePre(input: Pair<Order, Customer>) = preConditions {
        println("pre")
        val (order, customer) = input

        "주문 상태는 CREATED여야 합니다" means {
            order.status == "CREATED"
        }
        "주문 금액은 1,000원 이상이어야 합니다" quickFix "Order 금액을 1,000원 이상으로 설정하세요" means {
            order.amount >= 1000
        }
        "고객 이름이 비어 있으면 안 됩니다" mustBe {
            customer.name.isNotBlank()
        }
        "고객 이름은 최소 2자 이상이어야 합니다" quickFix "고객 이름을 2자 이상 입력하세요" means {
            customer.name.length >= 2
        }
        "고객 나이는 18세 이상이어야 합니다" mustBe {
            customer.age >= 18
        }
    }

    override fun validatePost(input: Pair<Order, Customer>, result: Order) = postConditions {
        println("post")
        "승인 후 주문 상태는 APPROVED여야 합니다" mustBe {
            result.status == "APPROVED"
        }
        "승인된 주문 금액은 1,000원 이상이어야 합니다" means {
            result.amount >= 1000
        }
    }
}