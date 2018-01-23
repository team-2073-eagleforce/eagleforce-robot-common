package com.team2073.common.commands.drive;

import com.team2073.common.subsystems.AbstractEagledriveSubsystem;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.command.Command;

public class DriveWithJoystickCommand extends Command {
	private final AbstractEagledriveSubsystem drivetrain;
	private final Joystick joystick;
	private final Joystick wheel;

	public DriveWithJoystickCommand(AbstractEagledriveSubsystem drivetrain, Joystick joystick, Joystick wheel) {
		this.drivetrain = drivetrain;
		this.joystick = joystick;
		this.wheel = wheel;
		requires(drivetrain);
	}

	@Override
	protected void execute() {
		drivetrain.move(joystick.getY(), wheel.getX());
	}

	@Override
	protected boolean isFinished() {
		return false;
	}
}
