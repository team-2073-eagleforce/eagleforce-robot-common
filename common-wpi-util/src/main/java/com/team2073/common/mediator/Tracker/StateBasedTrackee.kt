package com.team2073.common.mediator.Tracker

import com.team2073.common.mediator.subsys.SubsystemStateCondition

interface StateBasedTrackee {
    fun updateTracker(): SubsystemStateCondition
}