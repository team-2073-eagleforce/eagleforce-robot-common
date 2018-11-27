package com.team2073.common.mediator.Tracker

interface PositionBasedTrackee: SubsystemTrackee<Double> {
    override fun updateTracker(): Double
}