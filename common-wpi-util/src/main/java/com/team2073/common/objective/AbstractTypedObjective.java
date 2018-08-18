package com.team2073.common.objective;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.team2073.common.objective.Objective.ConflictingStrategy;

/**
 * An implementation of {@link Objective} that allows specifying a state of type {@link Enum}.
 * This state is generally defined in the subsystem this Objective will interact with.
 * This allows the Objective to easily communicate with the Subsystem using the state enum.
 * 
 * @author Preston Briggs
 *
 * @param <T> The enum defining the various states a subsystem may be in
 */
public abstract class AbstractTypedObjective<T> extends AbstractObjective {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final T desiredState;
	private StatusChecker status;
	
	public AbstractTypedObjective(T desiredState) {
		logger.trace("Constructing Objective [{}]", this);
		this.desiredState = desiredState;
	}

	protected T getDesiredState() {
		return desiredState;
	}

	@Override
	public void execute() {
		logger.trace("Executing Objective [{}]", this);
		status = start();
		logger.trace("Executing Objective [{}] complete.", this);
	}

	@Override
	public void interrupt() {
		if(status == null) {
			Exception forLoggingOnly = new IllegalStateException("status must not be null");
			logger.warn("status was null during interrupt [{}].", getClass().getSimpleName(), forLoggingOnly);
			return;
		}
		logger.debug("Interrupting Objective [{}]", this);
		status.interrupt();
		logger.debug("Interrupting Objective [{}] complete.", this);
	}

	@Override
	public boolean isFinished() {
		if(status == null) {
			Exception forLoggingOnly = new IllegalStateException("status must not be null");
			logger.warn("status was null during isFinished [{}] Returning true.", getClass().getSimpleName(), forLoggingOnly);
			return true;
		}
		return status.isComplete();
	}
	
	@Override
	public String toString() {
		return desiredState.toString();
	}
	
	protected abstract StatusChecker start();

	/** Default {@link ConflictingStrategy}. */
	@Override
	public ConflictingStrategy getConflictingStrategy() {
		return ConflictingStrategy.DENY_REQUESTED;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((desiredState == null) ? 0 : desiredState.hashCode());
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractTypedObjective other = (AbstractTypedObjective) obj;
		if (desiredState == null) {
			if (other.desiredState != null)
				return false;
		} else if (!desiredState.equals(other.desiredState))
			return false;
		return true;
	}
	
}
