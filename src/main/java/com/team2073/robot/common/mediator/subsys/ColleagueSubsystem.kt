package com.team2073.robot.common.mediator.subsys

import edu.wpi.first.wpilibj.command.Subsystem

abstract class ColleagueSubsystem : Subsystem {

    constructor(name: String) : super(name)
    constructor() : super()

    override fun initDefaultCommand() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    abstract fun<T> set(place: T)


}