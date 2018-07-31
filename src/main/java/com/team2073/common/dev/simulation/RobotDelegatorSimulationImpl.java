package com.team2073.common.dev.simulation;

import com.team2073.common.robot.AbstractRobotDelegator;

public class RobotDelegatorSimulationImpl extends AbstractRobotDelegator {

	public RobotDelegatorSimulationImpl() {
		super(new SimulationRobot());
	}

}
