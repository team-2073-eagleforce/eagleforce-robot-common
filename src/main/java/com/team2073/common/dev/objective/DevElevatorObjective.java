package com.team2073.common.dev.objective;

import com.team2073.common.dev.simulation.subsys.DevElevatorSubsystem;
import com.team2073.common.dev.simulation.subsys.DevElevatorSubsystem.ElevatorHeight;
import com.team2073.common.objective.AbstractTypedObjective;
import com.team2073.common.objective.StatusChecker;

public class DevElevatorObjective extends AbstractTypedObjective<ElevatorHeight> {

	protected DevElevatorSubsystem elevator;
	
	public DevElevatorObjective(DevElevatorSubsystem elevator, ElevatorHeight desiredState) {
		super(desiredState);
		this.elevator = elevator;
		requireSubsystem(elevator);
	}

	@Override
	public StatusChecker start() {
		return elevator.moveToHeight(getDesiredState());
	}

	@Override
	public ConflictingStrategy getConflictingStrategy() {
		return ConflictingStrategy.DENY_REQUESTED;
	}

//	@Override
//	public boolean isFinished() {
//		return elevator.isAtHeight(getDesiredState());
//	}
}
