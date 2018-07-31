package com.team2073.common.dev.testboard;

import com.team2073.common.command.wrapping.WrappableCommand;

import edu.wpi.first.wpilibj.Timer;

/**
 * A command that throws an exception after execute() is called 100 times (or
 * whatever the value of {@link #death} is set to. Used to test exception
 * handling.
 * 
 * @author Preston Briggs
 *
 */
public class TimeBombCommand extends WrappableCommand {
	
	private int counter = 0;
	private int death = 100;
	
	public TimeBombCommand() {
		requires(RobotMap.motorSubsystem);
	}

	@Override
	public void initialize() {
		counter = 0;
		super.initialize();
	}

	@Override
	public void execute() {
		System.out.println("Blowing up in: " + (death - counter));
		counter++;
		if(counter > death) {
			System.err.println("you die... in 1 second!");
			Timer.delay(1);
			throw new RuntimeException("you die now!");
		}
		super.execute();
	}

	@Override
	public boolean isFinished() {
		return false;
	}

	
}
