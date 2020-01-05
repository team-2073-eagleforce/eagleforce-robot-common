package com.team2073.common.mediator.tracker

interface StateBasedTrackee: SubsystemTrackee{
    override fun <SubsystemStateCondition> updateTracker(): SubsystemStateCondition
}