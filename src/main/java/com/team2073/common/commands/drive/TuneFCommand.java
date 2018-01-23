package com.team2073.common.commands.drive;

import com.team2073.common.subsystems.AbstractMotionProfileDriveSubsystem;

import edu.wpi.first.wpilibj.command.Command;

public class TuneFCommand extends Command {
	private final AbstractMotionProfileDriveSubsystem drivetrain;
	private double startingGyro = 0;

	public TuneFCommand(AbstractMotionProfileDriveSubsystem drivetrain) {
		this.drivetrain = drivetrain;
	}

	@Override
	protected void initialize() {
		startingGyro = drivetrain.getGyroAngle();
	}

	@Override
	protected void execute() {
		drivetrain.adjustF(startingGyro);
	}

	@Override
	protected boolean isFinished() {
		return false;
	}
}
