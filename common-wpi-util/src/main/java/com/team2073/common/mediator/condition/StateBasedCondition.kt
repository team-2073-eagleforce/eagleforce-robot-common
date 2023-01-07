package com.team2073.common.mediator.condition

import com.team2073.common.mediator.subsys.SubsystemStateCondition

class StateBasedCondition<T : Enum<T>>(val state: SubsystemStateCondition<T>?) : Condition<SubsystemStateCondition<T>> {

    override fun getConditionValue(): SubsystemStateCondition<T> {
        return state as SubsystemStateCondition<T>
    }

    override fun isInCondition(condition: Condition<SubsystemStateCondition<T>>): Boolean {
        return state == condition.getConditionValue()
    }

    override fun toString(): String {
        return "A state condition with value [" + state.toString() + "]"
    }
}