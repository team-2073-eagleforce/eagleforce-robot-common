package com.team2073.common.simulation.env;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.IMotorController;
import com.team2073.common.periodic.PeriodicRunnable;
import com.team2073.common.simulation.model.SimulationCycleComponent;
import edu.wpi.first.wpilibj.Solenoid;

public class SubsystemTestFixtures {

	public static class ConstantOutputtingSubsystem implements PeriodicRunnable {
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


	public static class BasicPeriodicComponent implements PeriodicRunnable {
		int cycles = 0;


		@Override
		public void onPeriodic() {
			cycles++;
		}

		public int getCycles() {
			return cycles;
		}
	}


	public static class SolenoidSubsystem implements PeriodicRunnable {
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
