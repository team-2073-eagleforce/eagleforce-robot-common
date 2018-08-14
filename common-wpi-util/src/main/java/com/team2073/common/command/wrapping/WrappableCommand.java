package com.team2073.common.command.wrapping;

import com.team2073.common.command.wrapping.WrapUtil.CommandWrapperBuilder;
import com.team2073.common.command.wrapping.impl.ExceptionWrappingCommand;
import com.team2073.common.command.wrapping.impl.LogWrappingCommand;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This is the core class in the Command Wrapping framework.
 * 
 * <h2>Command Wrapping Overview</h2>
 * <p>
 * Using this framework, you can wrap {@link Command}s to add functionality such
 * as logging and exception handling.
 * <p>
 * This works by passing the command you want wrapped into the constructor of
 * any of the command wrapper implementations (see subclasses of
 * {@link BaseWrappingCommand}). These command wrappers can be treated exactly
 * like a command (you can pass them into methods such as
 * {@link JoystickButton#whileActive(Command)}). They store a reference to the
 * 'innerCommand' and then intercept various methods and add their own
 * functionality before and after delegating the method call to the inner
 * command.
 * 
 * <h2>How to Wrap Commands</h2>
 * <p>
 * {@link Command} subclasses must extend {@link WrappableCommand} (instead of
 * extending {@link Command}) if they want to be eligible for command wrapping.
 * <p>
 * Example use:
 * 
 * <pre>
 * // SomeCommand must extend WrappableCommand
 * WrappableCommand myCommand = new SomeCommand();
 * WrappableCommand logWrappedMyCommand = new LogWrappingCommand(myCommand);
 * JoystickButton x = new JoystickButton(joystick, 1);
 * x.whileHeld(logWrappedMyCommand);
 * </pre>
 * 
 * <h2>Fully Configure Command Before Wrapping!</h2>
 * 
 * All modifications (timeout, subsystem, run when disabled, requires() etc.)
 * must be 100% complete before wrapping. Any modifications made after the
 * command has already been wrapped will not work properly.
 * <p>
 * If a timeout exists, it must have been passed in as a constructor argument of
 * {@link WrappableCommand}.
 * 
 * <h2>How to Use Multiple Command Wrappers</h2>
 * 
 * Command Wrappers are able to wrap other Command Wrappers
 * 
 * <pre>
 * // SomeCommand must extend WrappableCommand
 * WrappableCommand myCommand = new SomeCommand();
 * WrappableCommand logWrappedCommand = new LogWrappingCommand(myCommand);
 * WrappableCommand exceptionWrappedCommand = new ExceptionWrappingCommand(logWrappedCommand);
 * JoystickButton x = new JoystickButton(joystick, 1);
 * x.whileHeld(exceptionWrappedCommand);
 * </pre>
 * 
 * <h2>Command Wrapping Utility</h2>
 * 
 * There is also a utility class for wrapping commands: {@link CommandWrapperBuilder}.
 * 
 * <h2>How to Create Custom Command Wrappers</h2>
 * <p>
 * To create your own command wrapper implementation, see
 * {@link BaseWrappingCommand}.
 * 
 * @author Preston Briggs
 * @author Gabe Bui
 * 
 * @see BaseWrappingCommand
 * @see CommandWrapperBuilder
 * @see ExceptionWrappingCommand
 * @see LogWrappingCommand
 *
 */
public abstract class WrappableCommand extends Command {
	
	public static final double TIMEOUT_UNINITIALIZED = -1;

	/**
	 * This variable is just a holder for JavaDocs we only want internally.
	 * <p>
	 * Due to the design of the wpilib {@link Command} class, some hackery had
	 * to be done to get all this to work properly.
	 * <h2>Copying Internal Command State</h2> We need access to some of the
	 * internal state variables of the {@link Command} class. To achieve this,
	 * we create copies of various variables and provide internal getters to
	 * them.
	 */
	@SuppressWarnings("unused")
	private boolean internalJavaDocs;

	/**
	 * A duplicate of {@link Command#m_requirements}. See
	 * {@link #internalJavaDocs} for more info.
	 * 
	 * @see #getRequirementsCopy()
	 */
	private final Set<Subsystem> requirementsCopy = new HashSet<>();

	/**
	 * A duplicate of {@link Command#m_timeout}. See {@link #internalJavaDocs}
	 * for more info.
	 * 
	 * @see #getTimeoutCopy()
	 */
	private double timeoutCopy = TIMEOUT_UNINITIALIZED;

	/** @see Command#Command() Command() */
	public WrappableCommand() {
		super();
	}

	/** @see Command#Command(double) Command(double) */
	public WrappableCommand(double timeout) {
		super(timeout);
		timeoutCopy = timeout;
	}

	/** @see Command#Command(String, double) Command(String, double) */
	public WrappableCommand(String name, double timeout) {
		super(name, timeout);
		timeoutCopy = timeout;
	}

	/** @see Command#Command(String) Command(String) */
	public WrappableCommand(String name) {
		super(name);
	}

	/**
	 * See {@link #internalJavaDocs} for more info.
	 * 
	 * @return The time (in seconds) before this command "times out" (or -1 if
	 *         no timeout).
	 */
	public synchronized double getTimeoutCopy() {
		return timeoutCopy;
	}

	@Override
	public abstract boolean isFinished();

	@Override
	public synchronized void requires(Subsystem subsystem) {
		super.requires(subsystem);
		requirementsCopy.add(subsystem);
	}

	/**
	 * See {@link #internalJavaDocs} for more info.
	 * 
	 * @return the requirements (as a {@link Set} of {@link Subsystem
	 *         Subsystems}) of this command
	 */
	public synchronized Set<Subsystem> getRequirementsCopy() {
		return Collections.unmodifiableSet(requirementsCopy);
	}

	@Override
	public void initialize() {
		super.initialize();
	}

	@Override
	public void execute() {
		super.execute();
	}

	@Override
	public void end() {
		super.end();
	}

	@Override
	public void interrupted() {
		super.interrupted();
	}

	@Override
	public synchronized void setInterruptible(boolean interruptible) {
		super.setInterruptible(interruptible);
	}

}
