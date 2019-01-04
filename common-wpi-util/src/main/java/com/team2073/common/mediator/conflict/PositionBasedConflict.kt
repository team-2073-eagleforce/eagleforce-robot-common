package com.team2073.common.mediator.conflict

import com.team2073.common.mediator.condition.Condition
import com.team2073.common.mediator.condition.PositionBasedCondition
import com.team2073.common.mediator.request.Request
import com.team2073.common.mediator.subsys.ColleagueSubsystem
import com.team2073.common.mediator.subsys.PositionBasedSubsystem

class PositionBasedConflict<OS : ColleagueSubsystem, OC : Condition,CS : ColleagueSubsystem, CC : Condition>(var originSubsystemP: Class<OS>,
                                                                                                              var originConditionP: OC,
                                                                                                             var conflictingSubsystemP: Class<CS>,
                                                                                                              var conflictingConditionP: CC):
        Conflict<OS, OC, CC, CS>(originSubsystemP, originConditionP, conflictingSubsystemP, conflictingConditionP) {
    override fun isConditionConflicting(originCondition: Condition, conflictingCondition: Condition): Boolean {
        return originCondition == originConditionP && conflictingCondition == conflictingConditionP
    }

    override fun getResolution(currentCondition: Condition, subsystem: ColleagueSubsystem): Condition {
        var closestBound = (conflictingConditionP as PositionBasedCondition).findClosestBound(currentCondition)
        var safetyRange = (subsystem as PositionBasedSubsystem).getSafetyRange()
        lateinit var resolutionCondition: Condition
        val islowerBound = (conflictingConditionP as PositionBasedCondition).isLowerBound(closestBound)

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

    override fun isRequestConflicting(request: Request<CC, OS>, conflictingCondition: Condition): Boolean {
        return originCondition.isInCondition(request.condition) && conflictingCondition.isInCondition(conflictingCondition)
    }

    override fun invert(): Conflict<OS, OC, CC, CS> {
        return PositionBasedConflict(originSubsystemP, originConditionP, conflictingSubsystemP, conflictingConditionP)
    }


}