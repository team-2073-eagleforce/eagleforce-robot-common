package org.usfirst.frc.team2073.robot.commands.drive;

import org.usfirst.frc.team2073.robot.OI;
import org.usfirst.frc.team2073.robot.RobotMap;
import org.usfirst.frc.team2073.robot.subsystems.DrivetrainSubsystem;

import edu.wpi.first.wpilibj.command.Command;

public class DriveWithJoystickCommand extends Command {
	private final DrivetrainSubsystem drivetrain;

	public DriveWithJoystickCommand() {
		drivetrain = RobotMap.getDrivetrain();
		requires(drivetrain);
	}

	@Override
	protected void execute() {
		drivetrain.move(OI.getJoystick().getY(), OI.getWheel().getX());
	}

	@Override
	protected boolean isFinished() {
		return false;
	}
}
