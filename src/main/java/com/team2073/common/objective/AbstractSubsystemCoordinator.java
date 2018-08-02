package com.team2073.common.objective;

import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.team2073.common.objective.Objective.ConflictingStrategy;
import com.team2073.common.robot.PeriodicAware;
import com.team2073.common.robot.PeriodicRegistry;
import com.team2073.common.util.LogUtil;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public abstract class AbstractSubsystemCoordinator implements PeriodicAware {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private List<Deque<ObjectiveRequest>> objectiveRequestStacks = new LinkedList<>();
	private boolean waiting = false;

	public AbstractSubsystemCoordinator() {
		LogUtil.infoConstruct(getClass(), logger);
		PeriodicRegistry.registerInstance(this);
	}
	
	/**
	 * Interrupts all {@link Objective}s and clears the stack. Used internally on errors
	 * to 'reset' and can also be used externally to cancel all Objectives (for example 
	 * on disabled).
	 */
	public void reset() {
		logger.debug("Clearing Objective stacks");
		for (Deque<ObjectiveRequest> objectiveRequestStack : objectiveRequestStacks) {
			interruptStack(objectiveRequestStack);
		}
		objectiveRequestStacks.clear();
	}
	
	private void interruptStack(Deque<ObjectiveRequest> objectiveRequestStack) {
		logger.debug("Clearing Objective stack [{}]", stackToString(objectiveRequestStack));
		for (ObjectiveRequest request : objectiveRequestStack) {
			logger.debug("Interrupting [{}].", request.getRequestedObjective());
			request.interrupt();
		}
		objectiveRequestStack.clear();
	}

	// TODO: Create a periodic interface
	public void onPeriodic() {
		
		// Iterate through all objective stacks, which run in parallel to each other
		Iterator<Deque<ObjectiveRequest>> stackIterator = objectiveRequestStacks.iterator();
		while (stackIterator.hasNext()) {
			Deque<ObjectiveRequest> currStack = stackIterator.next();
			
			// Pop current stack if it has no more objectives
			if (currStack.isEmpty()) {
				stackIterator.remove();
				logger.debug("Removed objective stack [{}].", currStack);
				logStacks();
			}
			
			boolean errored = false;
			
			// Iterate through the current stack, stopping at any objective request that is
			// blocked (i.e. it has unresolved preconditions) and has unmet preconditions
			Iterator<ObjectiveRequest> reqIterator = currStack.descendingIterator();
			while (reqIterator.hasNext()) {
				ObjectiveRequest currReq = reqIterator.next();
				
				// Check if the current request is blocked and has unmet preconditions
				if (currReq.isBlocked()) {
					List<PreconditionMapping> preconditions = currReq.getRequestedObjective().getPreconditions();
					if (!preconditions.stream().allMatch(PreconditionMapping::isMet)) {
						break;
					}
				}
				
				logger.trace("Current request [{}]. Stack: [{}].", currReq, stackToString(currStack));
				
				// Try to run periodic method for current request
				try {
					if (periodicInternal(currStack, currReq)) {
						reqIterator.remove();
					}
					
				// Catch any exception thrown in periodic and break from loop for current stack
				} catch (Exception e) {
					logger.error("Exception occurred processing Objective [{}]. Stack [{}].", currReq, stackToString(currStack), e);
					onError(e);
					errored = true;
					break;
				}
			}
			
			// Interrupt stack if an exception was thrown
			if (errored) {
				interruptStack(currStack);
			}
		}
	}
	
	private boolean periodicInternal(Deque<ObjectiveRequest> currStack, ObjectiveRequest currReq) throws Exception {
		
		if(currReq == null) {
			if(waiting == false) {
				logger.debug("No objectives to process. Waiting...");
				waiting = true;
			}
			return false;
		}
		waiting = false;
		
		// Check interruptions
		if(currReq.isInterrupted()) {
			logger.debug("Interrupting [{}]. Stack: [{}]", currReq, stackToString(currStack));
			interruptStack(currStack);
			return false;
		}
		
		// Check completed
		if (currReq.isComplete() || currReq.isDenied()) {
			return true; // Return true to remove current objective from stack
		}
		
		// Check preconditions
		boolean canExecute = true;
		
		List<PreconditionMapping> preconditions = currReq.getRequestedObjective().getPreconditions();
		for (PreconditionMapping precondition : preconditions) {
			if (!precondition.isMet()) {
				// TODO: Verify the precondition resolution to fix this issue is actually in the
				// stack right now. If not, queue it.
				canExecute = false;
			}
		}
		
		if (!canExecute) {
			return false;
		}

		// If hasn't been executed yet, execute
		if(currReq.isQueued()) {
			logger.debug("Executing [{}]. Stack: [{}]", currReq, stackToString(currStack));
			currReq.execute();
			return false;
		}
		
		// Finish
		if(currReq.getRequestedObjective().isFinished()) {
			logger.debug("Completing [{}]. Stack: [{}]", currReq, stackToString(currStack));
			currReq.setComplete();
//			pop(currReq);
			// TODO: Gabe, why was this set to return false?
			return true;
		}
		
		return false;
		
	}
	
	private void pop(Deque<ObjectiveRequest> objectiveRequestStack, ObjectiveRequest completedObjReq) {
		ObjectiveRequest pop = objectiveRequestStack.pollLast();
		logger.debug("Removed objective [{}]. Stack: [{}]", pop, stackToString(objectiveRequestStack));
		if(completedObjReq != pop)
			logger.debug("WARNING: Popped ObjectiveRequest [{}] did not match the completed ObjectiveRequest [{}]", pop, completedObjReq);
	}
	
	private boolean isCircularQueuing(Deque<ObjectiveRequest> objectiveRequestStack, Objective blockingObjective) {
		for (ObjectiveRequest objective : objectiveRequestStack) {
			logger.trace("Comparing [{}] to [{}].", blockingObjective, objective.getRequestedObjective());
			if(blockingObjective.equals(objective.getRequestedObjective())) {
				logger.warn(
						"WARN: Objectives match! Possible circular queueing event. Matching Objectives: [{}] and [{}]. Stack: [{}]",
						blockingObjective, objective.getRequestedObjective(), stackToString(objectiveRequestStack));
				return true;
			}
		}
		
		return false;
	}

	private ObjectiveRequest queuePreconditionResolution(Deque<ObjectiveRequest> objectiveRequestStack, Objective objective) {
		// TODO: Check conflicts here instead and just never add to the stack if they exist
		ObjectiveRequest objectiveRequest = new ObjectiveRequest(objective);
		objectiveRequestStack.addLast(objectiveRequest);
		logger.debug("Queued new precondition resolution: [{}]. Request: [{}]. Stack: [{}].", objective, objectiveRequest, stackToString(objectiveRequestStack));
		return objectiveRequest;
	}
	
	protected ObjectiveRequest queue(Objective objective) {
		ObjectiveRequest objectiveRequest = new ObjectiveRequest(objective);
		
		if(objectiveRequestStacks.stream()
				.anyMatch(currStack -> currStack.stream()
						.anyMatch(currRequest -> currRequest.requestedObjective == objective))) {

			logger.debug("Objective exists in current stack. Objective: [{}]. Current stacks: [{}]", objective, stacksToString());
			objectiveRequest.setInterrupted();
			return objectiveRequest;
		}
		
		if (hasUninterruptibleConflicts(objective)) {
			logger.debug("Objective conflicts with current stack. Objective: [{}]. Current stacks: [{}]", objective, stacksToString());
			objectiveRequest.setInterrupted();
			return objectiveRequest;
		}
		
		// TODO: add a way for objectives to interrupt other objectives that are already running
		
		Deque<ObjectiveRequest> objectiveRequestStack = new LinkedList<>();
		objectiveRequestStack.addFirst(objectiveRequest);
		
		List<PreconditionMapping> preconditions = objective.getPreconditions();
		for (PreconditionMapping precondition : preconditions) {
			if (!precondition.isMet()) {
				Objective blockingObjective = precondition.getResolution();
				logger.debug("[{}] is blocking [{}]. Stack: [{}]", blockingObjective, objective, stackToString(objectiveRequestStack));
				if (isCircularQueuing(objectiveRequestStack, blockingObjective)) {
					logger.debug("Circular queueing detected when queueing objective: [{}].", objective);
					interruptStack(objectiveRequestStack);
					return objectiveRequest;
				}
				if (hasUninterruptibleConflicts(blockingObjective)) {
					logger.debug("Objective conflicts with current stack. Objective: [{}]. Current stacks: [{}]", objective, stacksToString());
					interruptStack(objectiveRequestStack);
					return objectiveRequest;
				}
				ObjectiveRequest blockingRequest = queuePreconditionResolution(objectiveRequestStack, blockingObjective);
				objectiveRequest.addBlockingObjective(blockingRequest);
			}
		}
		
		interruptConflictingRequests(objectiveRequestStack);
		
		objectiveRequestStacks.add(objectiveRequestStack);
		logger.debug("Queued new objective: [{}]. Request: [{}].", objective, objectiveRequest);
		logStacks();
		return objectiveRequest;
	}
	
	private Deque<ObjectiveRequest> getConflictingStack(Objective objective) {
		Set<Subsystem> subsystems = objective.getRequiredSubsystems();
		for (Deque<ObjectiveRequest> objectiveRequestStack : objectiveRequestStacks) {
			for (ObjectiveRequest queuedObjectiveRequest : objectiveRequestStack) {
				Set<Subsystem> queuedSubsystems = queuedObjectiveRequest.requestedObjective.getRequiredSubsystems();
				// Check if any fakesubsystems match
				if (!Collections.disjoint(subsystems, queuedSubsystems)) {
					return objectiveRequestStack;
				}
			}
		}
		return null;
	}
	
	private boolean hasUninterruptibleConflicts(Objective objective) {
		Deque<ObjectiveRequest> conflictingStack = getConflictingStack(objective);
		if (conflictingStack == null) {
			return false;
		}
		ConflictingStrategy strategy = objective.getConflictingStrategy();
		switch (strategy) {
			case DENY_REQUESTED:
				return true;
			case INTERRUPT_CONFLICTS:
				return false;
			default:
				throw new IllegalArgumentException("Invalid ConflictingStrategy [" + strategy + "].");
		}
	}
	
	private void interruptConflictingRequests(Deque<ObjectiveRequest> objectiveRequestStack) {
		logger.debug("Interrupting conflicting requests of stack [{}]", stackToString(objectiveRequestStack));
		for (ObjectiveRequest objectiveRequest : objectiveRequestStack) {
			Deque<ObjectiveRequest> conflictingStack = getConflictingStack(objectiveRequest.getRequestedObjective());
			if (conflictingStack == null) {
				continue;
			}
			interruptStack(conflictingStack);
			// TODO: ask Preston if we should also pop a conflicting stack after interrupting it
		}
	}
	

	// Subclass callbacks
	// ============================================================
	/**
	 * Subclasses may override this method to do some reporting on errors such as 
	 * vibrating a controller giving the driver feedback that something went wrong.
	 * 
	 * @see #onError(Exception)
	 */
	protected void onError(ErrorType error) {
		
	}
	
	/**
	 * Subclasses may override this method to do some reporting on errors such as 
	 * vibrating a controller giving the driver feedback that something went wrong.
	 * 
	 * @see #onError(ErrorType)
	 */
	protected void onError(Exception exception) {
		
	}


	// Helpers
	// ============================================================
	private String stacksToString() {
		return objectiveRequestStacks.stream()
				.map(this::stackToString)
				.collect(Collectors.joining(" | "));
	}
	
	private String stackToString(Deque<ObjectiveRequest> objectiveRequestStack) {
		return objectiveRequestStack.stream()
				.map(objReq -> objReq.getRequestedObjective().toString())
				.collect(Collectors.joining(" -> "));
	}
	
	private void logStacks() {
		String s = stacksToString();
		SmartDashboard.putString("Objective Stacks", s);
		logger.debug("Stacks: [{}].", s);
	}
	
	public enum ErrorType {
		/** An {@link Objective}'s {@link ObjectivePrecondition} failed and the resolution was the same as 
		 * the initial Objective. This will cause an infinite loop of Objective queuing so instead, the
		 * Objective stack is cleared. This will cause any currently running Objectives to end abruptly, first
		 * calling interrupt on the Objective. */
		CIRCULAR_QUEUING
	}
}
