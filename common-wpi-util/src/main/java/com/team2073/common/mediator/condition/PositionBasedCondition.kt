package com.team2073.common.mediator.condition

class PositionBasedCondition(val lowerBound: Double, var exactPosition: Double, val upperBound: Double) : Condition {

    /** testing javadocs publish to mavenlocal */
    override fun <Double> getConditionValue(): Double {
        return exactPosition as Double
    }

    override fun isInCondition(condition: Condition): Boolean {
        return ((condition as PositionBasedCondition).exactPosition in lowerBound..upperBound)
    }

    fun findClosestBound(condition: Condition): Double {
        if (condition.getConditionValue() as Double - lowerBound > condition.getConditionValue() as Double - upperBound) {
            return upperBound
        } else {
            return lowerBound
        }
    }

    /**
     * @return returns true if value is lower bound, false if value is upper bound, and null if value is neither
     */
    fun isLowerBound(bound: Double): Boolean? {
        if (bound == lowerBound) {
            return true
        } else if (bound == upperBound) {
            return false
        } else {
            return null
        }
    }

    override fun toString(): String {
        return "Between the range of [" +lowerBound + "] -- [" + upperBound + "] with an exact position of [" + exactPosition + "]"
    }

}