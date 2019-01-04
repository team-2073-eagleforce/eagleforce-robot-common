package com.team2073.common.mediator.conflict

import com.team2073.common.mediator.condition.Condition
import com.team2073.common.mediator.request.Request
import com.team2073.common.mediator.subsys.ColleagueSubsystem

abstract class Conflict<OS: ColleagueSubsystem, OC : Condition, CC : Condition, CS : ColleagueSubsystem>(var originSubsystem: Class<OS>,
                                                                              var originCondition: OC,
                                                                              var conflictingSubsystem: Class<CS>,
                                                                              var conflictingCondition: CC) {
    abstract fun invert(): Conflict<OS, OC, CC, CS>

    abstract fun isRequestConflicting(request: Request<CC, OS>, conflictingCondition: Condition): Boolean

    abstract fun isConditionConflicting(originCondition: Condition, conflictingCondition: Condition): Boolean

    abstract fun getResolution(currentCondition: Condition, subsystem: ColleagueSubsystem): Condition

    fun getName(): String {
        return "ORIGIN SUBSYSTEM: ${originSubsystem.simpleName} IN $originCondition conflicts with ${conflictingSubsystem.simpleName} IN $conflictingCondition"
    }
}