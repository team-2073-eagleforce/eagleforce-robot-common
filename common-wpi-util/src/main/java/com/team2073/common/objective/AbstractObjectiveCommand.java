package com.team2073.common.objective;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.team2073.common.assertion.Assert;
import com.team2073.common.command.AbstractLoggingCommand;

public abstract class AbstractObjectiveCommand extends AbstractLoggingCommand {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private AbstractSubsystemCoordinator coordinator;
	
	private ObjectiveRequest request = null;
	
	/** 
	 * An alternative constructor not requiring an AbstractSubsystemCoordinator. Must explictly set one
	 * using {@link #setCoordinator(AbstractSubsystemCoordinator)}. This constructor allows for more
	 * flexibility such as using dependency injection to inject the coordinator. 
	 */
	public AbstractObjectiveCommand() {
	}
	
	/** Constructor that sets the AbstractSubsystemCoordinator to be used by subclasses. */
	public AbstractObjectiveCommand(AbstractSubsystemCoordinator coordinator) {
		setCoordinator(coordinator);
	}
//
//	public AbstractObjectiveCommand(String name) {
//		super(name);
//	}

	@Override
	protected void initializeDelegate() {
		request = getCoordinator().queue(initializeObjective());
	}
	
	@Override
	protected boolean isFinishedDelegate() {
		if(request == null) {
			Exception forLoggingOnly = new IllegalStateException("request must not be null");
			logger.warn("Request was null during isFinished [{}] Returning true.", getClass().getSimpleName(), forLoggingOnly);
			return true;
		}
		return request.isFinished();
	}

	@Override
	protected void endDelegate(boolean interruptible) {
		request = null;
	}
	
//	@Override
//	protected void interruptedDelegate() {
//		if(request == null) {
//			Exception forLoggingOnly = new IllegalStateException("request must not be null");
//			logger.warn("Request was null during interrupt [{}].", getClass().getSimpleName(), forLoggingOnly);
//			return;
//		}
//		request.setInterrupted();
//	}
	
	protected AbstractSubsystemCoordinator getCoordinator() {
		if(coordinator == null)
			throw new IllegalStateException("AbstractSubsystemCoordinator was never set. If you used the default constructor you must "
					+ "explicitly call setCoordinator(AbstractSubsystemCoordinator) before attempting to retrieve coordinator.");
		return coordinator;
	}
	
	protected void setCoordinator(AbstractSubsystemCoordinator coordinator) {
		Assert.assertNotNull(coordinator, "coordinator");
		this.coordinator = coordinator;
	}
	
	protected abstract Objective initializeObjective();

}
