package com.team2073.common.dev.cmd;

//import com.google.inject.Inject;
import com.team2073.common.dev.objective.DevObjectiveFactory;
import com.team2073.common.dev.simulation.subsys.DevElevatorSubsystem.ElevatorHeight;
import com.team2073.common.dev.simulation.subsys.DevSubsystemCoordinatorImpl;
import com.team2073.common.objective.Objective;

public class DevElevatorToHeightCommand extends DevAbstractObjectiveCommand {

	private final ElevatorHeight height;

//	@Inject
	public DevElevatorToHeightCommand(DevSubsystemCoordinatorImpl coordinator, DevObjectiveFactory factory, ElevatorHeight height) {
		super(coordinator, factory);
		this.height = height;
	}

	@Override
	protected Objective initializeObjective() {
		return getFactory().getElevatorTo(height);
	}

}
