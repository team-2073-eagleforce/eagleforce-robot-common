package com.team2073.common.mediator.conflict

import com.team2073.common.mediator.condition.Condition
import com.team2073.common.mediator.condition.StateBasedCondition
import com.team2073.common.mediator.request.Request
import com.team2073.common.mediator.subsys.ColleagueSubsystem
import com.team2073.common.mediator.subsys.SubsystemStateCondition

class PositionStateBasedConflict<OS : ColleagueSubsystem, OC : Condition, CS : ColleagueSubsystem, CC : Condition>(var originSubsystemPS: Class<OS>,
                                                                                                                var originConditionPS: OC,
                                                                                                                var conflictingSubsystemPS: Class<CS>,
                                                                                                                var conflictingConditionPS: CC,
                                                                                                                var resolveState: SubsystemStateCondition) :
        Conflict<OS, OC, CC, CS>(originSubsystemPS, originConditionPS, conflictingSubsystemPS, conflictingConditionPS) {

    override fun isConditionConflicting(originCondition: Condition, conflictingCondition: Condition): Boolean {
        return originCondition == originConditionPS && conflictingCondition == conflictingConditionPS
    }

    override fun getResolution(currentCondition: Condition, subsystem: ColleagueSubsystem): Condition {
        return StateBasedCondition(resolveState)
    }

    override fun isRequestConflicting(request: Request<CC, OS>, conflictingCondition: Condition): Boolean {
        return conflictingCondition.isInCondition(conflictingCondition)
                && originCondition.isInCondition(request.condition)
    }

    override fun invert(): Conflict<OS, OC, CC, CS> {
        return PositionStateBasedConflict(originSubsystemPS, originConditionPS, conflictingSubsystemPS, conflictingConditionPS, resolveState)
    }

}