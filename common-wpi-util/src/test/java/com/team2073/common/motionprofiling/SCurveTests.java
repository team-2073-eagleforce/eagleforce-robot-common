package com.team2073.common.motionprofiling;

import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

public class SCurveTests {

	@Test
	public void SCurveMotionProfileGenerator_WHEN_nextPointIsCalled_RETURNS_validPoint(){
		double goalPosition = 5;
		double maxVelcoity = 6;
		double maxAcceleration = 20;
		double averageAcceleration = 15;
		SCurveProfileGenerator profile = new SCurveProfileGenerator(
				goalPosition, maxVelcoity, maxAcceleration, averageAcceleration);
//		double time = 0;
//		while(time < profile.getTotalTime()){
//			ProfileTrajectoryPoint point = profile.nextPoint(.01);
//			profile.nextPoint(profile.getTotalTime()).getPosition();
//			time = point.getCurrentTime();
//			System.out.println(round(point.getCurrentTime(), 4) + "\t" + round(point.getPosition(), 4) + "\t" + round(point.getVelocity(), 4) + "\t" + round(point.getAcceleration(), 4) + "\t" + round(point.getJerk(), 4));
//		}

//		Gets a point really close to the end of the profile to make sure we are decelerating appropriately
		ProfileTrajectoryPoint endPoint = profile.nextPoint(profile.getTotalTime()-.0001);
		assertThat(endPoint.getPosition()).isCloseTo(goalPosition, Offset.offset(.1));
		assertThat(endPoint.getVelocity()).isCloseTo(0, Offset.offset(.1));
		assertThat(endPoint.getAcceleration()).isCloseTo(0, Offset.offset(.1));
	}

	public static double round(double d, int decimalPlace) {
		BigDecimal bd = new BigDecimal(d);
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}

}
