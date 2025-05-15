package condition

interface ConditionGroup<T> {

    fun ConditionBuilder.applyTo(target: T)
}

fun <T> ConditionBuilder.applyGroup(target: T, group: ConditionGroup<T>) {
    with(group) {
        this@applyGroup.applyTo(target)
    }
}