package com.team2073.common.mediator.conflict

import com.team2073.common.mediator.condition.Condition
import com.team2073.common.mediator.condition.PositionBasedCondition
import com.team2073.common.mediator.request.Request
import com.team2073.common.mediator.subsys.ColleagueSubsystem
import com.team2073.common.mediator.subsys.PositionBasedSubsystem

class StatePositionBasedConflict<OS : ColleagueSubsystem, OC : Condition, CC : Condition, CS : ColleagueSubsystem>(var originSubsystemPS: Class<OS>,
                                                                                                                   var originConditionPS: OC,
                                                                                                                   var conflictingSubsystemPS: Class<CS>,
                                                                                                                   var conflictingConditionPS: CC,
                                                                                                                   var resolveState: Double) :
        Conflict<OS, OC, CC, CS>(originSubsystemPS, originConditionPS, conflictingSubsystemPS, conflictingConditionPS) {

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
        } else if (!islowerBound) {
            resolutionCondition = PositionBasedCondition(closestBound,
                    closestBound + safetyRange,
                    ((closestBound + safetyRange) + (closestBound)) / 2)
        }
        return resolutionCondition
    }

    override fun isConflicting(conflict: Conflict<OS, CC, OC, OS>, request: Request<CC, OS>, currentCondition: Condition): Boolean {

        var conflictCase = false
        var originCase = false

        if (currentCondition.isInCondition(conflict.conflictingCondition)) {
            conflictCase = true
        }
        if (conflict.originCondition.isInCondition(request.condition)) {
            originCase = true
        }

        return originCase && conflictCase

    }

    override fun invert(): Conflict<OS, OC, CC, CS> {
        return StatePositionBasedConflict(originSubsystemPS, originConditionPS, conflictingSubsystemPS, conflictingConditionPS, resolveState)
    }

}