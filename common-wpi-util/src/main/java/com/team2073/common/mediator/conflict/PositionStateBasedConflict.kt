package com.team2073.common.mediator.conflict

import com.team2073.common.mediator.condition.Condition
import com.team2073.common.mediator.condition.StateBasedCondition
import com.team2073.common.mediator.request.Request
import com.team2073.common.mediator.subsys.ColleagueSubsystem
import com.team2073.common.mediator.subsys.SubsystemStateCondition

class PositionStateBasedConflict<T: Enum<T>>(
        var originSubsystemPS: Class<out ColleagueSubsystem<Double>>,
        var originConditionPS: Condition<Double>,
        var conflictingSubsystemPS: Class<out ColleagueSubsystem<SubsystemStateCondition<T>>>,
        var conflictingConditionPS: Condition<SubsystemStateCondition<T>>,
        var resolveState: SubsystemStateCondition<T>?) :
        Conflict<Double, SubsystemStateCondition<T>>(originSubsystemPS, originConditionPS, conflictingSubsystemPS, conflictingConditionPS) {

    override fun isConditionConflicting(originCondition: Condition<Double>, conflictingCondition: Condition<SubsystemStateCondition<T>>): Boolean {
        return originCondition == originConditionPS && conflictingCondition == conflictingConditionPS
    }

    override fun getResolution(currentCondition: Condition<SubsystemStateCondition<T>>, subsystem: ColleagueSubsystem<SubsystemStateCondition<T>>): Condition<SubsystemStateCondition<T>> {
        return StateBasedCondition(resolveState)
    }

    override fun isRequestConflicting(request: Request<Double>, conflictingCondition: Condition<SubsystemStateCondition<T>>): Boolean {
        return conflictingCondition.isInCondition(conflictingConditionPS)
                && originCondition.isInCondition(request.condition)
    }

    override fun invert(): Conflict<SubsystemStateCondition<T>, Double> {
        return StatePositionBasedConflict(conflictingSubsystemPS, conflictingConditionPS, originSubsystemPS, originConditionPS)
    }

}