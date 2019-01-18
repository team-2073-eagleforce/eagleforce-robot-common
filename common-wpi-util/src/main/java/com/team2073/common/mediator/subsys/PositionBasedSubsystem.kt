package com.team2073.common.mediator.subsys

interface PositionBasedSubsystem: ColleagueSubsystem<Double> {

    fun getSafetyRange(): Double
}