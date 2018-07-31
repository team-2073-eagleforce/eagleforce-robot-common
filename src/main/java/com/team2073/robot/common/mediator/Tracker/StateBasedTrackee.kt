package com.team2073.robot.common.mediator.Tracker

import com.team2073.robot.common.mediator.subsys.SubsystemStateCondition

interface StateBasedTrackee {
    fun updateTracker(): SubsystemStateCondition
}