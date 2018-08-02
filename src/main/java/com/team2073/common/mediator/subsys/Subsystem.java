package com.team2073.common.mediator.subsys;

import edu.wpi.first.wpilibj.command.Scheduler;

public abstract class Subsystem {

	public Subsystem(String name){

	}

	public Subsystem(){

	}


	abstract void initDefaultCommand();

	public void periodic(){

	}


}
