package com.team2073.common.mediator.conflict

import com.team2073.common.mediator.condition.Condition
import com.team2073.common.mediator.request.Request
import com.team2073.common.mediator.subsys.ColleagueSubsystem

abstract class Conflict<OT, CT>(
        val originSubsystem: Class<out ColleagueSubsystem<OT>>,
        val originCondition: Condition<OT>,
        val conflictingSubsystem: Class<out ColleagueSubsystem<CT>>,
        val conflictingCondition: Condition<CT>,
        val canInvert: Boolean,
        val parallelism: Boolean) {

    abstract fun invert(): Conflict<CT, OT>

    abstract fun isRequestConflicting(request: Request<OT>, currentConflictingCondition: Condition<CT>, currentOriginCondition: Condition<OT>): Boolean

    abstract fun isConditionConflicting(originCondition: Condition<OT>, conflictingCondition: Condition<CT>): Boolean

    abstract fun getResolution(currentCondition: Condition<CT>, subsystem: ColleagueSubsystem<CT>): Condition<CT>

    fun getName(): String {
        return "ORIGIN SUBSYSTEM: ${originSubsystem.simpleName} IN $originCondition conflicts with ${conflictingSubsystem.simpleName} IN $conflictingCondition"
    }

    abstract fun getOriginParallelResolution(originSubsystem: ColleagueSubsystem<OT>, conflictingSubsystem: ColleagueSubsystem<CT>): Condition<OT>
}