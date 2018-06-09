package com.team2073.common.util;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A set of utilities for handling exceptions.
 * 
 * @author Preston Briggs
 */
public class ExceptionUtil {
	private static final Logger logger = LoggerFactory.getLogger(ExceptionUtil.class);


	// Logging versions
	// ============================================================
	/**
	 * Wrap the given method call in a try/catch and log then suppress any
	 * exceptions. Only used for methods with return type of void. For methods
	 * with a return value, see {@link #suppress(Callable, String)}.
	 * 
	 * @param action
	 *            The method to be called
	 * @param methodName
	 *            Used for logging.
	 * @return Whether or not an exception was thrown
	 */
	public static boolean suppressVoid(Runnable action, String methodName) {
		return suppressVoid(action, methodName, null);
	}

	/** An alternative to {@link #suppressVoid(Callable, String)} that allows specifying an additional message to log. */
	public static boolean suppressVoid(Runnable action, String methodName, String additionalMessage) {
		return suppressVoidInternal(action, methodName, additionalMessage, true);
	}


	// Non-logging versions
	// ============================================================
	/** An alternative to {@link #suppressVoid(Runnable, String)} without logging. */
	public static boolean suppressVoidNoLog(Runnable action, String methodName) {
		return suppressVoidNoLog(action, methodName, null);
	}

	/** An alternative to {@link #suppressVoid(Runnable, String, String)} without logging. */
	public static boolean suppressVoidNoLog(Runnable action, String methodName, String additionalMessage) {
		return suppressVoidInternal(action, methodName, additionalMessage, false);
	}


	// Internal version
	// ============================================================
	private static boolean suppressVoidInternal(Runnable action, String methodName, String additionalMessage, boolean logMsg) {
		try {
			action.run();
			return false;
		} catch (Exception e) {
			if(logMsg) {
				if(!StringUtil.isEmpty(additionalMessage))
					logger.error("An exception occurred invoking [{}] method. Suppressing exception. {}", methodName, additionalMessage, e);
				else
					logger.error("An exception occurred invoking [{}] method. Suppressing exception.", methodName, e);
			}
			return true;
		}
	}

	// Logging versions
	// ============================================================
	/**
	 * Wrap the given method call in a try/catch and log then suppress any
	 * exceptions. Returns the value returned from the method call or null if 
	 * an exception was thrown during invocation.
	 * <p>
	 * Use {@link #suppress(Callable, String, Object)} to specify a default return value
	 * on exception.
	 * 
	 * @param action The method to be called
	 * @param methodName Used for logging
	 * @return The object returned from the action's invocation or null if an exception occurred
	 */
	public static <R> R suppress(Callable<R> action, String methodName) {
		return suppress(action, methodName, (R)null);
	}
	
	/**
	 * An alternative to {@link #suppress(Callable, String)} that allows specifying a default value to return.
	 * @param defaultValue The object returned in case an exception was thrown.
	 * @return The object returned from the action's invocation or the defaultValue if an exception occurred
	 */
	public static <R> R suppress(Callable<R> action, String methodName, R defaultValue) {
		return suppress(action, methodName, defaultValue, null);
	}

	/** An alternative to {@link #suppress(Callable, String)} that allows specifying an additional message to log. */
	public static <R> R suppress(Callable<R> action, String methodName, String additionalMessage) {
		return suppress(action, methodName, null, additionalMessage);
	}
	
	/** An alternative to {@link #suppress(Callable, String, Object)} that allows specifying an additional message to append. */
	public static <R> R suppress(Callable<R> action, String methodName, R defaultValue, String additionalMessage) {
		return suppressInternal(action, methodName, defaultValue, additionalMessage, true);
	}

	// Non-logging versions
	// ============================================================
	/** Alternative to {@link #suppress(Callable, String)} without logging */
	public static <R> R suppressNoLog(Callable<R> action, String methodName) {
		return suppressNoLog(action, methodName, (R)null);
	}

	/** Alternative to {@link #suppress(Callable, String, Object)} without logging */
	public static <R> R suppressNoLog(Callable<R> action, String methodName, R defaultValue) {
		return suppressNoLog(action, methodName, defaultValue, null);
	}

	/** Alternative to {@link #suppress(Callable, String, String)} without logging */
	public static <R> R suppressNoLog(Callable<R> action, String methodName, String additionalMessage) {
		return suppressNoLog(action, methodName, null, additionalMessage);
	}

	/** Alternative to {@link #suppress(Callable, String, Object, String)} without logging */
	public static <R> R suppressNoLog(Callable<R> action, String methodName, R defaultValue, String additionalMessage) {
		return suppressInternal(action, methodName, defaultValue, additionalMessage, false);
	}

	// Internal version
	// ============================================================
	/**
	 * The internal method all suppress method use.
	 * 
	 * @param action The method to be invoked
	 * @param methodName Used for logging
	 * @param defaultValue The value to return if an exception occurs
	 * @param additionalMessage Additional message to log
	 * @param logMsg Whether to log a message
	 * @return The object returned from the action's invocation or the defaultValue if an exception occurred
	 */
	private static <R> R suppressInternal(Callable<R> action, String methodName, R defaultValue, String additionalMessage, boolean logMsg) {
		try {
			return action.call();
		} catch (Exception e) {
			if(logMsg) {
				if(!StringUtil.isEmpty(additionalMessage))
					logger.error("An exception occurred invoking [{}] method. Suppressing exception. {}", methodName, additionalMessage, e);
				else
					logger.error("An exception occurred invoking [{}] method. Suppressing exception.", methodName, e);
			}
			return defaultValue;
		}
	}

}