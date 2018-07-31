package com.team2073.common.mediator.subsys

abstract class StateBasedSubsystem : ColleagueSubsystem() {
    abstract override fun <SubsystemStateCondition> set(place: SubsystemStateCondition)

}