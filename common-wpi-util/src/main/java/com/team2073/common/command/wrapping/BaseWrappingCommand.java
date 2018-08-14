package com.team2073.common.command.wrapping;

import com.team2073.common.assertion.Assert;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder;

/**
 * <i>Note: See {@link WrappableCommand} as the root of documentation for
 * command wrapping.</i>
 * <p>
 * A base class for all Command Wrappers to extend. This class simply delegates
 * all applicable command calls to the wrapped command without adding any
 * pre/post functionality.
 * <p>
 * This allows Command Wrapping implementations to only override the methods
 * they care about wrapping and know that the rest of the methods will be
 * properly delegated to the inner command.
 * <p>
 * Subclasses can use the reference to {@link #innerCommand} to access the
 * wrapped command.
 * <p>
 * Note: <b>DO NOT</b> override the following methods. Overriding these will
 * cause command wrapping to no longer function properly:
 * <p>
 * TODO: Try to override these in this class, call super.method() and mark as
 * final.
 * <ul>
 * <li>{@link #requires(Subsystem)}
 * <li>{@link #clearRequirements()}
 * <li>{@link #doesRequire(Subsystem)}
 * <li>{@link #isTimedOut()}
 * <li>{@link #start()}
 * <li>{@link #isRunning()}
 * <li>{@link #cancel()}
 * <li>{@link #isCanceled()}
 * <li>{@link #getGroup()}
 * <li>{@link #setRunWhenDisabled(boolean)}
 * <li>{@link #willRunWhenDisabled()}
 * <li>{@link #toString()}
 * </ul>
 * <p>
 * 
 * @author Preston Briggs
 * @author Gabe Bui
 *
 */
public abstract class BaseWrappingCommand extends WrappableCommand {
	
	/** The inner command to be wrapped. */
	protected final WrappableCommand innerCommand;
	protected final String className;

	/**
	 * Note: <b>Do not modify the command after wrapping!</b>
	 * <p>
	 * All modifications (timeout, subsystem, run when disabled, requires() etc.)
	 * must be 100% complete before wrapping.
	 * <p>
	 * If a timeout exists, it must have been passed in as a constructor argument
	 * of {@link WrappableCommand}.
	 * 
	 * @param commandToWrap The command to be wrapped.
	 */
	public BaseWrappingCommand(WrappableCommand commandToWrap) {
		Assert.assertNotNull(commandToWrap, "commandToWrap");
		innerCommand = commandToWrap;
		setName(innerCommand.getName());
		setSubsystem(innerCommand.getSubsystem());
		setRunWhenDisabled(innerCommand.willRunWhenDisabled());
		innerCommand.getRequirementsCopy().forEach(this::requires);
		if(innerCommand.getTimeoutCopy() != BaseWrappingCommand.TIMEOUT_UNINITIALIZED)
			setTimeout(innerCommand.getTimeoutCopy());

		className = getInnerMostCommand().getClass().getSimpleName();
	}

	// Wrapped methods
	// ================================================================================
	@Override
	public boolean isFinished() {
		return innerCommand.isFinished();
	}

	@Override
	public void initialize() {
		innerCommand.initialize();
	}

	@Override
	public void execute() {
		innerCommand.execute();
	}

	@Override
	public void end() {
		innerCommand.end();
	}

	@Override
	public void interrupted() {
		innerCommand.interrupted();
	}

	@Override
	public synchronized void setInterruptible(boolean interruptible) {
		innerCommand.setInterruptible(interruptible);
	}

	@Override
	public synchronized boolean isInterruptible() {
		return innerCommand.isInterruptible();
	}

	// Helper methods
	// ================================================================================
	protected WrappableCommand getInnerCommand() {
		return innerCommand;
	}

	/**
	 * This method is an alternative to {@link Command#isParented()}. See
	 * {@link WrappableCommand#internalJavaDocs} for more info.
	 * 
	 * @return True if the command has a parent.
	 */
	public synchronized boolean isParentedCopy() {
		return getGroup() != null;
	}

	/**
	 * Cascade down the layers of wrapped commands until you get to the actual command.
	 * @return
	 */
	public WrappableCommand getInnerMostCommand() {
		if (innerCommand instanceof BaseWrappingCommand) {
			BaseWrappingCommand innerWrappingCmd = (BaseWrappingCommand) innerCommand;
			return innerWrappingCmd.getInnerMostCommand();
		}
		
		return innerCommand;
	}

	// Forbidden methods (Don't allow overriding these)
	// ================================================================================
	@Override
	public final void initSendable(SendableBuilder builder) {
		builder.setSmartDashboardType("Command");
		builder.addStringProperty(".name", innerCommand::getName, null);
		builder.addBooleanProperty("running", this::isRunning, (value) -> {
			if (value) {
				if (!isRunning()) {
					start();
				}
			} else {
				if (isRunning()) {
					cancel();
				}
			}
		});
		builder.addBooleanProperty(".isParented", this::isParentedCopy, null);
	}
	
	@Override
	public final synchronized void requires(Subsystem subsystem) {
		super.requires(subsystem);
	}
	
	@Override
	protected final void clearRequirements() {
		super.clearRequirements();
	}
	
	@Override
	public final synchronized boolean doesRequire(Subsystem system) {
		return super.doesRequire(system);
	}
	
	@Override
	protected final synchronized boolean isTimedOut() {
		return super.isTimedOut();
	}
	
	@Override
	public final synchronized void start() {
		super.start();
	}
	
	@Override
	public final synchronized boolean isRunning() {
		return super.isRunning();
	}
	
	@Override
	public final synchronized void cancel() {
		super.cancel();
	}
	
	@Override
	public final synchronized boolean isCanceled() {
		return super.isCanceled();
	}
	
	@Override
	public final synchronized CommandGroup getGroup() {
		return super.getGroup();
	}
	
	@Override
	public final void setRunWhenDisabled(boolean run) {
		super.setRunWhenDisabled(run);
	}
	
	@Override
	public final boolean willRunWhenDisabled() {
		return super.willRunWhenDisabled();
	}
	
	@Override
	public final String toString() {
		return super.toString();
	}
}
