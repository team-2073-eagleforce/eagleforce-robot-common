package com.team2073.common.mediator.conflict

import com.team2073.common.mediator.condition.Condition
import com.team2073.common.mediator.condition.StateBasedCondition
import com.team2073.common.mediator.request.Request
import com.team2073.common.mediator.subsys.ColleagueSubsystem
import com.team2073.common.mediator.subsys.SubsystemStateCondition

class StateBasedConflict<OS : ColleagueSubsystem, OC : Condition, CC : Condition, CS : ColleagueSubsystem>(var originSubsystemS: Class<OS>,
                                                                                                           var originConditionS: OC,
                                                                                                           var conflictingSubsystemS: Class<CS>,
                                                                                                           var conflictingConditionS: CC,
                                                                                                           var resolveState: SubsystemStateCondition) :
        Conflict<OS, OC, CC, CS>(originSubsystemS, originConditionS, conflictingSubsystemS, conflictingConditionS) {
    override fun getResolution(currentCondition: Condition, subsystem: ColleagueSubsystem): Condition {
        return StateBasedCondition(resolveState)
    }

    override fun isConflicting(conflict: Conflict<OS, CC, OC, OS>, request: Request<CC, OS>, currentCondition: Condition): Boolean {
        return conflict.originCondition.isInCondition(request.condition) && currentCondition.isInCondition(conflict.conflictingCondition)
    }

    override fun invert(): Conflict<OS, OC, CC, CS> {
        return StateBasedConflict(originSubsystemS, originConditionS, conflictingSubsystemS, conflictingConditionS, resolveState)
    }
}