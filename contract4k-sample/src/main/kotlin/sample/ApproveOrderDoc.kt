package sample

import base.Contract4kDocSpec
import base.documentation

object ApproveOrderDoc : Contract4kDocSpec() {
    override val doc = documentation {
        description("주문을 승인한다.")
        param("order", "주문 객체")
        param("customer", "주문자")
        returns("승인된 주문 객체")
        author("선우")
        since("1.0.0")
    }
}