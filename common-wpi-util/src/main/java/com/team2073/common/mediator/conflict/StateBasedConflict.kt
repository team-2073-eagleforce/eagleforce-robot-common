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

    override fun isConditionConflicting(originCondition: Condition, conflictingCondition: Condition): Boolean {
        return originCondition == originConditionS && conflictingCondition == conflictingConditionS
    }

    override fun getResolution(currentCondition: Condition, subsystem: ColleagueSubsystem): Condition {
        return StateBasedCondition(resolveState)
    }

    override fun isRequestConflicting(request: Request<CC, OS>, conflictingCondition: Condition): Boolean {
        return originCondition.isInCondition(request.condition) && conflictingCondition.isInCondition(conflictingConditionS)
    }

    override fun invert(): Conflict<OS, OC, CC, CS> {
        return StateBasedConflict(originSubsystemS, originConditionS, conflictingSubsystemS, conflictingConditionS, resolveState)
    }
}