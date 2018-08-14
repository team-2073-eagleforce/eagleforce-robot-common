package com.team2073.common.motionprofiling;

import com.ctre.phoenix.motion.TrajectoryPoint;
import com.team2073.common.concurrency.InterruptibleRunnable;

import java.util.List;

public class TrajectoryPointMultiPusher implements InterruptibleRunnable {
//	private boolean interrupted;
	private final MotionProfileHelper motorHelper;
	private final List<TrajectoryPoint> trajPointList;
//	private final Subsystem subsystem;
//	private final Command command;
	
	public TrajectoryPointMultiPusher(MotionProfileHelper motorHelper, List<TrajectoryPoint> trajPointList/*,  Command command, Subsystem subsystem*/) {
		this.motorHelper = motorHelper;
		this.trajPointList = trajPointList;
//		this.command = command;
//		this.subsystem = subsystem;
	}
	
	@Override
	public void run() {
		motorHelper.pushPoints(trajPointList);
		/* consider using this
		while(!interrupted) {
			if(subsystem.getCurrentCommandName().equals(command.getName()))
				interrupted = false;
			else
				interrupted = true;
		}
		*/
		// TODO: We should be able to interrupt this method, figure it out!
	}

	@Override
	public void interrupt() {
//		interrupted = true;
	}
	
}