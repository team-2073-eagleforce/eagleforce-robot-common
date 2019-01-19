package com.team2073.common.mediator.conflict

import com.team2073.common.mediator.condition.Condition
import com.team2073.common.mediator.condition.StateBasedCondition
import com.team2073.common.mediator.request.Request
import com.team2073.common.mediator.subsys.ColleagueSubsystem
import com.team2073.common.mediator.subsys.SubsystemStateCondition

class StateBasedConflict(
        var originSubsystemS: Class<ColleagueSubsystem<SubsystemStateCondition>>,
        var originConditionS: Condition<SubsystemStateCondition>,
        var conflictingSubsystemS: Class<ColleagueSubsystem<SubsystemStateCondition>>,
        var conflictingConditionS: Condition<SubsystemStateCondition>,
        var resolveState: SubsystemStateCondition) :
        Conflict<SubsystemStateCondition, SubsystemStateCondition>(originSubsystemS, originConditionS, conflictingSubsystemS, conflictingConditionS) {

    override fun invert(): Conflict<SubsystemStateCondition, SubsystemStateCondition> {
        return StateBasedConflict(originSubsystemS, originConditionS, conflictingSubsystemS, conflictingConditionS, resolveState)
    }

    override fun isRequestConflicting(request: Request<SubsystemStateCondition>, conflictingCondition: Condition<SubsystemStateCondition>): Boolean {
        return originCondition.isInCondition(request.condition) && conflictingCondition.isInCondition(conflictingConditionS)
    }

    override fun isConditionConflicting(originCondition: Condition<SubsystemStateCondition>, conflictingCondition: Condition<SubsystemStateCondition>): Boolean {
        return originCondition == originConditionS && conflictingCondition == conflictingConditionS
    }

    override fun getResolution(currentCondition: Condition<SubsystemStateCondition>, subsystem: ColleagueSubsystem<SubsystemStateCondition>): Condition<SubsystemStateCondition> {
        return StateBasedCondition(resolveState)
    }

}