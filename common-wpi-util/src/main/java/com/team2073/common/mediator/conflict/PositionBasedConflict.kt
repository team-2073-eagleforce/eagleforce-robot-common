package com.team2073.common.mediator.conflict

import com.team2073.common.mediator.condition.Condition
import com.team2073.common.mediator.condition.PositionBasedCondition
import com.team2073.common.mediator.request.Request
import com.team2073.common.mediator.subsys.ColleagueSubsystem
import com.team2073.common.mediator.subsys.PositionBasedSubsystem
import org.apache.commons.lang3.Range

class PositionBasedConflict(
        val originSubsystemP: Class<out ColleagueSubsystem<Double>>,
        val originConditionP: Condition<Double>,
        val conflictingSubsystemP: Class<out ColleagueSubsystem<Double>>,
        val conflictingConditionP: Condition<Double>) :
        Conflict<Double, Double>(originSubsystemP, originConditionP, conflictingSubsystemP, conflictingConditionP) {

    override fun isConditionConflicting(originCondition: Condition<Double>, conflictingCondition: Condition<Double>): Boolean {
        return originCondition == originConditionP && conflictingCondition == conflictingConditionP
    }

    override fun getResolution(currentCondition: Condition<Double>, subsystem: ColleagueSubsystem<Double>): Condition<Double> {
        val closestBound = (conflictingConditionP as PositionBasedCondition).findClosestBound(currentCondition)
        val safetyRange = (subsystem as PositionBasedSubsystem).getSafetyRange()

        return PositionBasedCondition(closestBound, Range.between(closestBound - safetyRange, closestBound + safetyRange))

    }

    override fun isRequestConflicting(request: Request<Double>, currentConflictingCondition: Condition<Double>, currentOriginCondition: Condition<Double>): Boolean {
        val range: Range<Double> = Range.between(currentOriginCondition.getConditionValue(), request.condition.getConditionValue())
        val conflictingRange: Range<Double> = Range.between((currentConflictingCondition as PositionBasedCondition).range.minimum,
                currentConflictingCondition.range.maximum)

        return range.isOverlappedBy(conflictingRange)
    }

    override fun invert(): Conflict<Double, Double> {
        return PositionBasedConflict(conflictingSubsystemP, conflictingConditionP, originSubsystemP, originConditionP)
    }
}