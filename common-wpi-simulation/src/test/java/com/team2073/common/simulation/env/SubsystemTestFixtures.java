package com.team2073.common.simulation.env;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.IMotorController;
import com.team2073.common.periodic.PeriodicAware;

public class SubsystemTestFixtures {

	static class ConstantOutputtingSubsystem implements PeriodicAware {
		private IMotorController talon;

		public ConstantOutputtingSubsystem(IMotorController talon) {
			this.talon = talon;
		}

		@Override
		public void onPeriodic() {
			talon.set(ControlMode.PercentOutput, .5);
		}
	}
}
