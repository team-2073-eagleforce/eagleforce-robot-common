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
        val closestBound = (conflictingConditionP as PositionBasedCondition).findClosestBound(currentCondition)
        val safetyRange = (subsystem as PositionBasedSubsystem).getSafetyRange()

        return PositionBasedCondition(closestBound, Range.between(closestBound - safetyRange, closestBound + safetyRange))

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

        nearestOriginSafeX = if(originPoint.x > conflictingPoint.x){
            conflictingPoint.x - conflictingSafetyRange - originSubsystem.getSafetyRange()
        }else{
            conflictingPoint.x + conflictingSafetyRange + originSubsystem.getSafetyRange()
        }

        val nearestOriginSafePoint = Vector2D(nearestOriginSafeX, nearestOriginSafeY)
        val nearestOriginSafePosition = originSubsystem.pointToPosition(nearestOriginSafePoint)

        return PositionBasedCondition(nearestOriginSafePosition,
                Range.between(nearestOriginSafePosition - originSafetyRange, nearestOriginSafePosition + originSafetyRange))
    }

    override fun isRequestConflicting(request: Request<Double>, currentConflictingCondition: Condition<Double>, currentOriginCondition: Condition<Double>): Boolean {
        val range: Range<Double> = Range.between(currentOriginCondition.getConditionValue(), request.condition.getConditionValue())
        val conflictingRange: Range<Double> = Range.between((currentConflictingCondition as PositionBasedCondition).range.minimum,
                currentConflictingCondition.range.maximum)

        return range.isOverlappedBy(conflictingRange)
    }

    override fun invert(): Conflict<Double, Double> {
        return PositionBasedConflict(conflictingSubsystemP, conflictingConditionP, originSubsystemP, originConditionP, canInvertP, parallelismP)
    }
}