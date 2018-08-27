package com.team2073.common.dev.cmd;

//import com.google.inject.Inject;
import com.team2073.common.dev.objective.DevObjectiveFactory;
import com.team2073.common.dev.simulation.subsys.DevSubsystemCoordinatorImpl;
import com.team2073.common.dev.simulation.subsys.DevShooterPivotSubsystem.ShooterAngle;
import com.team2073.common.objective.Objective;

public class DevShooterToAngleCommand extends DevAbstractObjectiveCommand {

	private final ShooterAngle angle;

//	@Inject
	public DevShooterToAngleCommand(DevSubsystemCoordinatorImpl coordinator, DevObjectiveFactory factory, ShooterAngle angle) {
		super(coordinator, factory);
		this.angle = angle;
	}

	@Override
	protected Objective initializeObjective() {
		return getFactory().getShooterTo(angle);
	}
	
}
