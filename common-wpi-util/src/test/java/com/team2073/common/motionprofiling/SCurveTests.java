package com.team2073.common.motionprofiling;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

public class SCurveTests {

	@Test
	public void SCurveMotionProfileGenerator_WHEN_nextPointIsCalled_RETURNS_validPoint(){
		SCurveProfileGenerator profile = new SCurveProfileGenerator(
				100, 15, 20, 15);
		double time = 0;
		while(time < profile.getTotalTime()){
			ProfileTrajectoryPoint point = profile.nextPoint(.01);
			time = point.getCurrentTime();
			System.out.println(round(point.getCurrentTime(), 4) + "\t" + round(point.getPosition(), 4) + "\t" + round(point.getVelocity(), 4) + "\t" + round(point.getAcceleration(), 4) + "\t" + round(point.getJerk(), 4));

		}
	}

	public static double round(double d, int decimalPlace) {
		BigDecimal bd = new BigDecimal(d);
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}

}
