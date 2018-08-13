package com.team2073.common.dev.cmd;

import com.google.inject.Inject;
import com.team2073.common.dev.objective.DevObjectiveFactory;
import com.team2073.common.dev.simulation.subsys.DevElevatorSubsystem.ElevatorHeight;
import com.team2073.common.dev.simulation.subsys.DevSubsystemCoordinatorImpl;
import com.team2073.common.objective.Objective;

public class DevElevatorToMaxCommand extends DevAbstractObjectiveCommand {

	@Inject
	public DevElevatorToMaxCommand(DevSubsystemCoordinatorImpl coordinator, DevObjectiveFactory factory) {
		super(coordinator, factory);
	}

	@Override
	protected Objective initializeObjective() {
//		return getFactory().getElevatorToMax();
		return getFactory().getElevatorTo(ElevatorHeight.MAX);
	}

}
