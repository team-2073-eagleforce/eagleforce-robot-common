package com.team2073.common.mediator.request

import com.team2073.common.mediator.condition.Condition
import com.team2073.common.mediator.subsys.ColleagueSubsystem

data class Request<T>(val subsystem: Class<out ColleagueSubsystem<T>>,
                      val condition: Condition<T>) {

    fun getName(): String {
        return "REQUEST| SUBSYSTEM: ${subsystem.simpleName} CONDITION: $condition"
    }
}