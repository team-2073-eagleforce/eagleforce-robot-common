package com.team2073.common.dev.cmd;

import com.google.inject.Inject;
import com.team2073.common.dev.objective.DevObjectiveFactory;
import com.team2073.common.dev.simulation.subsys.DevIntakeSideRollerSubsystem.IntakeSideRollerState;
import com.team2073.common.dev.simulation.subsys.DevSubsystemCoordinatorImpl;
import com.team2073.common.objective.Objective;

public class DevIntakeSideRollerRandomStateCommand extends DevAbstractObjectiveCommand {

	private IntakeSideRollerState[] states = IntakeSideRollerState.values();
	private int index = -1;

	@Inject
	public DevIntakeSideRollerRandomStateCommand(DevSubsystemCoordinatorImpl coordinator, DevObjectiveFactory factory) {
		super(coordinator, factory);
	}

	private IntakeSideRollerState nextState() {
		++index;
		if (index == states.length)
			index = 0;
		return states[index];
	}

	@Override
	protected Objective initializeObjective() {
		return getFactory().getIntakeSideRoller(nextState());
	}

}
