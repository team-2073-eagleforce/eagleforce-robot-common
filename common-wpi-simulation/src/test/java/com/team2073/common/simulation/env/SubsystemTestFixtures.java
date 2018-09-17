package com.team2073.common.simulation.env;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.IMotorController;
import com.ctre.phoenix.motorcontrol.IMotorControllerEnhanced;
import com.team2073.common.controlloop.MotionProfileControlloop;
import com.team2073.common.controlloop.PidfControlLoop;
import com.team2073.common.motionprofiling.SCurveProfileGenerator;
import com.team2073.common.periodic.PeriodicAware;
import com.team2073.common.simulation.model.SimulationCycleComponent;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;

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
	 * The encoder is on the same shaft of the pulley, and the encoder has 1350 tics per inch of elevator travel.
	 * <p/>
	 */
	public static class SimulatedElevatorSubsystem implements PeriodicAware {
		private IMotorControllerEnhanced talon;
		private DigitalInput zeroSensor;
		private Solenoid brake;

		private boolean started;
		private double ticsPerInch = 1350;
		private double setpoint;

		//		UNITS FOR P are in percentages per inch
		private PidfControlLoop pid = new PidfControlLoop(.023, 0, .02, 0, 10, 1);

		public SimulatedElevatorSubsystem(IMotorControllerEnhanced talon, DigitalInput zeroSensor, Solenoid brake) {
			this.talon = talon;
			this.zeroSensor = zeroSensor;
			this.brake = brake;
		}

		public void set(double setpoint) {
			this.setpoint = setpoint;
			pid.stopPID();
			started = false;
		}

		@Override
		public void onPeriodic() {
			if (!started) {
				pid.startPID(setpoint);
				brake.set(false);
				started = true;
			}
			pid.setNewPosition(talon.getSelectedSensorPosition(0) / ticsPerInch);

			if (zeroSensor.get()) {
				brake.set(true);
				talon.set(ControlMode.PercentOutput, 0);
			} else {
				talon.set(ControlMode.PercentOutput, pid.getOutput());
			}

		}
	}

	public static class SimulatedMotionProfileElevatorSubsystem implements PeriodicAware {
		double maxVelocity = 6;
		double maxAcceleration = 20;
		double averageAcceleration = 15;
		SCurveProfileGenerator profile;
		MotionProfileControlloop mpc = new MotionProfileControlloop(.005, 0.0001, .0155, .012, .01, 1);
		private IMotorControllerEnhanced talon;
		private DigitalInput zeroSensor;
		private Solenoid brake;
		private boolean started;
		private double ticsPerInch = 1350;
		private double setpoint;

		public SimulatedMotionProfileElevatorSubsystem(IMotorControllerEnhanced talon, DigitalInput zeroSensor, Solenoid brake) {
			this.talon = talon;
			this.zeroSensor = zeroSensor;
			this.brake = brake;

			mpc.dataPointCallable(() -> profile.nextPoint(.01));

			mpc.updatePosition(() -> talon.getSelectedSensorPosition(0) / ticsPerInch);
		}

		public void set(double setpoint) {
			this.setpoint = setpoint;
			profile = new SCurveProfileGenerator(
					setpoint, maxVelocity, maxAcceleration, averageAcceleration);
			mpc.stop();
			started = false;
		}

		@Override
		public void onPeriodic() {
			if (!started) {
				mpc.start();
				brake.set(false);
				started = true;
			}

			if (zeroSensor.get()) {
				brake.set(true);
				talon.set(ControlMode.PercentOutput, 0);
			} else {
				talon.set(ControlMode.PercentOutput, mpc.getOutput());
			}

		}
	}

	public static class SolenoidSubsystem implements PeriodicAware {

		Solenoid solenoid;

		public SolenoidSubsystem(Solenoid solenoid) {
			this.solenoid = solenoid;
		}

		@Override
		public void onPeriodic() {
			solenoid.set(true);
		}
	}


}
