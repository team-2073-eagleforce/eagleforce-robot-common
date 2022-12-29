package com.team2073.common.objective;

import java.util.List;
import java.util.Set;

//import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj2.command.Subsystem;

public interface Objective {

	void execute();

	void interrupt();

	boolean isFinished();

	List<PreconditionMapping> getPreconditions();

	Set<Subsystem> getRequiredSubsystems();
	
	ConflictingStrategy getConflictingStrategy();
	
	public enum ConflictingStrategy {
		DENY_REQUESTED,
		INTERRUPT_CONFLICTS
	}

}