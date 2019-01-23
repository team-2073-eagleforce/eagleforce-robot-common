package com.team2073.common.mediator.conflict

import com.team2073.common.mediator.condition.Condition
import com.team2073.common.mediator.condition.StateBasedCondition
import com.team2073.common.mediator.request.Request
import com.team2073.common.mediator.subsys.ColleagueSubsystem
import com.team2073.common.mediator.subsys.SubsystemStateCondition

class StateBasedConflict<OT: Enum<OT>, CT: Enum<CT>>(
        var originSubsystemS: Class<ColleagueSubsystem<SubsystemStateCondition<OT>>>,
        var originConditionS: Condition<SubsystemStateCondition<OT>>,
        var conflictingSubsystemS: Class<ColleagueSubsystem<SubsystemStateCondition<CT>>>,
        var conflictingConditionS: Condition<SubsystemStateCondition<CT>>,
        var resolveState: SubsystemStateCondition<CT>?) :
        Conflict<SubsystemStateCondition<OT>, SubsystemStateCondition<CT>>(originSubsystemS, originConditionS, conflictingSubsystemS, conflictingConditionS) {

    override fun invert(): Conflict<SubsystemStateCondition<CT>, SubsystemStateCondition<OT>> {
        return StateBasedConflict(conflictingSubsystemS, conflictingConditionS, originSubsystemS, originConditionS, null)
    }

    override fun isRequestConflicting(request: Request<SubsystemStateCondition<OT>>, conflictingCondition: Condition<SubsystemStateCondition<CT>>): Boolean {
        return originCondition.isInCondition(request.condition) && conflictingCondition.isInCondition(conflictingConditionS)
    }

    override fun isConditionConflicting(originCondition: Condition<SubsystemStateCondition<OT>>, conflictingCondition: Condition<SubsystemStateCondition<CT>>): Boolean {
        return originCondition == originConditionS && conflictingCondition == conflictingConditionS
    }

    override fun getResolution(currentCondition: Condition<SubsystemStateCondition<CT>>, subsystem: ColleagueSubsystem<SubsystemStateCondition<CT>>): Condition<SubsystemStateCondition<CT>> {
        return StateBasedCondition(resolveState)
    }

}