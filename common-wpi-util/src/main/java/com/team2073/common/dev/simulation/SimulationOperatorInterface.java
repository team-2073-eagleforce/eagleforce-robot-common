package com.team2073.common.dev.simulation;

import com.team2073.common.dev.cmd.DevElevatorToHeightCommand;
import com.team2073.common.dev.cmd.DevIntakeSideRollerRandomStateCommand;
import com.team2073.common.dev.cmd.DevShooterToAngleCommand;
import com.team2073.common.dev.objective.DevObjectiveFactory;
import com.team2073.common.dev.simulation.subsys.DevElevatorSubsystem.ElevatorHeight;
import com.team2073.common.dev.simulation.subsys.DevShooterPivotSubsystem.ShooterAngle;
import com.team2073.common.dev.simulation.subsys.DevSubsystemCoordinatorImpl;
import com.team2073.common.trigger.DPadTrigger;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.buttons.Trigger;
import edu.wpi.first.wpilibj.command.Command;

public class SimulationOperatorInterface {
	
	public static final Joystick controller = new Joystick(0);
	private static final JoystickButton a = new JoystickButton(controller, 1);
	private static final JoystickButton b = new JoystickButton(controller, 2);
	private static final JoystickButton x = new JoystickButton(controller, 3);
	private static final JoystickButton y = new JoystickButton(controller, 4);
	private static final Trigger dpadLeft = new DPadTrigger(controller, 270);
	private static final Trigger dpadUp = new DPadTrigger(controller, 0);
	private static final Trigger dpadRight = new DPadTrigger(controller, 90);
	private static final Trigger dpadDown = new DPadTrigger(controller, 180);

	private static DevSubsystemCoordinatorImpl subsysCrd;
	private static DevObjectiveFactory factory;
	
	public static void init() {
		subsysCrd = SimulationRobot.subsysCrd;
		factory = SimulationRobot.factory;
		
////		DevElevatorToPivotCommand elevToPivot = new DevElevatorToPivotCommand(subsysCrd);
//		DevElevatorToZeroCommand elevToZero = new DevElevatorToZeroCommand(subsysCrd, factory);
//		DevElevatorToMaxCommand elevToMax = new DevElevatorToMaxCommand(subsysCrd, factory);
//		DevShooterToFrontStraightCommand shooterToFront = new DevShooterToFrontStraightCommand(subsysCrd, factory);
//		DevShooterToBackStraightCommand shooterToBack = new DevShooterToBackStraightCommand(subsysCrd, factory);
//		
//		a.whenPressed(elevToZero);
//		y.whenPressed(elevToMax);
//		x.whenPressed(shooterToFront);
//		b.whenPressed(shooterToBack);
		
		dpadDown.whenActive(createElevatorCommand(ElevatorHeight.ZERO));
		dpadRight.whenActive(createElevatorCommand(ElevatorHeight.SWITCH));
		dpadLeft.whenActive(createElevatorCommand(ElevatorHeight.PIVOT));
		dpadUp.whenActive(createElevatorCommand(ElevatorHeight.MAX));
		x.whenPressed(createShooterPivotCommand(ShooterAngle.FORWARD_STRAIGHT));
		y.whenPressed(createShooterPivotCommand(ShooterAngle.FORWARD_UP));
		b.whenPressed(createShooterPivotCommand(ShooterAngle.BACKWARD));
		a.whileHeld(new DevIntakeSideRollerRandomStateCommand(subsysCrd, factory));
	}
	
	private static Command createElevatorCommand(ElevatorHeight height) {
		return new DevElevatorToHeightCommand(subsysCrd, factory, height);
	}
	
	private static Command createShooterPivotCommand(ShooterAngle angle) {
		return new DevShooterToAngleCommand(subsysCrd, factory, angle);
	}

}
