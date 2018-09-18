package com.team2073.common.mediator.conflict

import com.team2073.common.mediator.condition.Condition
import com.team2073.common.mediator.request.Request
import com.team2073.common.mediator.subsys.ColleagueSubsystem

abstract class Conflict<O : Condition, C : Condition, Z : ColleagueSubsystem>(var originSubsystem: Class<Z>,
                                                                              var originCondition: O,
                                                                              var conflictingSubsystem: Class<Z>,
                                                                              var conflictingCondition: C) {
    abstract fun invert(): Conflict<C, O, Z>

    abstract fun isConflicting(conflict: Conflict<C, O, Z>, request: Request, currentCondition: Condition): Boolean

    abstract fun getResolution(currentCondition: Condition, subsystem: ColleagueSubsystem): Condition

    fun getName(): String {
        return "ORIGIN SUBSYSTEM: ${originSubsystem.simpleName} IN $originCondition conflicts with ${conflictingSubsystem.simpleName} IN $conflictingCondition"
    }
}