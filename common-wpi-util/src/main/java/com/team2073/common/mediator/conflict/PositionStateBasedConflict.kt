package com.team2073.common.mediator.conflict

import com.team2073.common.mediator.condition.Condition
import com.team2073.common.mediator.condition.StateBasedCondition
import com.team2073.common.mediator.request.Request
import com.team2073.common.mediator.subsys.ColleagueSubsystem
import com.team2073.common.mediator.subsys.SubsystemStateCondition

class PositionStateBasedConflict(
        var originSubsystemPS: Class<ColleagueSubsystem<Double>>,
        var originConditionPS: Condition<Double>,
        var conflictingSubsystemPS: Class<ColleagueSubsystem<SubsystemStateCondition>>,
        var conflictingConditionPS: Condition<SubsystemStateCondition>,
        var resolveState: SubsystemStateCondition?) :
        Conflict<Double, SubsystemStateCondition>(originSubsystemPS, originConditionPS, conflictingSubsystemPS, conflictingConditionPS) {

    override fun isConditionConflicting(originCondition: Condition<Double>, conflictingCondition: Condition<SubsystemStateCondition>): Boolean {
        return originCondition == originConditionPS && conflictingCondition == conflictingConditionPS
    }

    override fun getResolution(currentCondition: Condition<SubsystemStateCondition>, subsystem: ColleagueSubsystem<SubsystemStateCondition>): Condition<SubsystemStateCondition> {
        return StateBasedCondition(resolveState)
    }

    override fun isRequestConflicting(request: Request<Double>, conflictingCondition: Condition<SubsystemStateCondition>): Boolean {
        return conflictingCondition.isInCondition(conflictingConditionPS)
                && originCondition.isInCondition(request.condition)
    }

    override fun invert(): Conflict<SubsystemStateCondition, Double> {
        return StatePositionBasedConflict(conflictingSubsystemPS, conflictingConditionPS, originSubsystemPS, originConditionPS)
    }

}