package com.team2073.common.dev.cmd;

import com.team2073.common.dev.objective.DevObjectiveFactory;
import com.team2073.common.dev.simulation.subsys.DevSubsystemCoordinatorImpl;

public abstract class DevAbstractElevatorObjectiveCommand extends DevAbstractObjectiveCommand {

	public DevAbstractElevatorObjectiveCommand(DevSubsystemCoordinatorImpl coordinator, DevObjectiveFactory factory) {
		super(coordinator, factory);
	}
}
