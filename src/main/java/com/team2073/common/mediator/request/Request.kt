package com.team2073.common.mediator.request

import com.team2073.common.mediator.condition.Condition
import com.team2073.common.mediator.subsys.ColleagueSubsystem

data class Request<T : Condition, C : ColleagueSubsystem>(val subsystem: Class<C>,
                                                     val condition: T) {
}