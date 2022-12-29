package com.team2073.common.command;

import edu.wpi.first.wpilibj2.command.InstantCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @see AbstractLoggingCommand
 * @author Preston Briggs
 *
 */
public abstract class AbstractLoggingInstantCommand extends InstantCommand {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private final String className = getClass().getSimpleName();
	
	
	public AbstractLoggingInstantCommand() {
		super();
		logger.debug("Constructing [{}] command.", className);
	}
//	public AbstractLoggingInstantCommand(String name) {
//		super(name);
//		logger.debug("Constructing [{}] command.", className);
//	}
	

	/*
	 * Unfortunately we have to copy/paste code from AbstractLoggingCommand since
	 * we are already extending InstantCommand.
	 */

	// ================================================================================
	// START - Copy from AbstractLoggingCommand
	// ================================================================================
	@Override
	public final void initialize() {
		logger.debug("[{}] command initializing...", className);
		initializeDelegate();
		logger.debug("[{}] command initialized successfully.", className);
	}

	protected void initializeDelegate() {
		super.initialize();
	}

	@Override
	public final void execute() {
		logger.trace("[{}] command executing...", className);
		executeDelegate();
		logger.trace("[{}] command executed successfully.", className);
	}

	protected void executeDelegate() {
		super.execute();
	}

	@Override
	public final void end(boolean interruptible) {
		logger.debug("[{}] command ending...", className);
		endDelegate(interruptible);
		logger.debug("[{}] command ended successfully.", className);
	}

	protected void endDelegate(boolean interruptible) {
		super.end(interruptible);
	}

//	@Override
//	public final void interrupted() {
//		logger.debug("[{}] command interrupting...", className);
//		interruptedDelegate();
//		logger.debug("[{}] command interrupted successfully.", className);
//	}
//
//	protected void interruptedDelegate() {
//		super.interrupted();
//	}
	
	@Override
	public final boolean isFinished() {
		logger.trace("[{}] command checking finished status...", className);
		boolean isFinished = isFinishedDelegate();
		if(!isFinished)
			logger.trace("[{}] command returned finished status of [{}].", className, isFinished);
		else
			logger.debug("[{}] command returned finished status of [{}].", className, isFinished);
			
		return isFinished;
	}
	// ================================================================================
	// END - Copy from AbstractLoggingCommand
	// ================================================================================


	protected boolean isFinishedDelegate() {
		return super.isFinished();
	}
}
