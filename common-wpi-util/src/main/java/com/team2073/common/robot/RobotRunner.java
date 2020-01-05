package com.team2073.common.robot;

import com.team2073.common.CommonConstants;
import com.team2073.common.config.CommonProperties;
import com.team2073.common.ctx.RobotContext;
import com.team2073.common.datarecorder.DataRecorder;
import com.team2073.common.event.RobotEventPublisher;
import com.team2073.common.event.RobotEventPublisher.RobotStateEvent;
import com.team2073.common.periodic.PeriodicRunner;
import com.team2073.common.proploader.PropertyLoader;
import com.team2073.common.robot.adapter.DriverStationAdapter;
import com.team2073.common.robot.adapter.SchedulerAdapter;
import com.team2073.common.robot.module.DiagnosticLoggingModule;
import com.team2073.common.robot.module.LoggingLevelModule;
import com.team2073.common.util.ExceptionUtil;
import edu.wpi.first.wpilibj.RobotController;

import java.text.DecimalFormat;

/**
 * <h1>WRANING: These javadocs are no longer valid ~1/18/2019 (COMMON-103)</h1>
 * Extend this class and simply pass in an implementation of
 * {@link RobotDelegate} to the super constructor (use
 * {@link AbstractRobotDelegate} for simplicity). Then just define the new
 * subclass as the Robot class in your project (in build.gradle for gradleRIO or
 * in build.xml for wpilib ant projects).
 * <p>
 * For example, define a RobotDelegate and override any methods required:
 * <pre>
 * public class RobotDelegatePowerUpImpl extends AbstractRobotDelegate {
 * 	{@literal @}Override
 * 	public void robotInit() {
 * 		// do something
 * 	}
 * }
 * </pre>
 * And then extend this class and pass in your RobotDelegate implementation:
 * <pre>
 * public class RobotDelegatorPowerUpImpl extends RobotRunner {
 *
 * 	public RobotDelegatorPowerUpImpl() {
 * 		super(new RobotDelegatePowerUpImpl());
 * 	}
 *
 * }
 * </pre>
 * <p>
 * This class will handle calling your {@link RobotDelegate} implementation and
 * catch any exceptions thrown besides those thrown in robotInit, those are
 * handled by rebooting.
 *
 * @author Preston Briggs
 */
public class RobotRunner implements RobotDelegate {
	
	private final RobotDelegate robot;

	private DecimalFormat formatter = new DecimalFormat("#.##");
	private boolean loggedFmsMatchData = false;

	private RobotContext robotContext;
	
	// Modules
	private LoggingLevelModule loggingLevelModule;
	private DiagnosticLoggingModule diagnosticLoggingModule;

	public RobotRunner(RobotDelegate robot) {
		this.robot = robot;
	}

	private enum MatchPeriod {
		SETUP,
		AUTONOMOUS,
		TELEOP;
	}
	
	private MatchPeriod currentPeriod = MatchPeriod.SETUP;
	
	@Override
	public void robotInit() {
		// Don't wrap in exception handling, handled by rebooting
		robotContext = RobotContext.getInstance();
		initializeDelegator();
		robotContext.registerPeriodicInstances();
		robot.robotInit();
		// Allow subclasses to register their own periodic instances
		robot.registerPeriodicInstance(robotContext.getPeriodicRunner());
		resetLastCheckedTime();
	}
	
	private void initializeDelegator() {

		CommonProperties commonProps;
		PeriodicRunner periodicRunner;
		DataRecorder dataRecorder;
		RobotEventPublisher eventPublisher;
		PropertyLoader propertyLoader;
		DriverStationAdapter driverStation;
		SchedulerAdapter scheduler;


		if ((commonProps = robot.createCommonProperties()) != null)
			robotContext.setCommonProps(commonProps);
		
		if ((periodicRunner = robot.createPeriodicRunner()) != null)
			robotContext.setPeriodicRunner(periodicRunner);

		if ((eventPublisher = robot.createEventPublisher()) != null)
			robotContext.setEventPublisher(eventPublisher);

		if ((dataRecorder = robot.createDataRecorder()) != null)
			robotContext.setDataRecorder(dataRecorder);
		
		if ((propertyLoader = robot.createPropertyLoader()) != null)
			robotContext.setPropertyLoader(propertyLoader);
		
		if ((driverStation = robot.createDriverStationAdapter()) != null)
			robotContext.setDriverStation(driverStation);
		
		if ((scheduler = robot.createSchedulerAdapter()) != null)
			robotContext.setScheduler(scheduler);

	}

	@Override
	public void disabledInit() {
		resetLastCheckedTime();
		ExceptionUtil.suppressVoid(robot::disabledInit, "robot::disabledInit");
		robotContext.getEventPublisher().setCurrentEvent(RobotStateEvent.DISABLED);
	}

	@Override
	public void autonomousInit() {
		currentPeriod = MatchPeriod.AUTONOMOUS;
		ExceptionUtil.suppressVoid(robot::autonomousInit, "robot::autonomousInit");
		robotContext.getEventPublisher().setCurrentEvent(RobotStateEvent.AUTONOMOUS_START);
	}

	@Override
	public void teleopInit() {
		currentPeriod = MatchPeriod.TELEOP;
		ExceptionUtil.suppressVoid(robot::teleopInit, "robot::teleopInit");
		robotContext.getEventPublisher().setCurrentEvent(RobotStateEvent.TELEOP_START);
	}

	@Override
	public void testInit() {
		ExceptionUtil.suppressVoid(robot::testInit, "robot::testInit");
		robotContext.getEventPublisher().setCurrentEvent(RobotStateEvent.TEST_START);
	}

	@Override
	public void robotPeriodic() {
		robotContext.getEventPublisher().setCurrentEvent(RobotStateEvent.PERIODIC);
		// scheduler was at the end but I feel like it needs to be before we run all subsystems
		robotContext.getScheduler().run();
		ExceptionUtil.suppressVoid(robot::robotPeriodic, "robot::robotPeriodic");
		robotContext.getPeriodicRunner().invokePeriodicInstances();
	}

	@Override
	public void disabledPeriodic() {
		ExceptionUtil.suppressVoid(robot::disabledPeriodic, "robot::disabledPeriodic");
		robotContext.getEventPublisher().setCurrentEvent(RobotStateEvent.PERIODIC);
	}

	@Override
	public void autonomousPeriodic() {
		ExceptionUtil.suppressVoid(robot::autonomousPeriodic, "robot::autonomousPeriodic");
		robotContext.getEventPublisher().setCurrentEvent(RobotStateEvent.PERIODIC);
	}

	@Override
	public void teleopPeriodic() {
		ExceptionUtil.suppressVoid(robot::teleopPeriodic, "robot::teleopPeriodic");
		robotContext.getEventPublisher().setCurrentEvent(RobotStateEvent.PERIODIC);
	}

	@Override
	public void testPeriodic() {
		ExceptionUtil.suppressVoid(robot::testPeriodic, "robot::testPeriodic");
		robotContext.getEventPublisher().setCurrentEvent(RobotStateEvent.PERIODIC);
	}

	private enum DsStatusMessage {
		CONNECTED,
		DISCONNECTED;
	}
	
	private DsStatusMessage previousDSMessage = DsStatusMessage.DISCONNECTED;
	
	public void logDsStatus() {
		if(robotContext.getDriverStation().isDSAttached()) {
			if(previousDSMessage != DsStatusMessage.CONNECTED)
				previousDSMessage = DsStatusMessage.CONNECTED;
		} else {
			if (previousDSMessage != DsStatusMessage.DISCONNECTED) {
				previousDSMessage = DsStatusMessage.DISCONNECTED;
			}
		}
	}
	
	private boolean isRealMatch() {
		return robotContext.getDriverStation().isFMSAttached();
	}
		
	private double lastCheckedTime = -1.0;
	
	private void resetLastCheckedTime() {
		lastCheckedTime = -1.0;
	}
	
	private enum VoltageStatusMessage {
		SAFE_VOLTAGE,
		ENTER_BROWNED_OUT,
		EXIT_BROWNED_OUT,
		UNSAFE_VOLTAGE;
	}
	
	private VoltageStatusMessage previousVoltageStatus = VoltageStatusMessage.SAFE_VOLTAGE;
	
	public void logUnsafeVoltages() {
		
		// TODO: 1/16/19 - Create RobotControllerAdapter and remove this if check
		
		if (RobotContext.instanceIsSimulationMode())
			return;
		
		if(RobotController.isBrownedOut()) {
			if(previousVoltageStatus != VoltageStatusMessage.ENTER_BROWNED_OUT) {
				previousVoltageStatus = VoltageStatusMessage.ENTER_BROWNED_OUT;
			}
		} else if(!RobotController.isBrownedOut()) {
			if(previousVoltageStatus == VoltageStatusMessage.ENTER_BROWNED_OUT) {
				previousVoltageStatus = VoltageStatusMessage.EXIT_BROWNED_OUT;
			}else if(RobotController.getBatteryVoltage() < CommonConstants.Diagnostics.UNSAFE_BATTERY_VOLTAGE) {
				if(previousVoltageStatus != VoltageStatusMessage.UNSAFE_VOLTAGE) {
					previousVoltageStatus = VoltageStatusMessage.UNSAFE_VOLTAGE;
				}
			} else if(RobotController.getBatteryVoltage() > CommonConstants.Diagnostics.UNSAFE_BATTERY_VOLTAGE) {
				if(previousVoltageStatus != VoltageStatusMessage.SAFE_VOLTAGE) {
					previousVoltageStatus = VoltageStatusMessage.SAFE_VOLTAGE;
				}
			}
		}else {
			previousVoltageStatus = VoltageStatusMessage.SAFE_VOLTAGE;
		}
	}


	@Override
	public double getPeriod() {
		return robot.getPeriod();
	}

}
