package com.team2073.common.mediator.conflict

import com.team2073.common.mediator.condition.Condition
import com.team2073.common.mediator.condition.StateBasedCondition
import com.team2073.common.mediator.request.Request
import com.team2073.common.mediator.subsys.ColleagueSubsystem
import com.team2073.common.mediator.subsys.SubsystemStateCondition

class StateBasedConflict<OT : Enum<OT>, CT : Enum<CT>>(
        val originSubsystemS: Class<ColleagueSubsystem<SubsystemStateCondition<OT>>>,
        val originConditionS: Condition<SubsystemStateCondition<OT>>,
        val conflictingSubsystemS: Class<ColleagueSubsystem<SubsystemStateCondition<CT>>>,
        val conflictingConditionS: Condition<SubsystemStateCondition<CT>>,
        val resolveState: SubsystemStateCondition<CT>?,
        val inverseResolveState: SubsystemStateCondition<OT>?,
        val canInvertS: Boolean,
        val parallelismS: Boolean) :
        Conflict<SubsystemStateCondition<OT>, SubsystemStateCondition<CT>>(originSubsystemS, originConditionS, conflictingSubsystemS, conflictingConditionS, canInvertS, parallelismS) {

    override fun canOverrideConflict(originSubsystem: ColleagueSubsystem<SubsystemStateCondition<OT>>, conflictingSubsystem: ColleagueSubsystem<SubsystemStateCondition<CT>>): Boolean {
        return false
    }

    override fun getOriginParallelResolution(originSubsystem: ColleagueSubsystem<SubsystemStateCondition<OT>>, conflictingSubsystem: ColleagueSubsystem<SubsystemStateCondition<CT>>): Condition<SubsystemStateCondition<OT>> {
        return StateBasedCondition(originSubsystem.getCurrentCondition().getConditionValue())
    }

    override fun invert(): Conflict<SubsystemStateCondition<CT>, SubsystemStateCondition<OT>> {
        return StateBasedConflict(conflictingSubsystemS, conflictingConditionS, originSubsystemS, originConditionS, inverseResolveState, resolveState, canInvertS, parallelismS)
    }

    override fun isRequestConflicting(request: Request<SubsystemStateCondition<OT>>, currentConflictingCondition: Condition<SubsystemStateCondition<CT>>, currentOriginCondition: Condition<SubsystemStateCondition<OT>>): Boolean {
        return originCondition.isInCondition(request.condition) && currentConflictingCondition.isInCondition(conflictingConditionS)
    }

    override fun isConditionConflicting(originCondition: Condition<SubsystemStateCondition<OT>>, conflictingCondition: Condition<SubsystemStateCondition<CT>>): Boolean {
        return originCondition == originConditionS && conflictingCondition == conflictingConditionS
    }

    override fun getResolution(currentCondition: Condition<SubsystemStateCondition<CT>>, subsystem: ColleagueSubsystem<SubsystemStateCondition<CT>>): Condition<SubsystemStateCondition<CT>> {
        return StateBasedCondition(resolveState)
    }
}