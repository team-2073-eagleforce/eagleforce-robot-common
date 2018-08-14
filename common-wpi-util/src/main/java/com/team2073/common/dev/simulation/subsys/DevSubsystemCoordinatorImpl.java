package com.team2073.common.dev.simulation.subsys;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.google.inject.Inject;
import com.team2073.common.objective.AbstractSubsystemCoordinator;

public class DevSubsystemCoordinatorImpl extends AbstractSubsystemCoordinator {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
//	@Inject private DevObjectiveFactory objFactory;
	
	// Constructors
	// ============================================================
	public DevSubsystemCoordinatorImpl() {
	}

//	/** Non Dependency-injection constructor */
//	public DevSubsystemCoordinatorImpl(DevElevatorSubsystem elevator, DevShooterSubsystem shooter) {
//		this.objFactory = new DevObjectiveFactory(shooter, elevator);
//	}
	
//	// Public methods
//	// ============================================================
//	public ObjectiveRequest elevatorToZero() {
//		return queue(objFactory.getElevatorToZero());
//	}
//
//	public ObjectiveRequest elevatorToSwitch() {
//		return queue(objFactory.getElevatorToSwitch());
//	}
//
//	public ObjectiveRequest elevatorToPivot() {
//		return queue(objFactory.getElevatorToPivot());
//	}
//
//	public ObjectiveRequest elevatorToMax() {
//		return queue(objFactory.getElevatorToMax());
//	}
//
//	public ObjectiveRequest shooterToFrontStraight() {
//		return queue(objFactory.getShooterToFrontStraight());
//	}
//
//	public ObjectiveRequest shooterToFrontUp() {
//		return queue(objFactory.getShooterToFrontUp());
//	}
//
//	public ObjectiveRequest shooterToBackStraight() {
//		return queue(objFactory.getShooterToBackStraight());
//	}
}
