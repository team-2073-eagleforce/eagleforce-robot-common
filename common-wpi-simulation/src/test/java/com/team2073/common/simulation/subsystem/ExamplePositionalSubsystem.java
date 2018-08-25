package com.team2073.common.simulation.subsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.IMotorController;
import com.ctre.phoenix.motorcontrol.IMotorControllerEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.team2073.common.periodic.PeriodicAware;
import com.team2073.common.simulation.speedcontroller.SimulationEagleSRX;
import edu.wpi.first.wpilibj.command.Subsystem;

public class ExamplePositionalSubsystem implements PeriodicAware{

	private IMotorControllerEnhanced talon;

	public ExamplePositionalSubsystem(IMotorControllerEnhanced talon){
		System.out.println(" IF THIS IS CALLED WE *softly* dont,  BREAK");

		this.talon = talon;


	}

	@Override
	public void onPeriodic() {
		talon.set(ControlMode.PercentOutput, .5);
	}

}
