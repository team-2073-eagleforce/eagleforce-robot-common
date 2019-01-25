package com.team2073.common.mediator.conflict

import com.team2073.common.mediator.condition.Condition
import com.team2073.common.mediator.request.Request
import com.team2073.common.mediator.subsys.ColleagueSubsystem

abstract class Conflict<OT, CT>(
        var originSubsystem: Class<out ColleagueSubsystem<OT>>,
        var originCondition: Condition<OT>,
        var conflictingSubsystem: Class<out ColleagueSubsystem<CT>>,
        var conflictingCondition: Condition<CT>) {

    abstract fun invert(): Conflict<CT, OT>

    abstract fun isRequestConflicting(request: Request<OT>, conflictingCondition: Condition<CT>): Boolean

    abstract fun isConditionConflicting(originCondition: Condition<OT>, conflictingCondition: Condition<CT>): Boolean

    abstract fun getResolution(currentCondition: Condition<CT>, subsystem: ColleagueSubsystem<CT>): Condition<CT>

    fun getName(): String {
        return "ORIGIN SUBSYSTEM: ${originSubsystem.simpleName} IN $originCondition conflicts with ${conflictingSubsystem.simpleName} IN $conflictingCondition"
    }
}