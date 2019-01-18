package com.team2073.common.mediator.conflict

import com.team2073.common.mediator.condition.Condition
import com.team2073.common.mediator.condition.StateBasedCondition
import com.team2073.common.mediator.request.Request
import com.team2073.common.mediator.subsys.ColleagueSubsystem
import com.team2073.common.mediator.subsys.SubsystemStateCondition

class PositionStateBasedConflict<OS : ColleagueSubsystem<Double>, OC : Condition, CS : ColleagueSubsystem<SubsystemStateCondition>, CC : Condition>(
        var originSubsystemPS: Class<OS>,
        var originConditionPS: OC,
        var conflictingSubsystemPS: Class<CS>,
        var conflictingConditionPS: CC,
        var resolveState: SubsystemStateCondition) :
        Conflict<Double, OS, OC, SubsystemStateCondition, CC, CS>(originSubsystemPS, originConditionPS, conflictingSubsystemPS, conflictingConditionPS) {

    override fun isConditionConflicting(originCondition: OC, conflictingCondition: CC): Boolean {
        return originCondition == originConditionPS && conflictingCondition == conflictingConditionPS
    }

    override fun getResolution(currentCondition: CC, subsystem: CS): Condition {
        return StateBasedCondition(resolveState)
    }

    override fun isRequestConflicting(request: Request<Double, CC, OS>, conflictingCondition: CC): Boolean {
        return conflictingCondition.isInCondition(conflictingConditionPS)
                && originCondition.isInCondition(request.condition)
    }

    override fun invert(): Conflict<Double, OS, OC, SubsystemStateCondition, CC, CS> {
        return PositionStateBasedConflict(originSubsystemPS, originConditionPS, conflictingSubsystemPS, conflictingConditionPS, resolveState)
    }

}