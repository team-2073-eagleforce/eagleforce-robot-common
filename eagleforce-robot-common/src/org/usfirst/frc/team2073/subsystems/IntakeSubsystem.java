package org.usfirst.frc.team2073.subsystems;

import org.usfirst.frc.team2073.interfaces.Intake;
import org.usfirst.frc.team2073.robot.RobotMap;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;

public class IntakeSubsystem extends Subsystem implements Intake {
	TalonSRX intakeMotor = RobotMap.getIntakeMotor();
	
	public IntakeSubsystem(){

	}

	@Override
	public void initDefaultCommand() {
		
	}

	@Override
	public void intake() {
		intakeMotor.set(ControlMode.PercentOutput, .8);
	}

	@Override
	public void outtake() {
		intakeMotor.set(ControlMode.PercentOutput, -.8);
	}

	@Override
	public void stop() {
		intakeMotor.set(ControlMode.PercentOutput, 0);
	}

}
