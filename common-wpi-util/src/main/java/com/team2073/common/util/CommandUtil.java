package com.team2073.common.util;

//import edu.wpi.first.wpilibj.command.Command;
//import edu.wpi.first.wpilibj.command.CommandGroup;
//import edu.wpi.first.wpilibj.command.WaitCommand;

public class CommandUtil {
//
//	public static CommandGroup waitBefore(Command command, double timeInSeconds) {
//		CommandGroup commandGroup = new CommandGroup();
//		commandGroup.addSequential(new WaitCommand(timeInSeconds));
//		commandGroup.addSequential(command);
//		return commandGroup;
//	}
//
//	public static CommandGroup sequentialNonInteruptible(Command... commands) {
//		CommandGroup commandGroup = new CommandGroup() {
//			{
//				setInterruptible(false);
//			}
//		};
//		for (Command command : commands) {
//			commandGroup.addSequential(command);
//		}
//		return commandGroup;
//	}
//
//	public static CommandGroup parallelDelayParallel(Command first, Command second, double delayInSeconds) {
//		CommandGroup commandGroup = new CommandGroup();
//
//		commandGroup.addSequential(first, delayInSeconds);
//		commandGroup.addParallel(second);
//		return commandGroup;
//	}
}
