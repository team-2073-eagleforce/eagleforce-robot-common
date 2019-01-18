package com.team2073.common.mediator.subsys

import com.team2073.common.periodic.PeriodicRunnable

interface ColleagueSubsystem<T> : PeriodicRunnable {

    fun set(place: T)

}