package com.team2073.common.objective;

import java.util.LinkedList;
import java.util.List;

public class ObjectiveRequest {

	protected final Objective requestedObjective;
	protected ObjectiveStatus status = ObjectiveStatus.QUEUED;
	
	private List<ObjectiveRequest> blockingObjectiveRequests = new LinkedList<>();

	public ObjectiveRequest(Objective requestedObjective) {
		this.requestedObjective = requestedObjective;
	}

	public boolean isDenied() {
		return status == ObjectiveStatus.DENIED;
	}

	public boolean isExecuting() {
		return status == ObjectiveStatus.EXECUTING;
	}

	public boolean isQueued() {
		return status == ObjectiveStatus.QUEUED;
	}

	protected boolean isComplete() {
		return status == ObjectiveStatus.COMPLETED;
	}

	protected boolean isInterrupted() {
		return status == ObjectiveStatus.INTERRUPTED;
	}

	/** Returns true if the command has completed or was interrupted 
	 * (Checks both {@link #isComplete()} and {@link #isInterrupted()}). */
	public boolean isFinished() {
		return isComplete() || isInterrupted();
	}

	public void setInterrupted() {
		status = ObjectiveStatus.INTERRUPTED;
	}

	// ============================================================
	// PACKAGE-PRIVATE METHODS
	// ============================================================

	void execute() {
		status = ObjectiveStatus.EXECUTING;
		requestedObjective.execute();
	}
	
	void interrupt() {
		status = ObjectiveStatus.INTERRUPTED;
		requestedObjective.interrupt();
	}

	void setComplete() {
		status = ObjectiveStatus.COMPLETED;
	}
	
	void setDenied() {
		status = ObjectiveStatus.DENIED;
	}
	
	void addBlockingObjective(ObjectiveRequest blockingObjectiveRequest) {
		blockingObjectiveRequests.add(blockingObjectiveRequest);
	}
	
	boolean isBlocked() {
		if (blockingObjectiveRequests.isEmpty())
			return false;
		
		return !blockingObjectiveRequests.stream().allMatch(ObjectiveRequest::isComplete);
	}
	
	// Getter/setters
	// ============================================================
	Objective getRequestedObjective() {
		return requestedObjective;
	}

	// Object overrides
	// ============================================================
	@Override
	public String toString() {
		return "ObjectiveRequest [" + requestedObjective + " : " + status + "]";
	}

}