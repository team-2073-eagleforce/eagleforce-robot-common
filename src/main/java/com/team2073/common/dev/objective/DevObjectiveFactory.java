package com.team2073.common.dev.objective;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import com.google.inject.Inject;
import com.team2073.common.dev.simulation.subsys.DevElevatorSubsystem;
import com.team2073.common.dev.simulation.subsys.DevElevatorSubsystem.ElevatorHeight;
import com.team2073.common.dev.simulation.subsys.DevIntakeSideRollerSubsystem;
import com.team2073.common.dev.simulation.subsys.DevIntakeSideRollerSubsystem.IntakeSideRollerState;
import com.team2073.common.dev.simulation.subsys.DevShooterPivotSubsystem;
import com.team2073.common.dev.simulation.subsys.DevShooterPivotSubsystem.ShooterAngle;
import com.team2073.common.objective.AbstractObjective;
import com.team2073.common.objective.Precondition;

public class DevObjectiveFactory {

	// Subsystems
	@Inject private DevShooterPivotSubsystem shooter;
	@Inject private DevElevatorSubsystem elevator;
	@Inject private DevIntakeSideRollerSubsystem intakeSides;
	
	// Objectives
//	private DevElevatorObjective elevatorToMax;
//	private DevElevatorObjective elevatorToPivot;
//	private DevElevatorObjective elevatorToSwitch;
//	private DevElevatorObjective elevatorToZero;
//	
//	private DevShooterObjective shooterToFrontStraight;
//	private DevShooterObjective shooterToFrontUp;
//	private DevShooterObjective shooterToBackUp;
//	private DevShooterObjective shooterToBackStraight;
	
	private final Map<ElevatorHeight, AbstractObjective> elevatorObjectives = new HashMap<>();
	private final Map<ShooterAngle, AbstractObjective> shooterObjectives = new HashMap<>();
	private final Map<IntakeSideRollerState, AbstractObjective> intakeSideRollerObjectives = new HashMap<>();
	
	// Preconditions
	private final Precondition elevatorCanMoveBelowBar = Precondition.named("elevatorCanMoveBelowBar", 
			() -> !shooter.isPivotBack());
//	private final ObjectivePrecondition canPivotForwards = ObjectivePrecondition.named("canPivotForwards",
//			() -> !(shooter.isPivotBack() && !elevator.isAtOrAboveHeight(ElevatorHeight.PIVOT)));
	private final Precondition canPivotForwards = Precondition.named("canPivotForwards",
			() -> !shooter.isPivotBack() || elevator.isAtHeight(ElevatorHeight.PIVOT));
	private final Precondition canPivotBackwards = Precondition.named("canPivotBackwards",
			() -> shooter.isPivotBack() || elevator.isAtHeight(ElevatorHeight.PIVOT));
	
	
			
	public DevObjectiveFactory() {
	}

	/** Non Dependency-injection constructor */
	public DevObjectiveFactory(DevShooterPivotSubsystem shooter, DevElevatorSubsystem elevator, DevIntakeSideRollerSubsystem intakeSides) {
		this.shooter = shooter;
		this.elevator = elevator;
		this.intakeSides = intakeSides;
		init();
	}

	@PostConstruct
	public void init() {
		// Create Objectives
//		elevatorToMax = new DevElevatorObjective(elevator, ElevatorHeight.MAX);
//		elevatorToPivot = new DevElevatorObjective(elevator, ElevatorHeight.PIVOT);
//		elevatorToSwitch = new DevElevatorObjective(elevator, ElevatorHeight.SWITCH);
//		elevatorToZero = new DevElevatorObjective(elevator, ElevatorHeight.ZERO);
//		
//		shooterToFrontStraight = new DevShooterObjective(shooter, ShooterAngle.FORWARD_STRAIGHT);
//		shooterToFrontUp = new DevShooterObjective(shooter, ShooterAngle.FORWARD_UP);
////		shooterToBackUp = new DevShooterObjective(shooter, ShooterAngle.BACKWARD_STRAIGHT);
//		shooterToBackStraight = new DevShooterObjective(shooter, ShooterAngle.BACKWARD);
		
		for (ElevatorHeight height : ElevatorHeight.values())
			elevatorObjectives.put(height, new DevElevatorObjective(elevator, height));
		for (ShooterAngle angle : ShooterAngle.values())
			shooterObjectives.put(angle, new DevShooterObjective(shooter, angle));
		for (IntakeSideRollerState state : IntakeSideRollerState.values())
			intakeSideRollerObjectives.put(state, new DevIntakeSideRollerObjective(intakeSides, state));
		
		// Add preconditions
		// Don't add PreconditionMappings until all objectives have been set above (or the mapping could be null)
////		elevatorToMax.add(elevatorCanMoveBelowBar, shooterToFrontUp);
//		elevatorToSwitch.add(elevatorCanMoveBelowBar, shooterToFrontUp);
//		elevatorToZero.add(elevatorCanMoveBelowBar, shooterToFrontUp);
		
		Precondition elevatorCanMoveBelowBar = Precondition.named("elevatorCanMoveBelowBar",  () -> !shooter.isPivotBack());
		getElevatorTo(ElevatorHeight.SWITCH).addPrecondition(elevatorCanMoveBelowBar, getShooterTo(ShooterAngle.FORWARD_UP));
		getElevatorTo(ElevatorHeight.ZERO).addPrecondition(elevatorCanMoveBelowBar, getShooterTo(ShooterAngle.FORWARD_UP));

		// TODO: Create a new Objective that goes to ANY front (covers a range from FORWARD_STRAIGHT.lowerbound to FORWARD_UP.upperbound)
		// This way if it was already at FORWARD_STRAIGHT it wouldn't make it move to FORWARD_UP
//		shooterToFrontStraight.add(canPivotForwards, elevatorToPivot);
//		shooterToFrontUp.addPrecondition(canPivotForwards, elevatorToPivot);
//		shooterToBackStraight.add(canPivotBackwards, elevatorToPivot);
////		shooterToBackUp.add(canPivotBackwards, elevatorToPivot);
		
		Precondition canPivotForwards = Precondition.named("canPivotForwards", () -> !shooter.isPivotBack() || elevator.isAtHeight(ElevatorHeight.PIVOT));
		Precondition canPivotBackwards = Precondition.named("canPivotBackwards", () -> shooter.isPivotBack() || elevator.isAtHeight(ElevatorHeight.PIVOT));
		getShooterTo(ShooterAngle.FORWARD_STRAIGHT).addPrecondition(canPivotForwards, getElevatorTo(ElevatorHeight.PIVOT));
		getShooterTo(ShooterAngle.FORWARD_UP).addPrecondition(canPivotForwards, getElevatorTo(ElevatorHeight.PIVOT));
		getShooterTo(ShooterAngle.BACKWARD).addPrecondition(canPivotBackwards, getElevatorTo(ElevatorHeight.PIVOT));
	}


//	public DevElevatorObjective getElevatorToMax() {
//		return elevatorToMax;
//	}
//
//	public DevElevatorObjective getElevatorToPivot() {
//		return elevatorToPivot;
//	}
//
//	public DevElevatorObjective getElevatorToSwitch() {
//		return elevatorToSwitch;
//	}
//
//	public DevElevatorObjective getElevatorToZero() {
//		return elevatorToZero;
//	}
//
//	public DevShooterObjective getShooterToFrontStraight() {
//		return shooterToFrontStraight;
//	}
//
//	public DevShooterObjective getShooterToFrontUp() {
//		return shooterToFrontUp;
//	}
//
////	public DevShooterObjective getShooterToBackUp() {
////		return shooterToBackUp;
////	}
//
//	public DevShooterObjective getShooterToBackStraight() {
//		return shooterToBackStraight;
//	}
	
	public AbstractObjective getElevatorTo(ElevatorHeight height) {
		return elevatorObjectives.get(height);
	}
	
	public AbstractObjective getShooterTo(ShooterAngle angle) {
		return shooterObjectives.get(angle);
	}
	
	public AbstractObjective getIntakeSideRoller(IntakeSideRollerState state) {
		return intakeSideRollerObjectives.get(state);
	}
}
