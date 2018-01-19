
package org.usfirst.frc.team2073.robot;

import org.usfirst.frc.team2073.robot.subsystems.DrivetrainSubsystem;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends TimedRobot {

	Command m_autonomousCommand;
	private SendableChooser<Command> m_chooser = new SendableChooser<>();
	private DrivetrainSubsystem drive;

	@Override
	public void robotInit() {
		RobotMap.init();
		OI.init();
		SmartDashboard.putData("Auto mode", m_chooser);
		drive = RobotMap.getDrivetrain();
	}

	@Override
	public void disabledInit() {

	}

	@Override
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	@Override
	public void autonomousInit() {
		m_autonomousCommand = m_chooser.getSelected();
		if (m_autonomousCommand != null) {
			m_autonomousCommand.start();
		}
	}

	@Override
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
	}

	@Override
	public void teleopInit() {
		DrivetrainSubsystem drive = RobotMap.getDrivetrain();
		drive.stopBrakeMode();
		if (m_autonomousCommand != null) {
			m_autonomousCommand.cancel();
		}
	}

	@Override
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
	}

	@Override
	public void testPeriodic() {
		drive.zeroEncoders();
	}
}
