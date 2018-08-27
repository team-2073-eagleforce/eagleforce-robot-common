package com.team2073.common.mediator.conflict

import com.team2073.common.mediator.Tracker.StateBasedTracker
import com.team2073.common.mediator.condition.Condition
import com.team2073.common.mediator.request.Request
import com.team2073.common.mediator.subsys.ColleagueSubsystem
import com.team2073.common.mediator.subsys.SubsystemStateCondition

class StateBasedConflict<O : Condition, C : Condition, Z : ColleagueSubsystem>(var originSubsystemS: Class<Z>,
                                                                               var originConditionS: O,
                                                                               var conflictingSubsystemS: Class<Z>,
                                                                               var conflictingConditionS: C,
                                                                               var resolveState: SubsystemStateCondition) :
		Conflict<O, C, Z>(originSubsystemS, originConditionS, conflictingSubsystemS, conflictingConditionS) {
    override fun <SubsystemStateCondition> getResolution(): SubsystemStateCondition {
        return resolveState as SubsystemStateCondition
    }

    override fun isConflicting(conflict: Conflict<C, O, Z>, request: Request<O, Z>): Boolean {
        return conflict.originCondition.isInCondition(request.condition) && StateBasedTracker.findSubsystemCondition(conflict.conflictingSubsystem).isInCondition(conflict.conflictingCondition)
    }

    override fun invert(): Conflict<C, O, Z> {
        return StateBasedConflict(conflictingSubsystemS, conflictingConditionS, originSubsystemS, originConditionS, resolveState)
    }
}