package com.team2073.common.dev.cmd;

import com.team2073.common.dev.objective.DevObjectiveFactory;
import com.team2073.common.dev.simulation.subsys.DevSubsystemCoordinatorImpl;
import com.team2073.common.objective.AbstractObjectiveCommand;

public abstract class DevAbstractObjectiveCommand extends AbstractObjectiveCommand {

	private DevObjectiveFactory factory;

	public DevAbstractObjectiveCommand(DevSubsystemCoordinatorImpl coordinator, DevObjectiveFactory factory) {
		super(coordinator);
		this.factory = factory;
	}
	
	protected DevObjectiveFactory getFactory() {
		return factory;
	}
	
}
