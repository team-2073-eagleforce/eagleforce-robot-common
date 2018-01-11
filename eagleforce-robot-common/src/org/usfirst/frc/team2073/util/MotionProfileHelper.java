package org.usfirst.frc.team2073.util;

import java.util.List;

import org.usfirst.frc.team2073.conf.AppConstants.DashboardKeys;
import org.usfirst.frc.team2073.conf.AppConstants.Defaults;

import com.ctre.phoenix.motion.MotionProfileStatus;
import com.ctre.phoenix.motion.TrajectoryPoint;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class MotionProfileHelper {
	public static void initTalon(TalonSRX talon) {
//		talon.(SmartDashboard.getNumber(DashboardKeys.FGAIN, Defaults.FGAIN));
		talon.config_kF(0, SmartDashboard.getNumber(DashboardKeys.FGAIN, Defaults.FGAIN), 5);
		talon.set(ControlMode.MotionProfile, 0);
//		talon.changeControlMode(TalonControlMode.MotionProfile);
//		talon.set(TalonSRX.SetValueMotionProfile.Disable.value);
		talon.clearMotionProfileTrajectories();
	}
	
	public static void setF(TalonSRX talon) {
//		talon.setF(SmartDashboard.getNumber(DashboardKeys.FGAIN, Defaults.FGAIN));
		talon.config_kF(0, SmartDashboard.getNumber(DashboardKeys.FGAIN, Defaults.FGAIN), 5);
	}

	public static void resetTalon(TalonSRX talon) {
		talon.set(ControlMode.MotionProfile, 0);
		talon.clearMotionProfileTrajectories();
	}
	
//	different version, potentially broken
//	public static void pushPoints(TalonSRX talon, List<TrajectoryPoint> trajPointList) {
//		trajPointList.forEach(talon::pushMotionProfileTrajectory);
//	}
	public static void pushPoints(TalonSRX talon, List<TrajectoryPoint> trajPointList) {
		trajPointList.forEach(trajPoint -> talon.pushMotionProfileTrajectory(trajPoint));
	}

	public static void processPoints(TalonSRX talon) {
		talon.processMotionProfileBuffer();
//		talon.set(TalonSRX.SetValueMotionProfile.Enable.value);
		talon.set(ControlMode.MotionProfile, 1);
	}

	public static void resetEnc(TalonSRX talon) {
		talon.setSelectedSensorPosition(0, 0, 5);
	}

	public static void stopTalon(TalonSRX talon) {
		talon.set(ControlMode.MotionProfile, 0);
		talon.clearMotionProfileTrajectories();
//		resetEnc(talon);
	}

	public static void checkDirection(TalonSRX talon, boolean forwards) {
//		talon.reverseOutput(!forwards);
//		talon.
	}

	public static void resetAndPushPoints(TalonSRX talon, List<TrajectoryPoint> tpList, boolean isForwards) {
		resetTalon(talon);
		pushPoints(talon, tpList);
		checkDirection(talon, isForwards);
	}

	public static boolean isFinished(TalonSRX talon) {
		MotionProfileStatus talonStatus = new MotionProfileStatus();
		talon.getMotionProfileStatus(talonStatus);
		return talonStatus.topBufferCnt == 0 && talonStatus.btmBufferCnt == 0;
	}

}