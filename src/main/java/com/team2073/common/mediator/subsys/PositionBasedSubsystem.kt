package com.team2073.common.mediator.subsys

abstract class PositionBasedSubsystem : ColleagueSubsystem() {

    abstract override fun <Double> set(place: Double)
}