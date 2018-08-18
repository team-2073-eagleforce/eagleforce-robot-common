package com.team2073.common.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * See JavaDocs of {@link AbstractLoggingCommand}. This class is the
 * same but for {@link CommandGroup}s.
 * 
 * @see AbstractLoggingCommand
 * @author Preston Briggs
 *
 */
public abstract class AbstractLoggingCommandGroup extends CommandGroup {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	private final String className = getClass().getSimpleName();

	public AbstractLoggingCommandGroup() {
		super();
		logger.debug("Constructing [{}] command.", className);
	}

	public AbstractLoggingCommandGroup(String name) {
		super(name);
		logger.debug("Constructing [{}] command.", className);
	}
	
	
	/*
	 * Unfortunately we have to copy/paste code from AbstractLoggingCommand since
	 * we are already extending CommandGroup.
	 */

	// ================================================================================
	// START - Copy from AbstractLoggingCommand
	// ================================================================================
	@Override
	protected final void initialize() {
		logger.debug("[{}] command initializing...", className);
		initializeDelegate();
		logger.debug("[{}] command initialized successfully.", className);
	}

	protected void initializeDelegate() {
		super.initialize();
	}

	@Override
	protected final void execute() {
		logger.trace("[{}] command executing...", className);
		executeDelegate();
		logger.trace("[{}] command executed successfully.", className);
	}

	protected void executeDelegate() {
		super.execute();
	}

	@Override
	protected final void end() {
		logger.debug("[{}] command ending...", className);
		endDelegate();
		logger.debug("[{}] command ended successfully.", className);
	}

	protected void endDelegate() {
		super.end();
	}

	@Override
	protected final void interrupted() {
		logger.debug("[{}] command interrupting...", className);
		interruptedDelegate();
		logger.debug("[{}] command interrupted successfully.", className);
	}

	protected void interruptedDelegate() {
		super.interrupted();
	}
	
	@Override
	protected final boolean isFinished() {
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
