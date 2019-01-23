package com.team2073.common.mediator.Tracker

import com.team2073.common.mediator.subsys.SubsystemStateCondition

interface StateBasedTrackee<T: Enum<T>>: SubsystemTrackee<SubsystemStateCondition<T>>{
    override fun updateTracker(): SubsystemStateCondition<T>
}