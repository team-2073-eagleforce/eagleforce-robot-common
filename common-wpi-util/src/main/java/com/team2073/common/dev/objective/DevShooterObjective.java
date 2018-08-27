package com.team2073.common.dev.objective;

import com.team2073.common.dev.simulation.subsys.DevShooterPivotSubsystem;
import com.team2073.common.dev.simulation.subsys.DevShooterPivotSubsystem.ShooterAngle;
import com.team2073.common.objective.AbstractTypedObjective;
import com.team2073.common.objective.StatusChecker;

public class DevShooterObjective extends AbstractTypedObjective<ShooterAngle> {
	
	protected DevShooterPivotSubsystem shooter;

	public DevShooterObjective(DevShooterPivotSubsystem shooter, ShooterAngle desiredState) {
		super(desiredState);
		this.shooter = shooter;
		requireSubsystem(shooter);
	}

	@Override
	public boolean isFinished() {
		return shooter.isAtAngle(getDesiredState());
	}

	@Override
	protected StatusChecker start() {
		return shooter.moveToAngle(getDesiredState());
	}

	@Override
	public ConflictingStrategy getConflictingStrategy() {
		return ConflictingStrategy.DENY_REQUESTED;
	}
}