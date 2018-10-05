package com.team2073.common.mediator.conflict

import com.team2073.common.mediator.condition.Condition
import com.team2073.common.mediator.request.Request
import com.team2073.common.mediator.subsys.ColleagueSubsystem

class NoResolutionConflict<O : Condition, C : Condition, Z : ColleagueSubsystem>(var originSubsystemN: Class<Z>,
                                                                                 var originConditionN: O,
                                                                                 var conflictingSubsystemN: Class<Z>,
                                                                                 var conflictingConditionN: C) :
        Conflict<O, C, Z>(originSubsystemN, originConditionN, conflictingSubsystemN, conflictingConditionN) {

    override fun invert(): Conflict<C, O, Z> {
        return NoResolutionConflict(conflictingSubsystemN, conflictingConditionN, originSubsystemN, originConditionN)
    }

    override fun isConflicting(conflict: Conflict<C, O, Z>, request: Request<C, Z>, currentCondition: Condition): Boolean {
        return currentCondition.isInCondition(conflict.conflictingCondition)
                && conflict.originCondition.isInCondition(request.condition)
    }

    override fun getResolution(currentCondition: Condition, subsystem: ColleagueSubsystem): Condition {
        return currentCondition
    }
}