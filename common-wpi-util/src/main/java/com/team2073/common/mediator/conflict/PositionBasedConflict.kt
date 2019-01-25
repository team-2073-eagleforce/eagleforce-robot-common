package com.team2073.common.mediator.conflict

import com.team2073.common.mediator.condition.Condition
import com.team2073.common.mediator.condition.PositionBasedCondition
import com.team2073.common.mediator.request.Request
import com.team2073.common.mediator.subsys.ColleagueSubsystem
import com.team2073.common.mediator.subsys.PositionBasedSubsystem

class PositionBasedConflict(
        var originSubsystemP: Class<out ColleagueSubsystem<Double>>,
        var originConditionP: Condition<Double>,
        var conflictingSubsystemP: Class<out ColleagueSubsystem<Double>>,
        var conflictingConditionP: Condition<Double>) :
        Conflict<Double, Double>(originSubsystemP, originConditionP, conflictingSubsystemP, conflictingConditionP) {

    override fun isConditionConflicting(originCondition: Condition<Double>, conflictingCondition: Condition<Double>): Boolean {
        return originCondition == originConditionP && conflictingCondition == conflictingConditionP
    }

    override fun getResolution(currentCondition: Condition<Double>, subsystem: ColleagueSubsystem<Double>): Condition<Double> {
        var closestBound = (conflictingConditionP as PositionBasedCondition).findClosestBound(currentCondition)
        var safetyRange = (subsystem as PositionBasedSubsystem).getSafetyRange()
        lateinit var resolutionCondition: Condition<Double>
        val isLowerBound = (conflictingConditionP as PositionBasedCondition).isLowerBound(closestBound)

        if (isLowerBound == null) {
            println("bound not upper/lower in condition")
        } else if (isLowerBound) {
            resolutionCondition = PositionBasedCondition(closestBound - safetyRange,
                    ((closestBound - safetyRange) + (closestBound)) / 2,
                    closestBound)
        } else if (!isLowerBound) {
            resolutionCondition = PositionBasedCondition(closestBound,
                    ((closestBound + safetyRange) + (closestBound)) / 2,
                    closestBound + safetyRange)
        }
        return resolutionCondition
    }

    override fun isRequestConflicting(request: Request<Double>, conflictingCondition: Condition<Double>): Boolean {
        return originCondition.isInCondition(request.condition) && conflictingCondition.isInCondition(conflictingConditionP)
    }

    override fun invert(): Conflict<Double, Double> {
        return PositionBasedConflict(conflictingSubsystemP, conflictingConditionP, originSubsystemP, originConditionP)
    }
}