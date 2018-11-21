package com.team2073.common.mediator.subsys

import com.team2073.common.periodic.PeriodicRunnable

abstract class ColleagueSubsystem : PeriodicRunnable {

    abstract fun<T> set(place: T)

}