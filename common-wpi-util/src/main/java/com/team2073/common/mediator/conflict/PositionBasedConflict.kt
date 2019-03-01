package com.team2073.common.mediator.conflict

import com.team2073.common.mediator.condition.Condition
import com.team2073.common.mediator.condition.PositionBasedCondition
import com.team2073.common.mediator.request.Request
import com.team2073.common.mediator.subsys.ColleagueSubsystem
import com.team2073.common.mediator.subsys.PositionBasedSubsystem
import org.apache.commons.lang3.Range
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D

class PositionBasedConflict(
        val originSubsystemP: Class<out ColleagueSubsystem<Double>>,
        val originConditionP: Condition<Double>,
        val conflictingSubsystemP: Class<out ColleagueSubsystem<Double>>,
        val conflictingConditionP: Condition<Double>,
        val canInvertP: Boolean,
        val parallelismP: Boolean) :
        Conflict<Double, Double>(originSubsystemP, originConditionP, conflictingSubsystemP, conflictingConditionP, canInvertP, parallelismP) {

    override fun isConditionConflicting(originCondition: Condition<Double>, conflictingCondition: Condition<Double>): Boolean {
        return originCondition == originConditionP && conflictingCondition == conflictingConditionP
    }

    override fun getResolution(currentCondition: Condition<Double>, subsystem: ColleagueSubsystem<Double>): Condition<Double> {
        var closestBound = (conflictingConditionP as PositionBasedCondition).findClosestBound(currentCondition)
        val safetyRange = (subsystem as PositionBasedSubsystem).getSafetyRange()

        if (closestBound - subsystem.getCurrentCondition().getConditionValue() > 0) {
            closestBound += safetyRange
        } else {
            closestBound -= safetyRange
        }
        return PositionBasedCondition(closestBound, Range.between(closestBound - safetyRange, closestBound + safetyRange))
    }

    fun isWithinBounds(lowerBound: Double, value: Double, upperBound: Double): Boolean {
        return Range.between(lowerBound, upperBound).contains(value)
    }

    override fun getOriginParallelResolution(originSubsystem: ColleagueSubsystem<Double>, conflictingSubsystem: ColleagueSubsystem<Double>): Condition<Double> {
        val originPoint: Vector2D = (originSubsystem as PositionBasedSubsystem).positionToPoint(originSubsystem.getCurrentCondition().getConditionValue())
        val conflictingPoint: Vector2D = (conflictingSubsystem as PositionBasedSubsystem).positionToPoint(conflictingSubsystem.getCurrentCondition().getConditionValue())
        val nearestOriginSafeY: Double
        val nearestOriginSafeX: Double
        val originSafetyRange = originSubsystem.getSafetyRange()
        val conflictingSafetyRange = conflictingSubsystem.getSafetyRange()

        nearestOriginSafeY = if (originPoint.y > conflictingPoint.y) {
            conflictingPoint.y - conflictingSafetyRange - originSubsystem.getSafetyRange()
        } else {
            conflictingPoint.y + conflictingSafetyRange + originSubsystem.getSafetyRange()
        }
        nearestOriginSafeX = if (originPoint.x > conflictingPoint.x) {
            conflictingPoint.x - conflictingSafetyRange - originSubsystem.getSafetyRange()
        } else {
            conflictingPoint.x + conflictingSafetyRange + originSubsystem.getSafetyRange()
        }

        val nearestOriginSafePoint = Vector2D(nearestOriginSafeX, nearestOriginSafeY)
        val nearestOriginSafePosition = originSubsystem.pointToPosition(nearestOriginSafePoint)

        //TODO needs to convert safety range to a relative point
        return if (originPoint.x - conflictingPoint.x < originSafetyRange || originPoint.y - conflictingPoint.y < originSafetyRange) {
            originSubsystem.getCurrentCondition()
        } else {
            PositionBasedCondition(nearestOriginSafePosition,
                    Range.between(nearestOriginSafePosition - originSafetyRange, nearestOriginSafePosition + originSafetyRange))
        }
    }

    override fun isRequestConflicting(request: Request<Double>, currentConflictingCondition: Condition<Double>, currentOriginCondition: Condition<Double>): Boolean {
        val originTravelRange: Range<Double> = Range.between(currentOriginCondition.getConditionValue(), request.condition.getConditionValue())
        val originConflictingRange: Range<Double> = (originConditionP as PositionBasedCondition).range
        val currentConflictingRange: Range<Double> = (currentConflictingCondition as PositionBasedCondition).range

        return originTravelRange.isOverlappedBy(originConflictingRange) && (conflictingConditionP as PositionBasedCondition).range.isOverlappedBy(currentConflictingRange)
    }

    override fun invert(): Conflict<Double, Double> {
        return PositionBasedConflict(conflictingSubsystemP, conflictingConditionP, originSubsystemP, originConditionP, canInvertP, parallelismP)
    }
}