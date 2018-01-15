/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team2073;


import org.usfirst.frc.team2073.commands.drive.TuneFCommand;
import org.usfirst.frc.team2073.conf.AppConstants.Controllers.DriveWheel;
import org.usfirst.frc.team2073.conf.AppConstants.Controllers.PowerStick;
import org.usfirst.frc.team2073.conf.AppConstants.Controllers.Xbox;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.command.Command;


public class OI {
	private static Joystick controller = new Joystick(Xbox.PORT);
	private static Joystick joystick = new Joystick(PowerStick.PORT);
	private static Joystick wheel = new Joystick(DriveWheel.PORT);
	
	static void init() {
		JoystickButton leftBumper = new JoystickButton(controller, Xbox.ButtonPorts.L1);
	
		Command tuneFcommand = new TuneFCommand();
		
		leftBumper.toggleWhenPressed(tuneFcommand);
	}
	
	
	public static Joystick getController() {
		return controller;
	}

	public static Joystick getWheel() {
		return wheel;
	}

	public static Joystick getJoystick() {
		return joystick;
	}
}
