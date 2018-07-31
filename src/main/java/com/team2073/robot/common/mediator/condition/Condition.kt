package com.team2073.robot.common.mediator.condition

interface Condition {

    fun isInCondition(condition: Condition): Boolean

    fun <T> getConditionValue(): T
}