package com.team2073.common.mediator.conflict

import com.team2073.common.mediator.condition.Condition
import com.team2073.common.mediator.condition.StateBasedCondition
import com.team2073.common.mediator.request.Request
import com.team2073.common.mediator.subsys.ColleagueSubsystem
import com.team2073.common.mediator.subsys.SubsystemStateCondition

class PositionStateBasedConflict<O : Condition, C : Condition, Z : ColleagueSubsystem>(var originSubsystemPS: Class<Z>,
                                                                                       var originConditionPS: O,
                                                                                       var conflictingSubsystemPS: Class<Z>,
                                                                                       var conflictingConditionPS: C,
                                                                                       var resolveState: SubsystemStateCondition) :
        Conflict<O, C, Z>(originSubsystemPS, originConditionPS, conflictingSubsystemPS, conflictingConditionPS) {

    override fun getResolution(currentCondition: Condition, subsystem: ColleagueSubsystem): Condition {
        return StateBasedCondition(resolveState)
    }

    override fun isConflicting(conflict: Conflict<C, O, Z>, request: Request<O, Z>, currentCondition: Condition): Boolean {
        return currentCondition.isInCondition(conflict.conflictingCondition)
                && conflict.originCondition.isInCondition(request.condition)
    }

    override fun invert(): Conflict<C, O, Z> {
        return PositionStateBasedConflict(conflictingSubsystemPS, conflictingConditionPS, originSubsystemPS, originConditionPS, resolveState)
    }

}