package com.team2073.common.mediator.condition

interface Condition<T> {

    fun isInCondition(condition: Condition<T>): Boolean

    fun getConditionValue(): T

    override fun toString(): String
}