package com.team2073.common.motionprofiling;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctre.phoenix.motion.MotionProfileStatus;
import com.ctre.phoenix.motion.SetValueMotionProfile;
import com.ctre.phoenix.motion.TrajectoryPoint;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class MotionProfileHelper {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private TalonSRX talon;
	private boolean loggedTalonNull = false;
	private String helperName;
	private boolean defaultDirection;
	private MotionProfileStatus talonStatus = new MotionProfileStatus();
	private Notifier processNotifier = new Notifier(() -> {
		if (talon != null) {
			talon.processMotionProfileBuffer();
			loggedTalonNull = false;
		} else {
			if(!loggedTalonNull) {
				logger.warn("MPHelper [{}]'s Talon is null, not processing motion profiling.", helperName);
				loggedTalonNull = true;
			}
		}
	});

	private String fSmartDashboardKey;
	// TODO: Change to fGainDefault
	private double fDefault;

	// TODO: What if we want multiple slaves? Shouldn't this be a list?
	private MotionProfileHelper slave = null;

	/**
	 * 
	 * @param talon
	 * @param defaultDirection
	 * @param fSmartDashboardKey
	 * @param fDefault
	 * @param helperName Used in logging to differentiate between MotionProfileHelpers.
	 */
	public MotionProfileHelper(TalonSRX talon, boolean defaultDirection, String fSmartDashboardKey, double fDefault, String helperName) {
		this.talon = talon;
		this.defaultDirection = defaultDirection;
		this.fSmartDashboardKey = fSmartDashboardKey;
		this.fDefault = fDefault;
		this.helperName = helperName;
		startProcessNotifier();
		SmartDashboard.setDefaultNumber(fSmartDashboardKey, fDefault);
	}

	/**
	 * Deprecated 2/21/2018. Use {@link #MotionProfileHelper(TalonSRX, boolean, String, double, String)} instead.
	 * 
	 * @param talon
	 * @param defaultDirection
	 * @param fSmartDashboardKey
	 * @param fDefault
	 */
	public MotionProfileHelper(TalonSRX talon, boolean defaultDirection, String fSmartDashboardKey, double fDefault) {
		this(talon, defaultDirection, fSmartDashboardKey, fDefault, "MP Helper " + talon.getDeviceID());
	}

	// This constructor is internally used for slaves
	private MotionProfileHelper() {}
	
	private void startProcessNotifier() {
		processNotifier.startPeriodic(0.005);
	}

	public void setSlave(TalonSRX slaveTalon, boolean slaveDefaultDirection) {
		slaveTalon.set(ControlMode.Follower, talon.getDeviceID());
		slave = new MotionProfileHelper();
		slave.talon = slaveTalon;
		slave.defaultDirection = slaveDefaultDirection;
		slave.startProcessNotifier();
	}

	public void initTalon() {
		talon.config_kF(0, SmartDashboard.getNumber(fSmartDashboardKey, fDefault), 5);
		talon.set(ControlMode.MotionProfile, SetValueMotionProfile.Disable.value);
		talon.configAllowableClosedloopError(0, 5, 5);
		talon.clearMotionProfileTrajectories();
	}

	public void setF(double f) {
		talon.config_kF(0, f, 0);
		if (slave != null) {
			slave.setF(f);
		}
	}

	public void resetTalon() {
		talon.set(ControlMode.MotionProfile, SetValueMotionProfile.Disable.value);
		talon.clearMotionProfileHasUnderrun(0);
		talon.clearMotionProfileTrajectories();
	}

	public void pushPoints(List<TrajectoryPoint> trajPointList) {
		Queue<TrajectoryPoint> trajPointQueue = new LinkedList<>(trajPointList);
		while (!trajPointQueue.isEmpty()) {
			if (talon.isMotionProfileTopLevelBufferFull()) {
				Timer.delay(0.005);
				continue;
			}
			talon.pushMotionProfileTrajectory(trajPointQueue.poll());
		}
	}

	public void processPoints() {
		if(isUnderRun()) {
			logger.warn("[{}]: Motion Profile is underrun! Top=[{}], Bottom=[{}]"
					, helperName, topBufferCount(), bottomBufferCount());
		}
		
		if(logger.isTraceEnabled()) {
			logger.trace("[{}]: TopBuffer=[{}] BottomBuffer=[{}]."
					, helperName, topBufferCount(), bottomBufferCount());
		}
		
		talon.processMotionProfileBuffer();
		talon.set(ControlMode.MotionProfile, SetValueMotionProfile.Enable.value);
	}

	public void resetEnc() {
		talon.setSelectedSensorPosition(0, 0, 0);
	}

	public void stopTalon() {
		talon.set(ControlMode.MotionProfile, SetValueMotionProfile.Disable.value);
		talon.clearMotionProfileTrajectories();
	}

	public void checkDirection(boolean forwards) {
		if (forwards) {
			talon.setInverted(!defaultDirection);
		} else {
			talon.setInverted(defaultDirection);
		}
	}

	public void pushPointsDrive(List<TrajectoryPoint> tpList, boolean isForwards) {
		pushPoints(tpList);
		checkDirection(isForwards);
		if (slave != null) {
			slave.checkDirection(isForwards);
		}
	}
	
	public void pushPointsSingle(List<TrajectoryPoint> tpList, boolean isForwards) {
		pushPoints(tpList);
		checkDirection(isForwards);
	}

	// TODO: Remove this method?
	public void reset() {
		resetTalon();
	}
	
	public int topBufferCount() {
		talon.getMotionProfileStatus(talonStatus);
		return talonStatus.topBufferCnt;
	}
	
	public int bottomBufferCount() {
		talon.getMotionProfileStatus(talonStatus);
		return talonStatus.btmBufferCnt;
	}

	public boolean isFinished() {
		talon.getMotionProfileStatus(talonStatus);
		return talonStatus.topBufferCnt == 0 && talonStatus.btmBufferCnt == 0;
	}
	
	public boolean isUnderRun() {
		talon.getMotionProfileStatus(talonStatus);
		return talonStatus.isUnderrun;
	}
	
	public boolean hasUnderRun() {
		talon.getMotionProfileStatus(talonStatus);
		return talonStatus.hasUnderrun;
	}

	public boolean isBufferSufficentlyFull(int minPointsInBuffer) {
//		return talon.getMotionProfileTopLevelBufferCount() > minPointsInBuffer;
		return bottomBufferCount() > minPointsInBuffer;
	}

	public void changeF(double fChange) {
		double newF = SmartDashboard.getNumber(fSmartDashboardKey, fDefault) + fChange;
		talon.config_kF(0, newF, 5);
		SmartDashboard.putNumber(fSmartDashboardKey, newF);
	}
}