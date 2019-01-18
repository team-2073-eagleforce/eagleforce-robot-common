package com.team2073.common.mediator.conflict

import com.team2073.common.mediator.condition.Condition
import com.team2073.common.mediator.condition.PositionBasedCondition
import com.team2073.common.mediator.request.Request
import com.team2073.common.mediator.subsys.ColleagueSubsystem
import com.team2073.common.mediator.subsys.PositionBasedSubsystem
import com.team2073.common.mediator.subsys.SubsystemStateCondition

class StatePositionBasedConflict<OS : ColleagueSubsystem<SubsystemStateCondition>, OC : Condition, CS : ColleagueSubsystem<Double>, CC : Condition>(var originSubsystemPS: Class<OS>,
                                                                                                                   var originConditionPS: OC,
                                                                                                                   var conflictingSubsystemPS: Class<CS>,
                                                                                                                   var conflictingConditionPS: CC) :
        Conflict<SubsystemStateCondition, OS, OC, Double, CC, CS>(originSubsystemPS, originConditionPS, conflictingSubsystemPS, conflictingConditionPS) {

    override fun isConditionConflicting(originCondition: OC, conflictingCondition: CC): Boolean {
        return originCondition == originConditionPS && conflictingCondition == conflictingConditionPS
    }

    override fun getResolution(currentCondition: CC, subsystem: CS): Condition {
        var closestBound = (conflictingConditionPS as PositionBasedCondition).findClosestBound(currentCondition)
        var safetyRange = (subsystem as PositionBasedSubsystem).getSafetyRange()
        lateinit var resolutionCondition: Condition
        val islowerBound = (conflictingConditionPS as PositionBasedCondition).isLowerBound(closestBound)

        if (islowerBound == null) {
            println("bound not upper/lower in condition")
        } else if (islowerBound) {
            resolutionCondition = PositionBasedCondition(closestBound - safetyRange,
                    ((closestBound - safetyRange) + (closestBound)) / 2,
                    closestBound)
        } else if (!islowerBound) {
            resolutionCondition = PositionBasedCondition(closestBound,
                    ((closestBound + safetyRange) + (closestBound)) / 2,
                    closestBound + safetyRange)
        }
        return resolutionCondition
    }

    override fun isRequestConflicting(request: Request<SubsystemStateCondition, CC, OS>, conflictingCondition: CC): Boolean {

        var conflictCase = false
        var originCase = false

        if (conflictingCondition.isInCondition(conflictingConditionPS)) {
            conflictCase = true
        }
        if (originCondition.isInCondition(request.condition)) {
            originCase = true
        }

        return originCase && conflictCase

    }

    override fun invert(): Conflict<SubsystemStateCondition, OS, OC, Double, CC, CS> {
        return StatePositionBasedConflict(originSubsystemPS, originConditionPS, conflictingSubsystemPS, conflictingConditionPS)
    }

}