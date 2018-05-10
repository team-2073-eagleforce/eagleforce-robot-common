package com.team2073.common.robot;

import com.team2073.common.util.ExceptionUtil;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.TimedRobot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.conf.AppConstants;

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
public abstract class AbstractRobotDelegator extends TimedRobot {

	private RobotController robotController;
	private final RobotDelegate robot;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final DriverStation driverStation = DriverStation.getInstance();
	private DecimalFormat formatter = new DecimalFormat("#.##");

	public AbstractRobotDelegator(RobotDelegate robot, RobotController robotController) {
		this.robot = robot;
		this.robotController = robotController;
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
		logStartingConfig();
		resetLastCheckedTime();
	}

	@Override
	public void disabledInit() {
		resetLastCheckedTime();
		logger.info("disabled");
		ExceptionUtil.suppressVoid(robot::disabledInit, "robot::disabledInit");
	}

	@Override
	public void autonomousInit() {
		currentPeriod = MatchPeriod.AUTONOMOUS;
		logger.info("autonomous enabled");
		ExceptionUtil.suppressVoid(robot::autonomousInit, "robot::autonomousInit");
	}

	@Override
	public void teleopInit() {
		currentPeriod = MatchPeriod.TELEOP;
		logger.info("teleop enabled");
		ExceptionUtil.suppressVoid(robot::teleopInit, "robot::teleopInit");
	}

	@Override
	public void testInit() {
		logger.info("test enabled");
		ExceptionUtil.suppressVoid(robot::testInit, "robot::testInit");
	}

	@Override
	public void robotPeriodic() {
		logAllChecks();
		ExceptionUtil.suppressVoid(robot::robotPeriodic, "robot::robotPeriodic");
		PeriodicRegistry.runPeriodic();
	}

	@Override
	public void disabledPeriodic() {
		ExceptionUtil.suppressVoid(robot::disabledPeriodic, "robot::disabledPeriodic");
	}

	@Override
	public void autonomousPeriodic() {
		ExceptionUtil.suppressVoid(robot::autonomousPeriodic, "robot::autonomousPeriodic");
	}

	@Override
	public void teleopPeriodic() {
		ExceptionUtil.suppressVoid(robot::teleopPeriodic, "robot::teleopPeriodic");
	}

	@Override
	public void testPeriodic() {
		ExceptionUtil.suppressVoid(robot::testPeriodic, "robot::testPeriodic");
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
		return true;
//		return driverStation.isFMSAttached();
	}
		
	public void logStartingConfig() {		
		if(isRealMatch()) {
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
		if(robotController.isBrownedOut()) {
			if(previousVoltageStatus != VoltageStatusMessage.ENTER_BROWNED_OUT) {
				logger.info("Brown out detected. Voltage: [{}]", formatter.format(robotController.getBatteryVoltage()));
				previousVoltageStatus = VoltageStatusMessage.ENTER_BROWNED_OUT;
			}
		} else if(!robotController.isBrownedOut()) {
			if(previousVoltageStatus == VoltageStatusMessage.ENTER_BROWNED_OUT) {
				logger.info("Exiting brown out, Voltage: [{}]", formatter.format(robotController.getBatteryVoltage()));
				previousVoltageStatus = VoltageStatusMessage.EXIT_BROWNED_OUT;
			}else if(robotController.getBatteryVoltage() < AppConstants.Diagnostics.UNSAFE_BATTERY_VOLTAGE) {
				if(previousVoltageStatus != VoltageStatusMessage.UNSAFE_VOLTAGE) {
					logger.info("Unsafe battery levels: [{}]", formatter.format(robotController.getBatteryVoltage()));
					previousVoltageStatus = VoltageStatusMessage.UNSAFE_VOLTAGE;
				}
			} else if(robotController.getBatteryVoltage() > AppConstants.Diagnostics.UNSAFE_BATTERY_VOLTAGE) {
				if(previousVoltageStatus != VoltageStatusMessage.SAFE_VOLTAGE) {
					logger.info("Safe battery level: [{}]", formatter.format(robotController.getBatteryVoltage()));
					previousVoltageStatus = VoltageStatusMessage.SAFE_VOLTAGE;
				}
			}
		}else {
			previousVoltageStatus = VoltageStatusMessage.SAFE_VOLTAGE;
		}
	}
}
