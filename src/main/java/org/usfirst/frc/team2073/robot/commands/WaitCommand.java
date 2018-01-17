package org.usfirst.frc.team2073.robot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 *
 */
public class WaitCommand extends InstantCommand {
	private double delayInSeconds;
    public WaitCommand(double delayInSeconds) {
    	this.delayInSeconds = delayInSeconds;
    }

    @Override
    protected void initialize() {
    	Timer.delay(delayInSeconds);
    }

}
