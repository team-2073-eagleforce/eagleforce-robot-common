package com.team2073.common.command.wrapping.impl;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.team2073.common.command.wrapping.BaseWrappingCommand;
import com.team2073.common.command.wrapping.WrappableCommand;

/**
 * Wraps a {@link WrappableCommand} to add exception handling. If an exception is thrown
 * in any of the supported methods, it will be suppressed and logged at error level.
 * <p>
 * <i>Note: See {@link WrappableCommand} as the root of documentation for command wrapping.</i>
 * 
 * @author Preston Briggs
 *
 */
public class ExceptionWrappingCommand extends BaseWrappingCommand {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	/** See {@link ExceptionWrappingCommand}. */
	public ExceptionWrappingCommand(WrappableCommand toWrap) {
		super(toWrap);
	}

	@Override
	public void initialize() {
		boolean exceptionThrown = handleVoid(innerCommand::initialize, "initialize");
		if(exceptionThrown) {
			logger.error("initialize() method failed. Ending command...");
			cancel();
		}
	}

	@Override
	public void execute() {
		boolean exceptionThrown = handleVoid(innerCommand::execute, "execute");
		if(exceptionThrown) {
			logger.error("execute() method failed. Ending command...");
			cancel();
		}
	}

	@Override
	public void end() {
		handleVoid(innerCommand::end, "end");
	}

	@Override
	public void interrupted() {
		handleVoid(innerCommand::interrupted, "interrupted");
	}
	
	@Override
	public boolean isFinished() {
		return handle(innerCommand::isFinished, "isFinished", true
				, "Unable to determine result of isFinished() call. Returning false to end command.");
	}
	
	/**
	 * Wrap the given action (method call) in a try/catch and log then swallow any exceptions.
	 * 
	 * @param action The method to be called.
	 * @param methodName Used for logging.
	 * @return Whether or not an exception was thrown.
	 */
	private boolean handleVoid(Runnable action, String methodName) {
		try {
			action.run();
			return false;
		} catch (Exception e) {
			logger.error("An exception occurred executing [{}] on command [{}]. Exception will be supressed."
					, methodName, className, e);
			return true;
		}
	}
	
	private <R> R handle(Callable<R> action, String methodName, R defaultValue) {
		return handle(action, methodName, defaultValue, "");
	}
	
	private <R> R handle(Callable<R> action, String methodName, R defaultValue, String additionalMessage) {
		try {
			return action.call();
		} catch (Exception e) {
			logger.error("An exception occurred executing [{}] on command [{}]. Exception will be supressed. {}"
					, methodName, className, additionalMessage, e);
			return defaultValue;
		}
	}

}
