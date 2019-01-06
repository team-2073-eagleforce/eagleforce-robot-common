package com.team2073.common.ctremotionprofile;

import com.ctre.phoenix.motion.TrajectoryPoint;
import com.team2073.common.motionprofiling.MotionProfileConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MotionProfileGenerator {
	private static final Logger logger = LoggerFactory.getLogger(MotionProfileGenerator.class);
	private static final int ACCELERATION_CURVE = 3000;

	public static List<TrajectoryPoint> generatePoints(MotionProfileConfiguration mpc) {
		List<TrajectoryPoint> tpList = new ArrayList<>();

		// Store config in easy to access variables
		final double maxVel = mpc.getMaxVel();
		final double endDistance = mpc.getEndDistance();
		final int interval = mpc.getIntervalVal();
		final double maxAcc = mpc.getMaxAcc();
		final boolean isVelocityOnly = mpc.isVelocityOnly();
		final int intervalVal = mpc.getIntervalVal();

		// Resolve non-config, static variables
		// final double t1 = (1. / (maxAcc / RENAME_THIS));
		final double t1 = ACCELERATION_CURVE / maxAcc;
		final double t2 = t1 / 2;
		double f2;
		final List<Double> f1List = new ArrayList<>();

		// Initialize everything to zero for the first record
		tpList.add(initialTp(interval));
		f1List.add(0.0);
		f2 = 0;

		// Create a counter to use while looping
		int i = 0;
		
		while (true) {
			i++;
			double posOrNeg;
			TrajectoryPoint tPoint = new TrajectoryPoint();
			TrajectoryPoint prevTp = tpList.get(i - 1);

			posOrNeg = increasingOrDecreasing(i, endDistance, maxVel, intervalVal, t1);
			double sumF1Count = Math.max(0, Math.min(1, (f1List.get(i - 1) + posOrNeg)));
			f1List.add(sumF1Count);
			f2 = calculateF2(t2, i, intervalVal, f1List, f2);

//			tPoint.timeDurMs = interval;
			tPoint.timeDur = interval;
			tPoint.profileSlotSelect0 = 0;
			tPoint.velocity = calculateVelocity(maxVel, f1List, f2, i, t2, intervalVal);
			tPoint.position = (prevTp.position + calculatePosition(tPoint, prevTp, intervalVal));

			tpList.add(tPoint);
			logger.trace(i + "\t" + tPoint.velocity + "\t" + tPoint.position );
			if (tPoint.velocity == 0 || (isVelocityOnly && tPoint.velocity == maxVel)) {
				tPoint.isLastPoint = true;
				break;
			}
		}

		return tpList;
	}

	// Private helper methods
	// ====================================================================================================
	private static TrajectoryPoint initialTp(int interval) {
		TrajectoryPoint tp = new TrajectoryPoint();
		tp.timeDur = interval;
		tp.position = 0;
		tp.velocity = 0;
		tp.zeroPos = true;

		return tp;
	}
	
	// TODO: Remove?
//	private static double round(double d, int decimalPlace) {
//		BigDecimal bd = new BigDecimal(d);
//		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
//		return bd.doubleValue();
//	}

	/**
	 * TODO: Jason, add JavaDocs about what this is doing mathematically.
	 * 
	 * @param i
	 * @param endDistance
	 * @param maxVel
	 * @param intervalVal
	 * @param t1
	 * @return
	 */
	// TODO: Break these variables out into a model object to be passed around
	private static double increasingOrDecreasing(int i, double endDistance, double maxVel, int intervalVal, double t1) {
		if (i - 1 < (((endDistance / maxVel) * 1000) / intervalVal)) {
			return (1. / Math.round(t1 / intervalVal));
		} else {
			return ((-1.) / Math.round(t1 / intervalVal));
		}
	}

	private static double calculateF2(double t2, int i, int interval, final List<Double> f1List, double f2) {
		double retVal = 0;
		int sum = (int) (Math.round(t2 / interval));
		if (i == (int) Math.min(Math.round(t2 / interval), i)) {
			retVal = f2 + f1List.get(i);
		} else {
			retVal = 0;
			for (int j = 0; j < sum; j++) {
				retVal += f1List.get(i - j);
			}
		}

		return retVal;
	}

	private static double calculateVelocity(final double maxVel, final List<Double> f1List, final double f2, int i, double t2,
			double intervalVal) {
		return maxVel * ((f1List.get(i) + f2) / (1 + (Math.round(t2 / intervalVal))));
	}

	private static double calculatePosition(TrajectoryPoint currTp, TrajectoryPoint prevTp, int intervalVal) {
		final double avgVel = (currTp.velocity + prevTp.velocity) / 2;
		return ((avgVel * intervalVal) / 1000);
	}
}
