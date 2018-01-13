package org.usfirst.frc.team2073.interfaces;

import org.usfirst.frc.team2073.RobotMap;

import edu.wpi.first.wpilibj.Solenoid;

public abstract class AbstractTwoSpeedDriveSubsystem extends AbstractEagledriveSubsystem{
	Solenoid solenoid1 = RobotMap.getDriveSolenoid1();
	Solenoid solenoid2 = RobotMap.getDriveSolenoid2();
	
	public void shiftHighGear() {
		solenoid1.set(false);//TODO: rename misleading shiftHighGear/shiftLowGear names
		solenoid2.set(true);
	}

	public void shiftLowGear() {
		solenoid1.set(true);
		solenoid2.set(false);
	}
}
