package com.team2073.robot.common.mediator.condition

import com.team2073.robot.common.mediator.subsys.SubsystemStateCondition

class StateBasedCondition(val state: SubsystemStateCondition?) : Condition {

    override fun <SubsystemStateCondition>getConditionValue(): SubsystemStateCondition {
        return state as SubsystemStateCondition
    }
//    return some type of identifier

    override fun isInCondition(condition: Condition): Boolean {
        return this.state == state
    }
}