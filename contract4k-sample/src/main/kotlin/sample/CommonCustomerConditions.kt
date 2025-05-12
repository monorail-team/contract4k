package sample

import condition.ConditionBuilder
import condition.ConditionGroup
import condition.util.Patterns.EMAIL
import condition.util.hasSub
import condition.util.matchesForm

object CommonCustomerConditions : ConditionGroup<Customer> {

    override fun ConditionBuilder.applyTo(target: Customer) {
        "이메일 형식이어야 합니다" means { target.email matchesForm EMAIL }
        "고객 이름에 'A'가 포함되어야 합니다" means { target.name hasSub "A" }
    }
}
