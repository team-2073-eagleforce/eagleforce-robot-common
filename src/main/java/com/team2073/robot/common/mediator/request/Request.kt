package com.team2073.robot.common.mediator.request

import com.team2073.robot.common.mediator.condition.Condition
import com.team2073.robot.common.mediator.subsys.ColleagueSubsystem

data class Request<T : Condition, C : ColleagueSubsystem>(val subsystem: Class<C>,
                                                     val condition: T) {
}