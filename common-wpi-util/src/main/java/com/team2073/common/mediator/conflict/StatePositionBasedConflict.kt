package com.team2073.common.mediator.conflict

import com.team2073.common.mediator.condition.Condition
import com.team2073.common.mediator.condition.PositionBasedCondition
import com.team2073.common.mediator.request.Request
import com.team2073.common.mediator.subsys.ColleagueSubsystem
import com.team2073.common.mediator.subsys.PositionBasedSubsystem
import com.team2073.common.mediator.subsys.SubsystemStateCondition

class StatePositionBasedConflict<T : Enum<T>>(val originSubsystemPS: Class<out ColleagueSubsystem<SubsystemStateCondition<T>>>,
                                              val originConditionPS: Condition<SubsystemStateCondition<T>>,
                                              val conflictingSubsystemPS: Class<out ColleagueSubsystem<Double>>,
                                              val conflictingConditionPS: Condition<Double>,
                                              val inverseResolveState: SubsystemStateCondition<T>?) :
        Conflict<SubsystemStateCondition<T>, Double>(originSubsystemPS, originConditionPS, conflictingSubsystemPS, conflictingConditionPS){

    override fun isConditionConflicting(originCondition: Condition<SubsystemStateCondition<T>>, conflictingCondition: Condition<Double>): Boolean {
        return originCondition == originConditionPS && conflictingCondition == conflictingConditionPS
    }

    override fun getResolution(currentCondition: Condition<Double>, subsystem: ColleagueSubsystem<Double>): Condition<Double> {
        val closestBound = (conflictingConditionPS as PositionBasedCondition).findClosestBound(currentCondition)
        val safetyRange = (subsystem as PositionBasedSubsystem).getSafetyRange()
        lateinit var resolutionCondition: Condition<Double>
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

    override fun isRequestConflicting(request: Request<SubsystemStateCondition<T>>, conflictingCondition: Condition<Double>): Boolean {

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

    override fun invert(): Conflict<Double, SubsystemStateCondition<T>> {
        return PositionStateBasedConflict(conflictingSubsystemPS, conflictingConditionPS, originSubsystemPS, originConditionPS, inverseResolveState)
    }
}