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
            "주문은 빈 값이 될 수 없습니다" means { order isNot nil }
            "소비자는 빈 값이 될 수 없습니다" means { customer isNot nil }
        }
    }

    override fun validatePre(input: Pair<Order, Customer>) {
        val (order, customer) = input
        softConditions {
            means(code = "testError", message = "테스트", predicate = { order `is` nil })

            quickFix(
                code = "널코드",
                message = "주문이 비어있으면 안됨",
                fixMessage = "주문 넣어라"
            ) means { order `is` nil}

            meansAnyOf(
                code = "OPTION_A_OR_B_RECOMMENDED",
                message = "선택 사항 A 또는 B를 포함하는 것이 좋습니다",
            ) {
                "A 포함됨" meansNested { order.items has "c" }
                "B 포함됨" meansNested { order.items has "b" }
            }

            "주문 가격은 1..10000 사이여야 합니다" means { order.amount between (1..10_000) }
            "상품 목록은 비어있으면 안 됩니다" means { order.items isNot  empty}
            "상품 목록 크기는 1..5 사이여야 합니다" means { order.items hasCountInRange (1..5) }
            "상품 목록에 중복이 없어야 합니다" means { order.items are distinctElements}
            "상품 목록에 A, B가 모두 포함되어야 합니다" means { order.items hasAll listOf("A", "B") }
            "상품 목록에 C가 없어야 합니다" means { order.items doesNotHave  "C" }

            applyGroup(customer, CommonCustomerConditions)

        }

        conditions {
            "고객 연락처 유효성: 이메일 또는 전화번호 중 하나는 유효해야 합니다" meansAllOf {
                "이메일 형식 검사" meansNested { customer.email matchesPattern  Patterns.EMAIL }
                "고객 이름 비어있지 않음" meansNested { customer.name.isNotBlank()}
            }

            "특별 주문 조건" quickFix "아이템 X 추가 또는 금액 증가" meansAnyOf {
                "아이템 X 포함 여부" meansNested { order.items has "X" }
                "주문 총액 조건" meansNested { order.amount >= 50000 }
            }
        }
    }

    override fun validatePost(input: Pair<Order, Customer>, output: Order) {
        conditions {
            "반환된 주문 상태는 APPROVED 여야 합니다" means { output.status == OrderStatus.APPROVED }
        }
    }
}