package com.team2073.common.command.wrapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.team2073.common.command.wrapping.impl.ExceptionWrappingCommand;
import com.team2073.common.command.wrapping.impl.LogWrappingCommand;

//import edu.wpi.first.wpilibj.command.Command;

/**
 * A utility class for easily wrapping commands.
 * <p>
 * <i>Note: See {@link WrappableCommand} as the root of documentation for command wrapping.</i>
 * <p>
 * <h2>How to Use</h2>
 * 1) Create a CommandWrapperBuilder
 * 2) Add Command Wrappers
 * 3) Build 
 * <pre>
 * WrappableCommand moveMotorCommand = 
 * 	WrapUtil.create(new DevMoveMotorCommand())
 * 	.logWrap()
 * 	.exceptionWrap()
 * 	.build();
 * </pre>
 * <h2>Shortcut to Wrap All</h2>
 * If you want to include all wrappers, you can use the shortcut:
 * <pre>
 * WrappableCommand moveMotorCommand = WrapUtil.wrapAllAndBuild(new DevMoveMotorCommand());
 * </pre>
 * 
 * @author Preston Briggs
 * 
 * @see WrappableCommand
 */
public abstract class WrapUtil {
//
//	/** Shortcut to create a {@link CommandWrapperBuilder}. */
//	public static CommandWrapperBuilder create(WrappableCommand cmd) {
//		return new CommandWrapperBuilder(cmd);
//	}
//
//	/** A shortcut to wrap a command in every wrapper available in this class. */
//	public static Command wrapAllAndBuild(WrappableCommand cmd) {
//		return create(cmd)
//				.logWrap()
//				.exceptionWrap()
//				.build();
//	}
//
//	/**
//	 * The internal builder used by {@link WrapUtil}.
//	 *
//	 * @author Preston Briggs
//	 */
//	public static class CommandWrapperBuilder {
//		private final Logger logger = LoggerFactory.getLogger(getClass());
//
//		private WrappableCommand wrappedCmd;
//
//		/** See {@link CommandWrapperBuilder} */
//		public CommandWrapperBuilder(WrappableCommand cmd) {
//			this.wrappedCmd = cmd;
//			logger.debug("Created CommandWrapperBuilder for [{}].", wrappedCmd.getClass().getSimpleName());
//		}
//
//		/** Wrap command in a {@link LogWrappingCommand}. */
//		public CommandWrapperBuilder logWrap() {
//			logger.debug("Wrapping [{}] in LogWrappingCommand", wrappedCmd.getClass().getSimpleName());
//			wrappedCmd = new LogWrappingCommand(wrappedCmd);
//			return this;
//		}
//
//		/** Wrap command in a {@link ExceptionWrappingCommand}. */
//		public CommandWrapperBuilder exceptionWrap() {
//			logger.debug("Wrapping [{}] in ExceptionWrappingCommand", wrappedCmd.getClass().getSimpleName());
//			wrappedCmd = new ExceptionWrappingCommand(wrappedCmd);
//			return this;
//		}
//
//		/**
//		 * Call this at the end of the chain to return the wrapped command.
//		 *
//		 * @return The fully wrapped command, ready to be used.
//		 */
//		public Command build() {
//			logger.debug("Building Command from [{}].", wrappedCmd.getClass().getSimpleName());
//			return wrappedCmd;
//		}
//
//		/**
//		 * <b>Use {@link #build()} instead.</b>
//		 * <p>
//		 * This is not the preferred method of building a
//		 * {@link WrappableCommand}. It is just here in case you need to access
//		 * the returned command as a {@link WrappableCommand}. In order to make
//		 * this work, the {@link WrappableCommand} needed to increase the
//		 * visibility of all the {@link Command} methods from protected to
//		 * public. In order to retain the initial public API of the
//		 * {@link Command} class, a {@link Command} is returned from the
//		 * {@link #build()} method (so you no longer have access to the internal
//		 * methods).
//		 *
//		 * @return
//		 */
//		@Deprecated
//		public WrappableCommand buildAsWrappable() {
//			logger.debug("Building WrappableCommand from [{}].", wrappedCmd.getClass().getSimpleName());
//			return wrappedCmd;
//		}
//	}
}