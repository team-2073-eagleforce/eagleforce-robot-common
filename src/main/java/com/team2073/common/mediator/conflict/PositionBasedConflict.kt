package com.team2073.common.mediator.conflict

import com.team2073.common.mediator.Tracker.PositionBasedTracker
import com.team2073.common.mediator.condition.Condition
import com.team2073.common.mediator.condition.PositionBasedCondition
import com.team2073.common.mediator.request.Request
import com.team2073.common.mediator.subsys.ColleagueSubsystem

class PositionBasedConflict<O : Condition, C : Condition, Z : ColleagueSubsystem>(var originSubsystemP: Class<Z>,
                                                                                  var originConditionP: O,
                                                                                  var conflictingSubsystemP: Class<Z>,
                                                                                  var conflictingConditionP: C) :
		Conflict<O, C, Z>(originSubsystemP, originConditionP, conflictingSubsystemP, conflictingConditionP) {
    override fun <Double> getResolution(): Double {
        return (conflictingConditionP as PositionBasedCondition).findClosestBound(PositionBasedTracker.findSubsystemCondition(conflictingSubsystemP)) as Double
    }

    override fun isConflicting(conflict: Conflict<C, O, Z>, request: Request<O, Z>): Boolean {
        return conflict.originCondition.isInCondition(request.condition) && PositionBasedTracker.findSubsystemCondition(conflict.conflictingSubsystem).isInCondition(conflict.conflictingCondition)
    }

        override fun invert(): Conflict<C, O, Z> {
            return PositionBasedConflict(conflictingSubsystemP, conflictingConditionP, originSubsystemP, originConditionP)
        }


}