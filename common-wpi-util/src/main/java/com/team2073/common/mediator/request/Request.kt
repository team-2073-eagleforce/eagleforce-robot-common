package com.team2073.common.mediator.request

import com.team2073.common.mediator.condition.Condition
import com.team2073.common.mediator.subsys.ColleagueSubsystem

data class Request<Z, T : Condition, C : ColleagueSubsystem<Z>>(val subsystem: Class<C>,
                                                          val condition: T) {

    var hasBeenRequested = false

    fun getName(): String{
        return "REQUEST| SUBSYSTEM: ${subsystem.simpleName} CONDITION: $condition"
    }
}