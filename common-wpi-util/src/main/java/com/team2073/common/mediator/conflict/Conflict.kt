package com.team2073.common.mediator.conflict

import com.team2073.common.mediator.condition.Condition
import com.team2073.common.mediator.request.Request
import com.team2073.common.mediator.subsys.ColleagueSubsystem

abstract class Conflict<OT, OS : ColleagueSubsystem<OT>, OC : Condition, CT, CC : Condition, CS : ColleagueSubsystem<CT>>(
        var originSubsystem: Class<OS>,
        var originCondition: OC,
        var conflictingSubsystem: Class<CS>,
        var conflictingCondition: CC) {

    abstract fun invert(): Conflict<OT, OS, OC, CT, CC, CS>

    abstract fun isRequestConflicting(request: Request<OT, CC, OS>, conflictingCondition: CC): Boolean

    abstract fun isConditionConflicting(originCondition: OC, conflictingCondition: CC): Boolean

    // TODO: Possibly generify the return type of Condition
    abstract fun getResolution(currentCondition: CC, subsystem: CS): Condition

    fun getName(): String {
        return "ORIGIN SUBSYSTEM: ${originSubsystem.simpleName} IN $originCondition conflicts with ${conflictingSubsystem.simpleName} IN $conflictingCondition"
    }
}