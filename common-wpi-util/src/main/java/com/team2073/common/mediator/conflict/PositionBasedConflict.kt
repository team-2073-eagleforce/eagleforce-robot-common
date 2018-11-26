package com.team2073.common.mediator.conflict

import com.team2073.common.mediator.condition.Condition
import com.team2073.common.mediator.condition.PositionBasedCondition
import com.team2073.common.mediator.request.Request
import com.team2073.common.mediator.subsys.ColleagueSubsystem
import com.team2073.common.mediator.subsys.PositionBasedSubsystem

class PositionBasedConflict<O : Condition, C : Condition, Z : ColleagueSubsystem>(var originSubsystemP: Class<Z>,
                                                                                  var originConditionP: O,
                                                                                  var conflictingSubsystemP: Class<Z>,
                                                                                  var conflictingConditionP: C) :
        Conflict<O, C, Z>(originSubsystemP, originConditionP, conflictingSubsystemP, conflictingConditionP) {
    override fun getResolution(currentCondition: Condition, subsystem: ColleagueSubsystem): Condition {
        var closestBound = (conflictingConditionP as PositionBasedCondition).findClosestBound(currentCondition)
        var safetyRange = (subsystem as PositionBasedSubsystem).getSafetyRange()
        lateinit var resolutionCondition: Condition
        val islowerBound = (conflictingConditionP as PositionBasedCondition).isLowerBound(closestBound)

        if (islowerBound == null) {
            println("bound not upper/lower in condition")
        } else if (islowerBound) {
            resolutionCondition = PositionBasedCondition(closestBound - safetyRange,
                    closestBound,
                    ((closestBound - safetyRange) + (closestBound)) / 2)
        }else if(!islowerBound){
            resolutionCondition = PositionBasedCondition(closestBound,
                    closestBound + safetyRange,
                    ((closestBound + safetyRange) + (closestBound)) / 2)
        }
        return resolutionCondition
    }

    override fun isConflicting(conflict: Conflict<C, O, Z>, request: Request<C, Z>, currentCondition: Condition): Boolean {
        return conflict.originCondition.isInCondition(request.condition) && currentCondition.isInCondition(conflict.conflictingCondition)
    }

    override fun invert(): Conflict<C, O, Z> {
        return PositionBasedConflict(conflictingSubsystemP, conflictingConditionP, originSubsystemP, originConditionP)
    }


}