package com.team2073.common.mediator.conflict

import com.team2073.common.mediator.condition.Condition
import com.team2073.common.mediator.condition.StateBasedCondition
import com.team2073.common.mediator.request.Request
import com.team2073.common.mediator.subsys.ColleagueSubsystem
import com.team2073.common.mediator.subsys.SubsystemStateCondition

class StateBasedConflict<OS : ColleagueSubsystem<SubsystemStateCondition>, OC : Condition, CC : Condition, CS : ColleagueSubsystem<SubsystemStateCondition>>(
        var originSubsystemS: Class<OS>,
        var originConditionS: OC,
        var conflictingSubsystemS: Class<CS>,
        var conflictingConditionS: CC,
        var resolveState: SubsystemStateCondition) :
        Conflict<SubsystemStateCondition, OS, OC, SubsystemStateCondition, CC, CS>(originSubsystemS, originConditionS, conflictingSubsystemS, conflictingConditionS) {

    override fun invert(): Conflict<SubsystemStateCondition, OS, OC, SubsystemStateCondition, CC, CS> {
        return StateBasedConflict(originSubsystemS, originConditionS, conflictingSubsystemS, conflictingConditionS, resolveState)
    }

    override fun isRequestConflicting(request: Request<SubsystemStateCondition, CC, OS>, conflictingCondition: CC): Boolean {
        return originCondition.isInCondition(request.condition) && conflictingCondition.isInCondition(conflictingConditionS)
    }

    override fun isConditionConflicting(originCondition: OC, conflictingCondition: CC): Boolean {
        return originCondition == originConditionS && conflictingCondition == conflictingConditionS
    }

    override fun getResolution(currentCondition: CC, subsystem: CS): Condition {
        return StateBasedCondition(resolveState)
    }

}