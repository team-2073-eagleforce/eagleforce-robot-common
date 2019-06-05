package com.team2073.common.mediator.tracker

interface PositionBasedTrackee: SubsystemTrackee {
    override fun <Double> updateTracker(): Double
}