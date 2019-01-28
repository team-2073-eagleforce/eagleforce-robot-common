package com.team2073.common.robot;

import com.team2073.common.CommonConstants;
import com.team2073.common.ctx.RobotContext;
import com.team2073.common.datarecorder.DataRecorder;
import com.team2073.common.event.RobotEventPublisher;
import com.team2073.common.event.RobotEventPublisher.RobotStateEvent;
import com.team2073.common.periodic.OccasionalLoggingRunner;
import com.team2073.common.periodic.PeriodicRunner;
import com.team2073.common.periodic.SmartDashboardAware;
import com.team2073.common.periodic.SmartDashboardAwareRunner;
import com.team2073.common.proploader.PropertyLoader;
import com.team2073.common.robot.adapter.DriverStationAdapter;
import com.team2073.common.robot.adapter.SchedulerAdapter;
import com.team2073.common.util.ExceptionUtil;
import com.team2073.common.util.LogUtil;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class RobotRunner implements RobotDelegate, SmartDashboardAware {
	
	private final RobotDelegate robot;
	private Logger log = LoggerFactory.getLogger(getClass());

	private DecimalFormat formatter = new DecimalFormat("#.##");
	private boolean loggedFmsMatchData = false;

	private RobotContext robotContext;

	// Fields from RobotContext
	private PeriodicRunner periodicRunner;
	private OccasionalLoggingRunner loggingRunner;
	private DataRecorder dataRecorder;
	private RobotEventPublisher eventPublisher;
	private SmartDashboardAwareRunner smartDashboardRunner;
	private PropertyLoader propertyLoader;
	private DriverStationAdapter driverStation;
	private SchedulerAdapter scheduler;

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
		LogUtil.infoInit(getClass(), log);
		robotContext = RobotContext.getInstance();
		initializeDelegator();
		robotContext.registerPeriodicInstances();
		robot.robotInit();
		// Allow subclasses to register their own periodic instances
		robot.registerPeriodicInstance(robotContext.getPeriodicRunner());
		resetLastCheckedTime();
		LogUtil.infoInitEnd(getClass(), log);
	}

	private void initializeDelegator() {
		log.info("Initializing Robot Delegator Context...");

		if ((periodicRunner = robot.createPeriodicRunner()) != null)
			robotContext.setPeriodicRunner(periodicRunner);

		if ((eventPublisher = robot.createEventPublisher()) != null)
			robotContext.setEventPublisher(eventPublisher);

		if ((smartDashboardRunner = robot.createSmartDashboardRunner()) != null)
			robotContext.setSmartDashboardRunner(smartDashboardRunner);

		if ((loggingRunner = robot.createLoggingRunner()) != null)
			robotContext.setLoggingRunner(loggingRunner);
		
		if ((dataRecorder = robot.createDataRecorder()) != null)
			robotContext.setDataRecorder(dataRecorder);
		
		if ((propertyLoader = robot.createPropertyLoader()) != null)
			robotContext.setPropertyLoader(propertyLoader);

		refreshFromAppContext();
		log.info("Initializing Robot Delegator complete.");
	}

	@Override
	public void disabledInit() {
		log.info("Robot disabled.");
		resetLastCheckedTime();
		ExceptionUtil.suppressVoid(robot::disabledInit, "robot::disabledInit");
		eventPublisher.setCurrentEvent(RobotStateEvent.DISABLED);
	}

	@Override
	public void autonomousInit() {
		log.info("Autonomous enabled.");
		currentPeriod = MatchPeriod.AUTONOMOUS;
		ExceptionUtil.suppressVoid(robot::autonomousInit, "robot::autonomousInit");
		eventPublisher.setCurrentEvent(RobotStateEvent.AUTONOMOUS_START);
	}

	@Override
	public void teleopInit() {
		log.info("Teleop enabled.");
		currentPeriod = MatchPeriod.TELEOP;
		ExceptionUtil.suppressVoid(robot::teleopInit, "robot::teleopInit");
		eventPublisher.setCurrentEvent(RobotStateEvent.TELEOP_START);
	}

	@Override
	public void testInit() {
		log.info("Test mode enabled.");
		ExceptionUtil.suppressVoid(robot::testInit, "robot::testInit");
		eventPublisher.setCurrentEvent(RobotStateEvent.TEST_START);
	}

	@Override
	public void robotPeriodic() {
		refreshFromAppContext();
		logAllChecks();
		logStartingConfig();
		eventPublisher.setCurrentEvent(RobotStateEvent.PERIODIC);
		// scheduler was at the end but I feel like it needs to be before we run all subsystems
		scheduler.run();
		ExceptionUtil.suppressVoid(robot::robotPeriodic, "robot::robotPeriodic");
		periodicRunner.invokePeriodicInstances();
	}

	@Override
	public void disabledPeriodic() {
		ExceptionUtil.suppressVoid(robot::disabledPeriodic, "robot::disabledPeriodic");
		eventPublisher.setCurrentEvent(RobotStateEvent.PERIODIC);
	}

	@Override
	public void autonomousPeriodic() {
		ExceptionUtil.suppressVoid(robot::autonomousPeriodic, "robot::autonomousPeriodic");
		eventPublisher.setCurrentEvent(RobotStateEvent.PERIODIC);
	}

	@Override
	public void teleopPeriodic() {
		ExceptionUtil.suppressVoid(robot::teleopPeriodic, "robot::teleopPeriodic");
		eventPublisher.setCurrentEvent(RobotStateEvent.PERIODIC);
	}

	@Override
	public void testPeriodic() {
		ExceptionUtil.suppressVoid(robot::testPeriodic, "robot::testPeriodic");
		eventPublisher.setCurrentEvent(RobotStateEvent.PERIODIC);
	}

	private void refreshFromAppContext() {
		periodicRunner = robotContext.getPeriodicRunner();
		loggingRunner = robotContext.getLoggingRunner();
		dataRecorder = robotContext.getDataRecorder();
		eventPublisher = robotContext.getEventPublisher();
		smartDashboardRunner = robotContext.getSmartDashboardRunner();
		driverStation = robotContext.getDriverStation();
		scheduler = robotContext.getScheduler();
	}
	
	private enum DsStatusMessage {
		CONNECTED,
		DISCONNECTED;
	}
	
	private DsStatusMessage previousDSMessage = DsStatusMessage.DISCONNECTED;
	
	public void logDsStatus() {
		if(driverStation.isDSAttached()) {
			if(previousDSMessage != DsStatusMessage.CONNECTED)
				log.info("Driver Station Connected");
				previousDSMessage = DsStatusMessage.CONNECTED;
		}else {
			if(previousDSMessage != DsStatusMessage.DISCONNECTED) {
				log.info("Driver Station Disconnected");
				previousDSMessage = DsStatusMessage.DISCONNECTED;
			}
		}
	}
	
	private boolean isRealMatch() {
		return driverStation.isFMSAttached();
	}
		
	public void logStartingConfig() {		
		if(!loggedFmsMatchData && isRealMatch()) {
			loggedFmsMatchData = true;
			log.info("Alliance: [{}], DriverStation: [{}], Match type: [{}], Match Number: [{}]"
					, driverStation.getAlliance().toString()
					, driverStation.getLocation()
					, driverStation.getMatchType()
					, Integer.toString(driverStation.getMatchNumber()));
		}
	}

	public void logAllChecks() {
		if(isRealMatch()) {
			logDsStatus();
			logRemainingMatchTime();
			logUnsafeVoltages();
		}
	}
	
	private double lastCheckedTime = -1.0;
	
	private void resetLastCheckedTime() {
		lastCheckedTime = -1.0;
	}
	
	public void logRemainingMatchTime() {	
		if(driverStation.getMatchTime() != -1.0) {
			if(lastCheckedTime == -1.0 || lastCheckedTime-10 > driverStation.getMatchTime()){
				log.info("[{}] seconds left in [{}]"
				, formatter.format(driverStation.getMatchTime())
				, currentPeriod.toString());
				
				lastCheckedTime = Math.abs(driverStation.getMatchTime());
			}
		}
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
				log.info("Brown out detected. Voltage: [{}]", formatter.format(RobotController.getBatteryVoltage()));
				previousVoltageStatus = VoltageStatusMessage.ENTER_BROWNED_OUT;
			}
		} else if(!RobotController.isBrownedOut()) {
			if(previousVoltageStatus == VoltageStatusMessage.ENTER_BROWNED_OUT) {
				log.info("Exiting brown out, Voltage: [{}]", formatter.format(RobotController.getBatteryVoltage()));
				previousVoltageStatus = VoltageStatusMessage.EXIT_BROWNED_OUT;
			}else if(RobotController.getBatteryVoltage() < CommonConstants.Diagnostics.UNSAFE_BATTERY_VOLTAGE) {
				if(previousVoltageStatus != VoltageStatusMessage.UNSAFE_VOLTAGE) {
					log.info("Unsafe battery levels: [{}]", formatter.format(RobotController.getBatteryVoltage()));
					previousVoltageStatus = VoltageStatusMessage.UNSAFE_VOLTAGE;
				}
			} else if(RobotController.getBatteryVoltage() > CommonConstants.Diagnostics.UNSAFE_BATTERY_VOLTAGE) {
				if(previousVoltageStatus != VoltageStatusMessage.SAFE_VOLTAGE) {
					log.info("Safe battery level: [{}]", formatter.format(RobotController.getBatteryVoltage()));
					previousVoltageStatus = VoltageStatusMessage.SAFE_VOLTAGE;
				}
			}
		}else {
			previousVoltageStatus = VoltageStatusMessage.SAFE_VOLTAGE;
		}
	}

	@Override
	public void updateSmartDashboard() {
		SmartDashboard.putBoolean("field.FMS Connected", driverStation.isFMSAttached());
		SmartDashboard.putString("field.Alliance", driverStation.getAlliance().toString());
		SmartDashboard.putNumber("field.Station Number", driverStation.getLocation());
		SmartDashboard.putString("field.Match Type", driverStation.getMatchType().toString());
		SmartDashboard.putNumber("field.Match Number", driverStation.getMatchNumber());
	}

	@Override
	public void readSmartDashboard() {
		
	}
	
	@Override
	public double getPeriod() {
		return robot.getPeriod();
	}

}
