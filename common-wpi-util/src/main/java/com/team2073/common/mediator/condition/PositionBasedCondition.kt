package com.team2073.common.mediator.condition

import org.apache.commons.lang3.Range

class PositionBasedCondition(val exactPosition: Double, val range: Range<Double>) : Condition<Double> {

    override fun getConditionValue(): Double {
        return exactPosition
    }

    override fun isInCondition(condition: Condition<Double>): Boolean {
        return range.contains(condition.getConditionValue())
    }

    fun findClosestBound(condition: Condition<Double>): Double {
        return if (condition.getConditionValue() - range.minimum > condition.getConditionValue() - range.maximum) {
            range.maximum
        } else {
            range.minimum
        }
    }

    /**
     * @return returns true if value is lower bound, false if value is upper bound, and null if value is neither
     */
    fun isLowerBound(bound: Double): Boolean? {
        return when (bound) {
            range.minimum -> true
            range.maximum -> false
            else -> null
        }
    }

    override fun toString(): String {
        return "Between the range of [${range.minimum}] -- [${range.maximum}] with an exact position of [$exactPosition]"
    }

}