package com.team2073.common.mediator.conflict

import com.team2073.common.mediator.condition.Condition
import com.team2073.common.mediator.condition.PositionBasedCondition
import com.team2073.common.mediator.condition.StateBasedCondition
import com.team2073.common.mediator.request.Request
import com.team2073.common.mediator.subsys.ColleagueSubsystem
import com.team2073.common.mediator.subsys.PositionBasedSubsystem
import com.team2073.common.mediator.subsys.SubsystemStateCondition
import org.apache.commons.lang3.Range

class StatePositionBasedConflict<T : Enum<T>>(val originSubsystemPS: Class<out ColleagueSubsystem<SubsystemStateCondition<T>>>,
                                              val originConditionPS: Condition<SubsystemStateCondition<T>>,
                                              val conflictingSubsystemPS: Class<out ColleagueSubsystem<Double>>,
                                              val conflictingConditionPS: Condition<Double>,
                                              val inverseResolveState: SubsystemStateCondition<T>?,
                                              val canInvertPS: Boolean,
                                              val parallelismSP: Boolean) :
        Conflict<SubsystemStateCondition<T>, Double>(originSubsystemPS, originConditionPS, conflictingSubsystemPS, conflictingConditionPS, canInvertPS, parallelismSP){

    override fun getOriginInterimResolution(originSubsystem: ColleagueSubsystem<SubsystemStateCondition<T>>, conflictingSubsystem: ColleagueSubsystem<Double>): Condition<SubsystemStateCondition<T>> {
        return StateBasedCondition(originSubsystem.getCurrentCondition().getConditionValue())
    }

    override fun isConditionConflicting(originCondition: Condition<SubsystemStateCondition<T>>, conflictingCondition: Condition<Double>): Boolean {
        return originCondition == originConditionPS && conflictingCondition == conflictingConditionPS
    }

    override fun getResolution(currentCondition: Condition<Double>, subsystem: ColleagueSubsystem<Double>): Condition<Double> {
        val closestBound = (conflictingConditionPS as PositionBasedCondition).findClosestBound(currentCondition)
        val safetyRange = (subsystem as PositionBasedSubsystem).getSafetyRange()

        return PositionBasedCondition(closestBound, Range.between(closestBound - safetyRange, closestBound + safetyRange))

    }

    override fun isRequestConflicting(request: Request<SubsystemStateCondition<T>>, currentConflictingCondition: Condition<Double>, currentOriginCondition: Condition<SubsystemStateCondition<T>>): Boolean {
        return request.condition.isInCondition(originCondition) && currentConflictingCondition.isInCondition(conflictingConditionPS)
    }

    override fun invert(): Conflict<Double, SubsystemStateCondition<T>> {
        return PositionStateBasedConflict(conflictingSubsystemPS, conflictingConditionPS, originSubsystemPS, originConditionPS, inverseResolveState, canInvertPS, parallelismSP)
    }
}