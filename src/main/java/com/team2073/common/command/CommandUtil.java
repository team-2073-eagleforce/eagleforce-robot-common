package com.team2073.common.command;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;

public class CommandUtil {
	public static CommandGroup waitBefore(Command command, double timeInSeconds) {
		CommandGroup commandGroup = new CommandGroup();
		commandGroup.addSequential(new WaitCommand(timeInSeconds));
		commandGroup.addSequential(command);
		return commandGroup;
	}
}
