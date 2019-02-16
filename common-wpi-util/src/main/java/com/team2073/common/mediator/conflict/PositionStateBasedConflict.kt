package com.team2073.common.mediator.conflict

import com.team2073.common.mediator.condition.Condition
import com.team2073.common.mediator.condition.StateBasedCondition
import com.team2073.common.mediator.request.Request
import com.team2073.common.mediator.subsys.ColleagueSubsystem
import com.team2073.common.mediator.subsys.SubsystemStateCondition
import org.apache.commons.lang3.Range

class PositionStateBasedConflict<T : Enum<T>>(
        val originSubsystemPS: Class<out ColleagueSubsystem<Double>>,
        val originConditionPS: Condition<Double>,
        val conflictingSubsystemPS: Class<out ColleagueSubsystem<SubsystemStateCondition<T>>>,
        val conflictingConditionPS: Condition<SubsystemStateCondition<T>>,
        val resolveState: SubsystemStateCondition<T>?) :
        Conflict<Double, SubsystemStateCondition<T>>(originSubsystemPS, originConditionPS, conflictingSubsystemPS, conflictingConditionPS) {

    override fun isConditionConflicting(originCondition: Condition<Double>, conflictingCondition: Condition<SubsystemStateCondition<T>>): Boolean {
        return originCondition == originConditionPS && conflictingCondition == conflictingConditionPS
    }

    override fun getResolution(currentCondition: Condition<SubsystemStateCondition<T>>, subsystem: ColleagueSubsystem<SubsystemStateCondition<T>>): Condition<SubsystemStateCondition<T>> {
        return StateBasedCondition(resolveState)
    }

    override fun isRequestConflicting(request: Request<Double>, currentConflictingCondition: Condition<SubsystemStateCondition<T>>, currentOriginCondition: Condition<Double>): Boolean {
        val travelRange: Range<Double> = Range.between(currentOriginCondition.getConditionValue(), request.condition.getConditionValue())
        return currentConflictingCondition.isInCondition(conflictingConditionPS)
                && travelRange.contains(originConditionPS.getConditionValue())
    }

    override fun invert(): Conflict<SubsystemStateCondition<T>, Double> {
        return StatePositionBasedConflict(conflictingSubsystemPS, conflictingConditionPS, originSubsystemPS, originConditionPS, null)
    }
}