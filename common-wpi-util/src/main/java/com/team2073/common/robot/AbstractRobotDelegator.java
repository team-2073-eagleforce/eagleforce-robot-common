package com.team2073.common.robot;

import com.team2073.common.AppConstants;
import com.team2073.common.event.ListenerRegistry;
import com.team2073.common.event.ListenerRegistry.RobotStateEvent;
import com.team2073.common.periodic.OccasionalLoggingRegistry;
import com.team2073.common.periodic.PeriodicRegistry;
import com.team2073.common.smartdashboard.SmartDashboardAware;
import com.team2073.common.util.ExceptionUtil;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;

/**
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
 * public class RobotDelegatorPowerUpImpl extends AbstractRobotDelegator {
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
public abstract class AbstractRobotDelegator extends TimedRobot implements SmartDashboardAware {
	
	private final RobotDelegate robot;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final DriverStation driverStation = DriverStation.getInstance();
	private DecimalFormat formatter = new DecimalFormat("#.##");
	private boolean loggedFmsMatchData = false;

	public AbstractRobotDelegator(RobotDelegate robot) {
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
		robot.robotInit();
		resetLastCheckedTime();
	}

	@Override
	public void disabledInit() {
		resetLastCheckedTime();
		logger.info("disabled");
		ExceptionUtil.suppressVoid(robot::disabledInit, "robot::disabledInit");
		ListenerRegistry.setCurrentEvent(RobotStateEvent.DISABLED);
	}

	@Override
	public void autonomousInit() {
		currentPeriod = MatchPeriod.AUTONOMOUS;
		logger.info("autonomous enabled");
		ExceptionUtil.suppressVoid(robot::autonomousInit, "robot::autonomousInit");
		ListenerRegistry.setCurrentEvent(RobotStateEvent.AUTONOMOUS_START);
	}

	@Override
	public void teleopInit() {
		currentPeriod = MatchPeriod.TELEOP;
		logger.info("teleop enabled");
		ExceptionUtil.suppressVoid(robot::teleopInit, "robot::teleopInit");
		ListenerRegistry.setCurrentEvent(RobotStateEvent.TELEOP_START);
	}

	@Override
	public void testInit() {
		logger.info("test enabled");
		ExceptionUtil.suppressVoid(robot::testInit, "robot::testInit");
		ListenerRegistry.setCurrentEvent(RobotStateEvent.TEST_START);
	}

	@Override
	public void robotPeriodic() {
		logAllChecks();
		logStartingConfig();
		ExceptionUtil.suppressVoid(robot::robotPeriodic, "robot::robotPeriodic");
		// TODO: Test this, merged from driving-practice branch
		PeriodicRegistry.runPeriodic();
		// TODO: Have the LoggingRegistry get called by the PeriodicRegistry instead
		OccasionalLoggingRegistry.startOccasionalLogging();
		// TODO: Have the ListenerRegistry get called by the PeriodicRegistry instead
 		ListenerRegistry.setCurrentEvent(RobotStateEvent.PERIODIC);
		ListenerRegistry.runEventListeners();
	}

	@Override
	public void disabledPeriodic() {
		ExceptionUtil.suppressVoid(robot::disabledPeriodic, "robot::disabledPeriodic");
		ListenerRegistry.setCurrentEvent(RobotStateEvent.PERIODIC);
	}

	@Override
	public void autonomousPeriodic() {
		ExceptionUtil.suppressVoid(robot::autonomousPeriodic, "robot::autonomousPeriodic");
		ListenerRegistry.setCurrentEvent(RobotStateEvent.PERIODIC);
	}

	@Override
	public void teleopPeriodic() {
		ExceptionUtil.suppressVoid(robot::teleopPeriodic, "robot::teleopPeriodic");
		ListenerRegistry.setCurrentEvent(RobotStateEvent.PERIODIC);
	}

	@Override
	public void testPeriodic() {
		ExceptionUtil.suppressVoid(robot::testPeriodic, "robot::testPeriodic");
		ListenerRegistry.setCurrentEvent(RobotStateEvent.PERIODIC);
	}
	
	private enum DsStatusMessage {
		CONNECTED,
		DISCONNECTED;
	}
	
	private DsStatusMessage previousDSMessage = DsStatusMessage.DISCONNECTED;
	
	public void logDsStatus() {
		if(driverStation.isDSAttached()) {
			if(previousDSMessage != DsStatusMessage.CONNECTED)
				logger.info("Driver Station Connected");
				previousDSMessage = DsStatusMessage.CONNECTED;
		}else {
			if(previousDSMessage != DsStatusMessage.DISCONNECTED) {
				logger.info("Driver Station Disconnected");
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
			logger.info("Alliance: [{}], DriverStation: [{}], Match type: [{}], Match Number: [{}]"
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
				logger.info("[{}] seconds left in [{}]"
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
		if(RobotController.isBrownedOut()) {
			if(previousVoltageStatus != VoltageStatusMessage.ENTER_BROWNED_OUT) {
				logger.info("Brown out detected. Voltage: [{}]", formatter.format(RobotController.getBatteryVoltage()));
				previousVoltageStatus = VoltageStatusMessage.ENTER_BROWNED_OUT;
			}
		} else if(!RobotController.isBrownedOut()) {
			if(previousVoltageStatus == VoltageStatusMessage.ENTER_BROWNED_OUT) {
				logger.info("Exiting brown out, Voltage: [{}]", formatter.format(RobotController.getBatteryVoltage()));
				previousVoltageStatus = VoltageStatusMessage.EXIT_BROWNED_OUT;
			}else if(RobotController.getBatteryVoltage() < AppConstants.Diagnostics.UNSAFE_BATTERY_VOLTAGE) {
				if(previousVoltageStatus != VoltageStatusMessage.UNSAFE_VOLTAGE) {
					logger.info("Unsafe battery levels: [{}]", formatter.format(RobotController.getBatteryVoltage()));
					previousVoltageStatus = VoltageStatusMessage.UNSAFE_VOLTAGE;
				}
			} else if(RobotController.getBatteryVoltage() > AppConstants.Diagnostics.UNSAFE_BATTERY_VOLTAGE) {
				if(previousVoltageStatus != VoltageStatusMessage.SAFE_VOLTAGE) {
					logger.info("Safe battery level: [{}]", formatter.format(RobotController.getBatteryVoltage()));
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
	
	
}
