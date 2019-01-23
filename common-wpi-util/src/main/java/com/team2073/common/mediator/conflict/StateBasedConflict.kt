package com.team2073.common.mediator.conflict

import com.team2073.common.mediator.condition.Condition
import com.team2073.common.mediator.condition.StateBasedCondition
import com.team2073.common.mediator.request.Request
import com.team2073.common.mediator.subsys.ColleagueSubsystem
import com.team2073.common.mediator.subsys.SubsystemStateCondition

class StateBasedConflict<T: Enum<T>>(
        var originSubsystemS: Class<ColleagueSubsystem<SubsystemStateCondition<T>>>,
        var originConditionS: Condition<SubsystemStateCondition<T>>,
        var conflictingSubsystemS: Class<ColleagueSubsystem<SubsystemStateCondition<T>>>,
        var conflictingConditionS: Condition<SubsystemStateCondition<T>>,
        var resolveState: SubsystemStateCondition<T>) :
        Conflict<SubsystemStateCondition<T>, SubsystemStateCondition<T>>(originSubsystemS, originConditionS, conflictingSubsystemS, conflictingConditionS) {

    override fun invert(): Conflict<SubsystemStateCondition<T>, SubsystemStateCondition<T>> {
        return StateBasedConflict(originSubsystemS, originConditionS, conflictingSubsystemS, conflictingConditionS, resolveState)
    }

    override fun isRequestConflicting(request: Request<SubsystemStateCondition<T>>, conflictingCondition: Condition<SubsystemStateCondition<T>>): Boolean {
        return originCondition.isInCondition(request.condition) && conflictingCondition.isInCondition(conflictingConditionS)
    }

    override fun isConditionConflicting(originCondition: Condition<SubsystemStateCondition<T>>, conflictingCondition: Condition<SubsystemStateCondition<T>>): Boolean {
        return originCondition == originConditionS && conflictingCondition == conflictingConditionS
    }

    override fun getResolution(currentCondition: Condition<SubsystemStateCondition<T>>, subsystem: ColleagueSubsystem<SubsystemStateCondition<T>>): Condition<SubsystemStateCondition<T>> {
        return StateBasedCondition(resolveState)
    }

}