package com.team2073.common.mediator.subsys

import com.team2073.common.periodic.PeriodicAware

abstract class ColleagueSubsystem : PeriodicAware {

    abstract fun<T> set(place: T)

}