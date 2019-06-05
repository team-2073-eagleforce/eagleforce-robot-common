package com.team2073.common.mediator.tracker

interface SubsystemTrackee {
    fun<T> updateTracker(): T
}