package com.team2073.common.dev.testboard;

import com.team2073.common.command.wrapping.WrappableCommand;

/**
 * A simple command that just runs a motor at 25% speed. Used for dev/testing purposes.
 * @author Preston Briggs
 *
 */
public class DevMoveMotorCommand extends WrappableCommand {
	
	private DevMotorSubsystem motorSubsystem;

	public DevMoveMotorCommand(DevMotorSubsystem motorSubsystem) {
		System.out.println("Constructing DevMoveMotorCommand using DevMotorSubsystem: " + motorSubsystem);
		this.motorSubsystem = motorSubsystem;
		requires(motorSubsystem);
	}
	
	@Override
	public void initialize() {
		System.out.println("DevMoveMotorCommand initializing...");
	}
	
	@Override
	public void execute() {
		System.out.println("DevMoveMotorCommand executing...");
		motorSubsystem.move(.25);
	}

	@Override
	public void end() {
		System.out.println("DevMoveMotorCommand ending...");
		motorSubsystem.stop();
		System.out.println("DevMoveMotorCommand ended");
	}

	@Override
	public boolean isFinished() {
		return false;
	}

}
