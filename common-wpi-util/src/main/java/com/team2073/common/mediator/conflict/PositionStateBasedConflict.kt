package com.team2073.common.mediator.conflict

import com.team2073.common.mediator.condition.Condition
import com.team2073.common.mediator.condition.StateBasedCondition
import com.team2073.common.mediator.request.Request
import com.team2073.common.mediator.subsys.ColleagueSubsystem
import com.team2073.common.mediator.subsys.SubsystemStateCondition

class PositionStateBasedConflict<OS : ColleagueSubsystem, OC : Condition, CC : Condition, CS : ColleagueSubsystem>(var originSubsystemPS: Class<OS>,
                                                                                                                var originConditionPS: OC,
                                                                                                                var conflictingSubsystemPS: Class<CS>,
                                                                                                                var conflictingConditionPS: CC,
                                                                                                                var resolveState: SubsystemStateCondition) :
        Conflict<OS, OC, CC, CS>(originSubsystemPS, originConditionPS, conflictingSubsystemPS, conflictingConditionPS) {

    override fun getResolution(currentCondition: Condition, subsystem: ColleagueSubsystem): Condition {
        return StateBasedCondition(resolveState)
    }

    override fun isConflicting(conflict: Conflict<OS, CC, OC, OS>, request: Request<CC, OS>, currentCondition: Condition): Boolean {
        return currentCondition.isInCondition(conflict.conflictingCondition)
                && conflict.originCondition.isInCondition(request.condition)
    }

    override fun invert(): Conflict<OS, OC, CC, CS> {
        return PositionStateBasedConflict(originSubsystemPS, originConditionPS, conflictingSubsystemPS, conflictingConditionPS, resolveState)
    }

}