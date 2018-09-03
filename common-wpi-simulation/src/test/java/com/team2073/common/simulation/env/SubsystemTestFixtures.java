package com.team2073.common.simulation.env;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.IMotorController;
import com.ctre.phoenix.motorcontrol.IMotorControllerEnhanced;
import com.team2073.common.controlloop.PidfControlLoop;
import com.team2073.common.periodic.PeriodicAware;
import com.team2073.common.simulation.model.SimulationCycleComponent;

public class SubsystemTestFixtures {

	public static class ConstantOutputtingSubsystem implements PeriodicAware {
		private IMotorController talon;

		public ConstantOutputtingSubsystem(IMotorController talon) {
			this.talon = talon;
		}

		@Override
		public void onPeriodic() {
			talon.set(ControlMode.PercentOutput, .5);
		}
	}

	public static class BasicCycleComponent implements SimulationCycleComponent {

		int cycles = 0;

		@Override
		public void cycle(SimulationEnvironment env) {
			cycles++;
		}

		public int getCycles() {
			return cycles;
		}
	}

	public static class BasicPeriodicComponent implements PeriodicAware {

		int cycles = 0;


		@Override
		public void onPeriodic() {
			cycles++;
		}

		public int getCycles() {
			return cycles;
		}
	}

	/**
	 * An example elevator subsystem
	 * <p>
	 *     The encoder is on the same shaft of the pulley, and the encoder has 1350 tics per inch of elevator travel.
	 *     <p/>
	 */
	public static class SimulatedElevatorSubsystem implements PeriodicAware {
		private IMotorControllerEnhanced talon;
		private boolean started;
		private double ticsPerInch = 1350;
		private double setpoint;
//		UNITS FOR P are in percentages per inch
		private PidfControlLoop pid = new PidfControlLoop(.023, 0.000001 , .02, 0,10 , 1);

		public SimulatedElevatorSubsystem(IMotorControllerEnhanced talon) {
			this.talon = talon;
		}

		public void set(double setpoint){
			this.setpoint = setpoint;
			pid.stopPID();
			started = false;
		}

		@Override
		public void onPeriodic() {
			if(!started){
				pid.startPID(setpoint);
				started = true;
			}

			pid.setNewPosition(talon.getSelectedSensorPosition(0)/ticsPerInch);
			talon.set(ControlMode.PercentOutput, pid.getOutput());
		}
	}


}
