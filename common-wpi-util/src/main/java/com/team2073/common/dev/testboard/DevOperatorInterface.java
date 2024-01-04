package com.team2073.common.dev.testboard;

import com.team2073.common.command.AbstractLoggingCommand;
import com.team2073.common.command.wrapping.WrapUtil;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;

public class DevOperatorInterface {
	
	public static final Joystick controller = new Joystick(0);
	private static final JoystickButton x = new JoystickButton(controller, 1);
	private static final JoystickButton a = new JoystickButton(controller, 2);

	private static AbstractLoggingCommand moveMotorCommand;
	private static AbstractLoggingCommand timeBombCommand;
	
	public static void init() {
		System.out.println("DevOperatorInterface init() start");
//		moveMotorCommand = WrapUtil.wrapAllAndBuild(new DevMoveMotorCommand(RobotMap.motorSubsystem));
//		timeBombCommand = WrapUtil.wrapAllAndBuild(new TimeBombCommand());
		a.onTrue(moveMotorCommand);
		x.onTrue(timeBombCommand);
		System.out.println("DevOperatorInterface init() complete");
	}

}
