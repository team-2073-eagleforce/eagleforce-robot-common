package com.team2073.common.dev.simulation;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.team2073.common.dev.objective.DevObjectiveFactory;
import com.team2073.common.dev.simulation.subsys.DevElevatorSubsystem;
import com.team2073.common.dev.simulation.subsys.DevIntakeSideRollerSubsystem;
import com.team2073.common.dev.simulation.subsys.DevShooterPivotSubsystem;
import com.team2073.common.dev.simulation.subsys.DevSubsystemCoordinatorImpl;
import com.team2073.common.robot.AbstractRobotDelegate;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import org.slf4j.LoggerFactory;

public class SimulationRobot extends AbstractRobotDelegate {

	public static DevSubsystemCoordinatorImpl subsysCrd;
	public static DevObjectiveFactory factory;
	
	@Override
	public void robotInit() {
		((Logger) LoggerFactory.getLogger(DevSubsystemCoordinatorImpl.class)).setLevel(Level.DEBUG);
		
		factory = new DevObjectiveFactory(new DevShooterPivotSubsystem(), new DevElevatorSubsystem(), new DevIntakeSideRollerSubsystem());
		subsysCrd = new DevSubsystemCoordinatorImpl();
		SimulationOperatorInterface.init();
	}
	
	@Override
	public void robotPeriodic() {
		CommandScheduler.getInstance().run();
		subsysCrd.onPeriodic();
	}
}
