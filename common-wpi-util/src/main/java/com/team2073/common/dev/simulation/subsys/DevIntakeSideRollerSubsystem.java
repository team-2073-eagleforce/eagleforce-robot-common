package com.team2073.common.dev.simulation.subsys;

import com.team2073.common.objective.StatusChecker;

import edu.wpi.first.wpilibj.command.Subsystem;

public class DevIntakeSideRollerSubsystem extends Subsystem {
	public enum IntakeSideRollerState {
		STOP, INTAKE, OUTTAKE
	}
	
	private IntakeSideRollerState state = IntakeSideRollerState.STOP;
	private StatusChecker status = null;

	@Override
	protected void initDefaultCommand() {
		
	}
	
	@Override
	public void periodic() {
		if (status != null && status.isInterrupted()) {
			resetState();
		}
		
		switch (state) {
			case INTAKE:
				// NO-OP
				break;
			case OUTTAKE:
				// NO-OP
				break;
			case STOP:
			default:
				// NO-OP
				break;
		}
	}
	
	public StatusChecker setState(IntakeSideRollerState newState) {
		if(state != newState) {
			System.out.printf("[%s] State change: [%s] -> [%s].\n", getClass().getSimpleName(), state, newState);
			state = newState;
		}
		return status = new StatusChecker();
	}
	
	public void resetState() {
		System.out.printf("[%s] Resetting state to STOP.\n", getClass().getSimpleName());
		state = IntakeSideRollerState.STOP;
		if (status != null) {
			status.complete();
			status = null;
		}
	}
}
