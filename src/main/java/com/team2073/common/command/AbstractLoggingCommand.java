package com.team2073.common.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Commands can extend this class (or one of it's siblings listed in
 * {@literal @}see section) to automatically add logging functionality to the
 * commands. Messages are logged at the start and end of key methods to help
 * determine when a command is failing, etc.
 * 
 * <h3>How to Use this Class</h3> To add this logging, simply extend this class
 * and override the *delegate(...) methods instead of the standard
 * {@link Command} methods. For example, instead of overriding execute(), the
 * subclass should override executeDelegate().
 * 
 * <h3>Message Frequency</h3> Repetitive messages (messages called continuously
 * during the command) are logged at a trace level and "once per command"
 * messages (methods that are called once per command 'run') are logged at a
 * debug level. Turning on debug logging should log ~10 lines per command run.
 * Turning on trace logging will, however, log ~ 200 lines per second.
 * 
 * <h3>Internal Example</h3> An example of what this class is doing internally:
 * 
 * <pre>
 * 
	{@literal @}Override
	protected final void initialize() {
		logger.debug("[{}] command initializing...", className);
		initializeDelegate(); // Subclass functionality is called here
		logger.debug("[{}] command initialized successfully.", className);
	}

	protected void initializeDelegate() {
		// Subclass functionality is called here
	}
 * </pre>
 * 
 * @see AbstractLoggingCommandGroup
 * @see AbstractLoggingInstantCommand
 * @author Gabe Bui & Preston Briggs
 *
 */
public abstract class AbstractLoggingCommand extends Command {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	protected final String className = getClass().getSimpleName();

	/*
	 * IF CHANGING THIS CLASS, UPDATE AbstractLoggingCommandGroup AND AbstractLoggingInstantCommand!
	 * 
	 * AbstractLoggingCommandGroup and AbstractLoggingInstantCommand both use the same code
	 * as this class but due to extending CommandGroup and InstantCommand we had to manually
	 * copy/paste this code into those classes instead of extending this class.
	 */
	
	public AbstractLoggingCommand() {
		super();
		logger.debug("Constructing [{}] command.", className);
	}

	public AbstractLoggingCommand(double timeout) {
		super(timeout);
		logger.debug("Constructing [{}] command. timeout=[{}].", className, timeout);
	}

	public AbstractLoggingCommand(String name, double timeout) {
		super(name, timeout);
		logger.debug("Constructing [{}] command. timeout=[{}].", className, timeout);
	}

	public AbstractLoggingCommand(String name) {
		super(name);
		logger.debug("Constructing [{}] command.", className);
	}

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
		if(isFinished)
			logger.debug("[{}] command returned finished status of [{}].", className, isFinished);
		else
			logger.trace("[{}] command returned finished status of [{}].", className, isFinished);
			
		return isFinished;
	}

	protected abstract boolean isFinishedDelegate();
}
