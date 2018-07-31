package com.team2073.common.dev.testboard;

import com.team2073.common.command.wrapping.WrapUtil;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.command.Command;

public class DevOperatorInterface {
	
	public static final Joystick controller = new Joystick(0);
	private static final JoystickButton x = new JoystickButton(controller, 1);
	private static final JoystickButton a = new JoystickButton(controller, 2);

	private static Command moveMotorCommand;
	private static Command timeBombCommand;
	
	public static void init() {
		System.out.println("DevOperatorInterface init() start");
		moveMotorCommand = WrapUtil.wrapAllAndBuild(new DevMoveMotorCommand(RobotMap.motorSubsystem));
		timeBombCommand = WrapUtil.wrapAllAndBuild(new TimeBombCommand());
		a.whenPressed(moveMotorCommand);
		x.whenPressed(timeBombCommand);
		System.out.println("DevOperatorInterface init() complete");
	}

}
