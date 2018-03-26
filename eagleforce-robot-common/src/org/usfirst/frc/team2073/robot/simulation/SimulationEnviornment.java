package org.usfirst.frc.team2073.robot.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.usfirst.frc.team2073.robot.simulation.models.Mechanism;

public class SimulationEnviornment {
	private List<Mechanism> mechanismList = new ArrayList<>();
	
	public SimulationEnviornment() {
		new Timer().scheduleAtFixedRate(new UpdateMechanisms(), 0, 1);
	}
	
	public void registerMechanism(Mechanism mechanism) {
		mechanismList.add(mechanism);
	}
	
	private class UpdateMechanisms extends TimerTask {
		@Override
		public void run() {
			for (Mechanism mechanism : mechanismList) {
				mechanism.periodic(1);
			}
		}
	}
	
	
	
	
	
	
}
