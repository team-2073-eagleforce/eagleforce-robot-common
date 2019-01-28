package com.team2073.common.mediator.condition

class PositionBasedCondition(private val lowerBound: Double, val exactPosition: Double, private val upperBound: Double) : Condition<Double> {

    override fun getConditionValue(): Double {
        return exactPosition
    }

    override fun isInCondition(condition: Condition<Double>): Boolean {
        return ((condition as PositionBasedCondition).exactPosition in lowerBound..upperBound)
    }

    fun findClosestBound(condition: Condition<Double>): Double {
        return if (condition.getConditionValue() - lowerBound > condition.getConditionValue() - upperBound) {
            upperBound
        } else {
            lowerBound
        }
    }

    /**
     * @return returns true if value is lower bound, false if value is upper bound, and null if value is neither
     */
    fun isLowerBound(bound: Double): Boolean? {
        return when (bound) {
            lowerBound -> true
            upperBound -> false
            else -> null
        }
    }

    override fun toString(): String {
        return "Between the range of [$lowerBound] -- [$upperBound] with an exact position of [$exactPosition]"
    }

}