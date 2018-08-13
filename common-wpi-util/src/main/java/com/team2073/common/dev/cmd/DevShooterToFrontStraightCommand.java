package com.team2073.common.dev.cmd;

import com.google.inject.Inject;
import com.team2073.common.dev.objective.DevObjectiveFactory;
import com.team2073.common.dev.simulation.subsys.DevSubsystemCoordinatorImpl;
import com.team2073.common.dev.simulation.subsys.DevShooterPivotSubsystem.ShooterAngle;
import com.team2073.common.objective.Objective;

public class DevShooterToFrontStraightCommand extends DevAbstractObjectiveCommand {

	@Inject
	public DevShooterToFrontStraightCommand(DevSubsystemCoordinatorImpl coordinator, DevObjectiveFactory factory) {
		super(coordinator, factory);
	}

	@Override
	protected Objective initializeObjective() {
//		return getFactory().getShooterToFrontStraight();
		return getFactory().getShooterTo(ShooterAngle.FORWARD_STRAIGHT);
	}
	
}
