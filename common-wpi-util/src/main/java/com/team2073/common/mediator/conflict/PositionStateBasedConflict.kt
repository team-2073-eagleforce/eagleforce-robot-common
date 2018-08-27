package com.team2073.common.mediator.conflict

import com.team2073.common.mediator.Tracker.StateBasedTracker
import com.team2073.common.mediator.condition.Condition
import com.team2073.common.mediator.request.Request
import com.team2073.common.mediator.subsys.ColleagueSubsystem
import com.team2073.common.mediator.subsys.SubsystemStateCondition

class PositionStateBasedConflict<O : Condition, C : Condition, Z : ColleagueSubsystem>(var originSubsystemPS: Class<Z>,
                                                                                       var originConditionPS: O,
                                                                                       var conflictingSubsystemPS: Class<Z>,
                                                                                       var conflictingConditionPS: C,
                                                                                       var resolveState: SubsystemStateCondition) :
        Conflict<O, C, Z>(originSubsystemPS, originConditionPS, conflictingSubsystemPS, conflictingConditionPS) {

    override fun <SubsystemStateCondition> getResolution(): SubsystemStateCondition {
        return resolveState as SubsystemStateCondition
    }

    override fun isConflicting(conflict: Conflict<C, O, Z>, request: Request<O, Z>): Boolean {

        var conflictCase = false
        var originCase = false

        if (StateBasedTracker.findSubsystemCondition(conflict.conflictingSubsystem).isInCondition(conflict.conflictingCondition)) {
            conflictCase = true
        }
         if (conflict.originCondition.isInCondition(request.condition)){
             originCase = true
        }

        return originCase && conflictCase

    }

    override fun invert(): Conflict<C, O, Z> {
        return PositionStateBasedConflict(conflictingSubsystemPS, conflictingConditionPS, originSubsystemPS, originConditionPS, resolveState)
    }

}