package com.team2073.common.commands.drive;

import com.team2073.common.subsystems.AbstractMotionProfileDriveSubsystem;

import edu.wpi.first.wpilibj.command.Command;

public class PointTurnMpCommand extends Command {
	private final AbstractMotionProfileDriveSubsystem drivetrain;
	private final double angle;

	public PointTurnMpCommand(AbstractMotionProfileDriveSubsystem drivetrain, double angle) {
		this.drivetrain = drivetrain;
		this.angle = angle;
		requires(drivetrain);
	}

	@Override
	protected void initialize() {
		drivetrain.autonPointTurn(angle);
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
