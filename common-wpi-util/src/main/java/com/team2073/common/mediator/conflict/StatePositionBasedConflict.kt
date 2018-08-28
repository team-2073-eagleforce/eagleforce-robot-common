package com.team2073.common.mediator.conflict

import com.team2073.common.mediator.condition.Condition
import com.team2073.common.mediator.condition.PositionBasedCondition
import com.team2073.common.mediator.request.Request
import com.team2073.common.mediator.subsys.ColleagueSubsystem
import com.team2073.common.mediator.subsys.PositionBasedSubsystem

class StatePositionBasedConflict<O : Condition, C : Condition, Z : ColleagueSubsystem>(var originSubsystemPS: Class<Z>,
                                                                                       var originConditionPS: O,
                                                                                       var conflictingSubsystemPS: Class<Z>,
                                                                                       var conflictingConditionPS: C,
                                                                                       var resolveState: Double) :
        Conflict<O, C, Z>(originSubsystemPS, originConditionPS, conflictingSubsystemPS, conflictingConditionPS) {

    override fun getResolution(currentCondition: Condition, subsystem: ColleagueSubsystem): Condition {
        var closestBound = (conflictingConditionPS as PositionBasedCondition).findClosestBound(currentCondition)
        var safetyRange = (subsystem as PositionBasedSubsystem).getSafetyRange()
        lateinit var resolutionCondition: Condition
        val islowerBound = (conflictingConditionPS as PositionBasedCondition).isLowerBound(closestBound)

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

    override fun isConflicting(conflict: Conflict<C, O, Z>, request: Request<O, Z>, currentCondition: Condition): Boolean {

        var conflictCase = false
        var originCase = false

        if(currentCondition.isInCondition(conflict.conflictingCondition)) {
            conflictCase = true
        }
        if (conflict.originCondition.isInCondition(request.condition)){
            originCase = true
        }

        return originCase && conflictCase

    }

    override fun invert(): Conflict<C, O, Z> {
        return StatePositionBasedConflict(conflictingSubsystemPS, conflictingConditionPS, originSubsystemPS, originConditionPS, resolveState)
    }

}