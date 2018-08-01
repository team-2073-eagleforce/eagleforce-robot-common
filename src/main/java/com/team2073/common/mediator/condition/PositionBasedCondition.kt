package com.team2073.common.mediator.condition

class PositionBasedCondition(val lowerBound: Double, val upperBound: Double, var exactPosition: Double): Condition {

    /** testing javadocs publish to mavenlocal */
    override fun <Double> getConditionValue(): Double {
        return exactPosition as Double
    }

    override fun isInCondition(condition: Condition): Boolean {
        return ((condition as PositionBasedCondition).exactPosition in lowerBound..upperBound)
    }

    fun findClosestBound(condition: Condition): Double{
        if(condition.getConditionValue() as Double - lowerBound > condition.getConditionValue() as Double - upperBound){
            return upperBound + 1
        }else{
            return lowerBound - 1
        }
        //TODO safe overshoot distance instead of 1's
    }
}