package com.team2073.common.dev.objective;

import com.team2073.common.dev.simulation.subsys.DevIntakeSideRollerSubsystem;
import com.team2073.common.dev.simulation.subsys.DevIntakeSideRollerSubsystem.IntakeSideRollerState;
import com.team2073.common.objective.AbstractTypedObjective;
import com.team2073.common.objective.StatusChecker;

public class DevIntakeSideRollerObjective extends AbstractTypedObjective<IntakeSideRollerState> {
	private DevIntakeSideRollerSubsystem intakeSides;
	
	public DevIntakeSideRollerObjective(DevIntakeSideRollerSubsystem intakeSides, IntakeSideRollerState desiredState) {
		super(desiredState);
		this.intakeSides = intakeSides;
		requireSubsystem(intakeSides);
	}
	
	@Override
	public StatusChecker start() {
		return intakeSides.setState(getDesiredState());
	}

	@Override
	public ConflictingStrategy getConflictingStrategy() {
		return ConflictingStrategy.DENY_REQUESTED;
	}
}
