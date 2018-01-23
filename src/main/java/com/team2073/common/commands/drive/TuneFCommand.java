package org.usfirst.frc.team2073.robot.commands.drive;

import org.usfirst.frc.team2073.robot.RobotMap;
import org.usfirst.frc.team2073.robot.subsystems.DrivetrainSubsystem;

import edu.wpi.first.wpilibj.command.Command;

public class TuneFCommand extends Command {
	private final DrivetrainSubsystem drive;
	private double startingGyro = 0;

	public TuneFCommand() {
		drive = RobotMap.getDrivetrain();
	}

	@Override
	protected void initialize() {
		startingGyro = drive.getGyroAngle();
	}

	@Override
	protected void execute() {
		drive.adjustF(startingGyro);
	}

	@Override
	protected boolean isFinished() {
		return false;
	}
}
