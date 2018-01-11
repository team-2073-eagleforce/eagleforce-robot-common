package org.usfirst.frc.team2073.util;

import java.util.ArrayList;
import java.util.List;

import org.usfirst.frc.team2073.domain.MotionProfileConfiguration;

import com.ctre.phoenix.motion.TrajectoryPoint;

public class MotionProfileGenerator {
	private static final int ACCELERATION_CURVE = 3000;

	public static List<TrajectoryPoint> generatePoints(MotionProfileConfiguration mpc) {
		List<TrajectoryPoint> tpList = new ArrayList<>();

		// Store config in easy to access variables
		final double maxVel = mpc.getMaxVel();
		final double endDistance = mpc.getEndDistance();
		final int interval = mpc.getInterval();
		final double maxAcc = mpc.getMaxAcc();
		final boolean isVelocityOnly = mpc.isVelocityOnly();

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

			posOrNeg = increasingOrDecreasing(i, endDistance, maxVel, interval, t1);
			double sumF1Count = Math.max(0, Math.min(1, (f1List.get(i - 1) + posOrNeg)));
			f1List.add(sumF1Count);
			f2 = calculateF2(t2, i, interval, f1List, f2);

			tPoint.profileSlotSelect = 0;
//			tPoint.timeDurMs = interval;
			tPoint.velocity = calculateVelocity(maxVel, f1List, f2, i, t2, interval);
			tPoint.position = (prevTp.position + calculatePosition(tPoint, prevTp, interval));

			tpList.add(tPoint);
			System.out.println(i + "\t" + tPoint.velocity + "\t" + tPoint.position );
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
//		tp.timeDurMs = interval;
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
	 * @param interval
	 * @param t1
	 * @return
	 */
	// TODO: Break these variables out into a model object to be passed around
	private static double increasingOrDecreasing(int i, double endDistance, double maxVel, int interval, double t1) {
		if (i - 1 < (((endDistance / maxVel) * 1000) / interval)) {
			return (1. / Math.round(t1 / interval));
		} else {
			return ((-1.) / Math.round(t1 / interval));
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
			double interval) {
		return maxVel * ((f1List.get(i) + f2) / (1 + (Math.round(t2 / interval))));
	}

	private static double calculatePosition(TrajectoryPoint currTp, TrajectoryPoint prevTp, int interval) {
		final double avgVel = (currTp.velocity + prevTp.velocity) / 2;
		return ((avgVel * interval) / 1000);
	}
}
