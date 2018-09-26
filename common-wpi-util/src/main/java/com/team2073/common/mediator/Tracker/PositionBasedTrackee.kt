package com.team2073.common.mediator.Tracker

interface PositionBasedTrackee: SubsystemTrackee {
    override fun <Double> updateTracker(): Double
}