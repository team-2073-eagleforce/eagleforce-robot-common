package com.team2073.common.simulation;

import com.team2073.common.simulation.models.Mechanism;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class SimulationEnvironment {
	private List<Mechanism> mechanismList = new ArrayList<>();
	
	public SimulationEnvironment() {
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
