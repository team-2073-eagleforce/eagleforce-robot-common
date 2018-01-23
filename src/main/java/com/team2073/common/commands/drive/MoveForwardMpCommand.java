package com.team2073.common.commands.drive;

import com.team2073.common.subsystems.AbstractMotionProfileDriveSubsystem;

import edu.wpi.first.wpilibj.command.Command;

public class MoveForwardMpCommand extends Command {
	private final AbstractMotionProfileDriveSubsystem drivetrain;
	private final double distance;

	public MoveForwardMpCommand(AbstractMotionProfileDriveSubsystem drivetrain, double distance) {
		this.drivetrain = drivetrain;
		this.distance = distance;
		requires(drivetrain);
	}

	@Override
	protected void initialize() {
		drivetrain.autonDriveForward(distance);
	}

	@Override
	protected void execute() {
		drivetrain.processMotionProfiling();
	}

	@Override
	protected boolean isFinished() {
		return drivetrain.isMotionProfilingFinished();
	}

	@Override
	protected void end() {
		drivetrain.stopMotionProfiling();
	}
}
