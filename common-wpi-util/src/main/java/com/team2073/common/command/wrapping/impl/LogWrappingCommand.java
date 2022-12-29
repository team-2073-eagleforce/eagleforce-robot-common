package com.team2073.common.command.wrapping.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.team2073.common.command.wrapping.BaseWrappingCommand;
import com.team2073.common.command.wrapping.WrappableCommand;

//import edu.wpi.first.wpilibj.command.Command;
//import edu.wpi.first.wpilibj.command.CommandGroup;
//import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.util.sendable.SendableBuilder;


public class LogWrappingCommand extends BaseWrappingCommand {
//	private final Logger logger = LoggerFactory.getLogger(getClass());
//
//	/**
//	 * See {@link LogWrappingCommand}.
//	 * @param toWrap The command that should be wrapped with logging.
//	 */
//	public LogWrappingCommand(WrappableCommand toWrap) {
//		super(toWrap);
//		logger.debug("Constructing [{}] command.", className);
//	}
//
//	@Override
//	public void initialize() {
//		logger.debug("[{}] command initializing...", className);
//		innerCommand.initialize();
//		logger.debug("[{}] command initialized successfully.", className);
//	}
//
//	@Override
//	public void execute() {
//		logger.trace("[{}] command executing...", className);
//		innerCommand.execute();
//		logger.trace("[{}] command executed successfully.", className);
//	}
//
//	@Override
//	public void end() {
//		logger.debug("[{}] command ending...", className);
//		innerCommand.end();
//		logger.debug("[{}] command ended successfully.", className);
//	}
//
//	@Override
//	public void interrupted() {
//		logger.debug("[{}] command interrupting...", className);
//		innerCommand.interrupted();
//		logger.debug("[{}] command interrupted successfully.", className);
//	}
//
//	@Override
//	public boolean isFinished() {
//		logger.trace("[{}] command checking finished status...", className);
//		boolean isFinished = innerCommand.isFinished();
//		if(isFinished)
//			logger.debug("[{}] command returned finished status of [{}].", className, isFinished);
//		else
//			logger.trace("[{}] command returned finished status of [{}].", className, isFinished);
//
//		return isFinished;
//	}
}
