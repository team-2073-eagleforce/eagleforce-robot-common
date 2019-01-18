package com.team2073.common.dev.simulation;

import com.team2073.common.robot.RobotRunner;

public class RobotRunnerSimulationImpl extends RobotRunner {

	public RobotRunnerSimulationImpl() {
		super(new SimulationRobot());
	}

}
