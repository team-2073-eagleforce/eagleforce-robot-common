package com.team2073.common.command.wrapping;

//import com.team2073.common.command.wrapping.WrapUtil.CommandWrapperBuilder;
import com.team2073.common.command.wrapping.impl.ExceptionWrappingCommand;
import com.team2073.common.command.wrapping.impl.LogWrappingCommand;
//import edu.wpi.first.wpilibj.buttons.JoystickButton;
//import edu.wpi.first.wpilibj.command.Command;
//import edu.wpi.first.wpilibj.command.Subsystem;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


public abstract class WrappableCommand {
	
//	public static final double TIMEOUT_UNINITIALIZED = -1;
//
//	/**
//	 * This variable is just a holder for JavaDocs we only want internally.
//	 * <p>
//	 * Due to the design of the wpilib {@link Command} class, some hackery had
//	 * to be done to get all this to work properly.
//	 * <h2>Copying Internal Command State</h2> We need access to some of the
//	 * internal state variables of the {@link Command} class. To achieve this,
//	 * we create copies of various variables and provide internal getters to
//	 * them.
//	 */
//	@SuppressWarnings("unused")
//	private boolean internalJavaDocs;
//
//	/**
//	 * A duplicate of {@link Command#m_requirements}. See
//	 * {@link #internalJavaDocs} for more info.
//	 *
//	 * @see #getRequirementsCopy()
//	 */
//	private final Set<Subsystem> requirementsCopy = new HashSet<>();
//
//	/**
//	 * A duplicate of {@link Command#m_timeout}. See {@link #internalJavaDocs}
//	 * for more info.
//	 *
//	 * @see #getTimeoutCopy()
//	 */
//	private double timeoutCopy = TIMEOUT_UNINITIALIZED;
//
//	/** @see Command#Command() Command() */
//	public WrappableCommand() {
//		super();
//	}
//
//	/** @see Command#Command(double) Command(double) */
//	public WrappableCommand(double timeout) {
//		super(timeout);
//		timeoutCopy = timeout;
//	}
//
//	/** @see Command#Command(String, double) Command(String, double) */
//	public WrappableCommand(String name, double timeout) {
//		super(name, timeout);
//		timeoutCopy = timeout;
//	}
//
//	/** @see Command#Command(String) Command(String) */
//	public WrappableCommand(String name) {
//		super(name);
//	}
//
//	/**
//	 * See {@link #internalJavaDocs} for more info.
//	 *
//	 * @return The time (in seconds) before this command "times out" (or -1 if
//	 *         no timeout).
//	 */
//	public synchronized double getTimeoutCopy() {
//		return timeoutCopy;
//	}
//
//	@Override
//	public abstract boolean isFinished();
//
//	@Override
//	public synchronized void requires(Subsystem subsystem) {
//		super.requires(subsystem);
//		requirementsCopy.add(subsystem);
//	}
//
//	/**
//	 * See {@link #internalJavaDocs} for more info.
//	 *
//	 * @return the requirements (as a {@link Set} of {@link Subsystem
//	 *         Subsystems}) of this command
//	 */
//	public synchronized Set<Subsystem> getRequirementsCopy() {
//		return Collections.unmodifiableSet(requirementsCopy);
//	}
//
//	@Override
//	public void initialize() {
//		super.initialize();
//	}
//
//	@Override
//	public void execute() {
//		super.execute();
//	}
//
//	@Override
//	public void end() {
//		super.end();
//	}
//
//	@Override
//	public void interrupted() {
//		super.interrupted();
//	}
//
//	@Override
//	public synchronized void setInterruptible(boolean interruptible) {
//		super.setInterruptible(interruptible);
//	}

}
