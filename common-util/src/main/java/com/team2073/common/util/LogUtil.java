package com.team2073.common.util;

import org.slf4j.Logger;

public abstract class LogUtil {
	
	private static final String CONSTRUCTOR_MSG = "Constructing [{}].";
	private static final String CONSTRUCTOR_END_MSG = "Constructing [{}] complete.";
	private static final String INITIALIZE_MSG = "Initializing [{}].";
	private static final String INITIALIZE_END_MSG = "Initializing [{}] complete.";

	// Constructor start
	// ================================================================================
	/** Logs the start of object construction. Ex message: "Constructing [SomeClassName]." <p>
	 * Example use: <p><b>LogUtil.infoConstruct(this, logger);</b> */
	public static void infoConstruct(Object clazz, Logger logger) {
		infoConstruct(clazz.getClass(), logger);
	}

	/** See {@link #infoConstruct(Object, Logger)}. */
	public static void infoConstruct(Class<?> clazz, Logger logger) {
		logger.info(CONSTRUCTOR_MSG, clazz.getSimpleName());
	}

	/** See {@link #infoConstruct(Object, Logger)}. */
	public static void debugConstruct(Object clazz, Logger logger) {
		debugConstruct(clazz.getClass(), logger);
	}

	/** See {@link #infoConstruct(Object, Logger)}. */
	public static void debugConstruct(Class<?> clazz, Logger logger) {
		logger.debug(CONSTRUCTOR_MSG, clazz.getSimpleName());
	}

	/** See {@link #infoConstruct(Object, Logger)}. */
	public static void traceConstruct(Object clazz, Logger logger) {
		traceConstruct(clazz.getClass(), logger);
	}

	/** See {@link #infoConstruct(Object, Logger)}. */
	public static void traceConstruct(Class<?> clazz, Logger logger) {
		logger.trace(CONSTRUCTOR_MSG, clazz.getSimpleName());
	}

	// Constructor end
	// ================================================================================
	/** Logs object construction complete. Ex message: "Constructing [SomeClassName] complete." <p>
	 * Example use: <p><b>LogUtil.infoConstructEnd(this.getClass(), logger);</b> */
	public static void infoConstructEnd(Object clazz, Logger logger) {
		infoConstructEnd(clazz.getClass(), logger);
	}

	/** See {@link #infoConstructEnd(Object, Logger)}. */
	public static void infoConstructEnd(Class<?> clazz, Logger logger) {
		logger.info(CONSTRUCTOR_END_MSG, clazz.getSimpleName());
	}

	/** See {@link #infoConstructEnd(Object, Logger)}. */
	public static void debugConstructEnd(Object clazz, Logger logger) {
		debugConstructEnd(clazz.getClass(), logger);
	}

	/** See {@link #infoConstructEnd(Object, Logger)}. */
	public static void debugConstructEnd(Class<?> clazz, Logger logger) {
		logger.debug(CONSTRUCTOR_END_MSG, clazz.getSimpleName());
	}

	/** See {@link #infoConstructEnd(Object, Logger)}. */
	public static void traceConstructEnd(Object clazz, Logger logger) {
		traceConstructEnd(clazz.getClass(), logger);
	}

	/** See {@link #infoConstructEnd(Object, Logger)}. */
	public static void traceConstructEnd(Class<?> clazz, Logger logger) {
		logger.trace(CONSTRUCTOR_END_MSG, clazz.getSimpleName());
	}

	// Initialization start
	// ================================================================================
	/** Logs the start of object initialization. Ex message: "Initializing [SomeClassName]." <p>
	 * Example use: <p><b>LogUtil.infoInit(this, logger);</b> */
	public static void infoInit(Object clazz, Logger logger) {
		infoInit(clazz.getClass(), logger);
	}

	/** See {@link #infoInit(Object, Logger)}. */
	public static void infoInit(Class<?> clazz, Logger logger) {
		logger.info(INITIALIZE_MSG, clazz.getSimpleName());
	}

	/** See {@link #infoInit(Object, Logger)}. */
	public static void debugInit(Object clazz, Logger logger) {
		debugInit(clazz.getClass(), logger);
	}

	/** See {@link #infoInit(Object, Logger)}. */
	public static void debugInit(Class<?> clazz, Logger logger) {
		logger.debug(INITIALIZE_MSG, clazz.getSimpleName());
	}

	/** See {@link #infoInit(Object, Logger)}. */
	public static void traceInit(Object clazz, Logger logger) {
		traceInit(clazz.getClass(), logger);
	}

	/** See {@link #infoInit(Object, Logger)}. */
	public static void traceInit(Class<?> clazz, Logger logger) {
		logger.trace(INITIALIZE_MSG, clazz.getSimpleName());
	}

	// Initialization end
	// ================================================================================
	/** Logs object initialization complete. Ex message: "Initializing [SomeClassName] complete." <p>
	 * Example use: <p><b>LogUtil.infoInitEnd(this.getClass(), logger);</b> */
	public static void infoInitEnd(Object clazz, Logger logger) {
		infoInitEnd(clazz.getClass(), logger);
	}

	/** See {@link #infoInitEnd(Object, Logger)}. */
	public static void infoInitEnd(Class<?> clazz, Logger logger) {
		logger.info(INITIALIZE_END_MSG, clazz.getSimpleName());
	}

	/** See {@link #infoInitEnd(Object, Logger)}. */
	public static void debugInitEnd(Object clazz, Logger logger) {
		debugInitEnd(clazz.getClass(), logger);
	}

	/** See {@link #infoInitEnd(Object, Logger)}. */
	public static void debugInitEnd(Class<?> clazz, Logger logger) {
		logger.debug(INITIALIZE_END_MSG, clazz.getSimpleName());
	}

	/** See {@link #infoInitEnd(Object, Logger)}. */
	public static void traceInitEnd(Object clazz, Logger logger) {
		traceInitEnd(clazz.getClass(), logger);
	}

	/** See {@link #infoInitEnd(Object, Logger)}. */
	public static void traceInitEnd(Class<?> clazz, Logger logger) {
		logger.trace(INITIALIZE_END_MSG, clazz.getSimpleName());
	}

	// Other util methods
	// ================================================================================
	/** Converts Logger format "{}" to String.format style "%s". */
	public static String convertLogToStrFmt(String string) {
		return string.replace("{}", "%s");
	}

	/** Converts String.format style "%s" to Logger style "{}". */
	public static String convertStrFmtToLog(String string) {
		return string.replace("%s", "{}");
	}
}
