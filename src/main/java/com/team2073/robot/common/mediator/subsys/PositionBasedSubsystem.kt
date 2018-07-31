package com.team2073.robot.common.mediator.subsys

abstract class PositionBasedSubsystem : ColleagueSubsystem() {

    abstract override fun <Double> set(place: Double)
}