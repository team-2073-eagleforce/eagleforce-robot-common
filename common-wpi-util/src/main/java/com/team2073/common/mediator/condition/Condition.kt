package com.team2073.common.mediator.condition

import com.team2073.common.mediator.subsys.ColleagueSubsystem

interface Condition {

    fun isInCondition(condition: Condition): Boolean

    fun <T> getConditionValue(): T
}