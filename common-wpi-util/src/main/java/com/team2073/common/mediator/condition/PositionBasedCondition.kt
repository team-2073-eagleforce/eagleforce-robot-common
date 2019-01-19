package com.team2073.common.mediator.condition

class PositionBasedCondition(val lowerBound: Double, var exactPosition: Double, val upperBound: Double) : Condition<Double> {

    /** testing javadocs publish to mavenlocal */
    override fun getConditionValue(): Double {
        return exactPosition
    }

    override fun isInCondition(condition: Condition<Double>): Boolean {
        return ((condition as PositionBasedCondition).exactPosition in lowerBound..upperBound)
    }

    fun findClosestBound(condition: Condition<Double>): Double {
        if (condition.getConditionValue() - lowerBound > condition.getConditionValue() - upperBound) {
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
        return "Between the range of [$lowerBound] -- [$upperBound] with an exact position of [$exactPosition]"
    }

}