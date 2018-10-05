package com.team2073.common.mediator.conflict

import com.team2073.common.mediator.condition.Condition
import com.team2073.common.mediator.condition.StateBasedCondition
import com.team2073.common.mediator.request.Request
import com.team2073.common.mediator.subsys.ColleagueSubsystem
import com.team2073.common.mediator.subsys.SubsystemStateCondition

class SpecialCaseConflict<O : Condition, C : Condition, Z : ColleagueSubsystem>(var originSubsystemSC: Class<Z>,
                                                                                       var originConditionSC: O,
                                                                                       var conflictingSubsystemSC: Class<Z>,
                                                                                       var conflictingConditionSC: C,
                                                                                var resolution: Runnable) :
        Conflict<O, C, Z>(originSubsystemSC, originConditionSC, conflictingSubsystemSC, conflictingConditionSC) {
    override fun getResolution(currentCondition: Condition, subsystem: ColleagueSubsystem): Condition {
        return currentCondition
    }

    fun getSpecialResolution(): Runnable{
        return resolution
    }

    override fun isConflicting(conflict: Conflict<C, O, Z>, request: Request<C, Z>, currentCondition: Condition): Boolean {
        return currentCondition.isInCondition(conflict.conflictingCondition)
                && conflict.originCondition.isInCondition(request.condition)
    }

    override fun invert(): Conflict<C, O, Z> {
        return SpecialCaseConflict(conflictingSubsystemSC, conflictingConditionSC, originSubsystemSC, originConditionSC, resolution)
    }
}