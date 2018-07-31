package com.team2073.common.objective;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 * TODO
 * @author Preston Briggs
 *
 * @param <T> The enum defining the state.
 */
public abstract class AbstractObjective implements Objective {

	private List<PreconditionMapping> preconditionList = new ArrayList<>();
	private List<PreconditionMapping> preconditionListUnmodifiable = Collections.unmodifiableList(preconditionList);
	private Set<Subsystem> subsystems = new HashSet<>();
	
	@Override
	public List<PreconditionMapping> getPreconditions() {
		return preconditionListUnmodifiable;
	}
	
	public void addPrecondition(Precondition precondition, Objective resolution) {
		addPrecondition(new PreconditionMapping(precondition, resolution));
	}
	
	// TODO: JavaDocs
	public void addPrecondition(PreconditionMapping precondition) {
		preconditionList.add(precondition);
	}
	
	@Override
	public Set<Subsystem> getRequiredSubsystems() {
		return subsystems;
	}
	
	public void requireSubsystem(Subsystem subsystem) {
		subsystems.add(subsystem);
	}

	@Override
	public abstract void execute();
	
	@Override
	public abstract void interrupt();
	
	@Override
	public abstract boolean isFinished();

	@Override
	public abstract int hashCode();

	@Override
	public abstract boolean equals(Object obj);

}
