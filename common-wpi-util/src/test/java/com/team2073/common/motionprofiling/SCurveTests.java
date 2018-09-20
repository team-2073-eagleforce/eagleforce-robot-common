package com.team2073.common.motionprofiling;

import com.team2073.common.controlloop.MotionProfileControlloop;
import com.team2073.common.util.ThreadUtil;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SCurveTests {

	@Test
	public void SCurveMotionProfileGenerator_WHEN_decelerating_RETURNS_appropriateValues(){
		double goalPosition = 5;
		double maxVelcoity = 6;
		double maxAcceleration = 20;
		double averageAcceleration = 15;
		SCurveProfileGenerator profile = new SCurveProfileGenerator(
				goalPosition, maxVelcoity, maxAcceleration, averageAcceleration);

//		Gets a point really close to the end of the profile to make sure we are decelerating appropriately
		ProfileTrajectoryPoint endPoint = profile.nextPoint(profile.getTotalTime()-.0001);
		assertThat(endPoint.getPosition()).isCloseTo(goalPosition, Offset.offset(.1));
		assertThat(endPoint.getVelocity()).isCloseTo(0, Offset.offset(.1));
		assertThat(endPoint.getAcceleration()).isCloseTo(0, Offset.offset(.1));
	}

	@Test
	public void MotionProfileControlloop_WHEN_started_RETURNS_sensibleOutputValues(){
		double goalPosition = 5;
		double maxVelcoity = 6;
		double maxAcceleration = 20;
		double averageAcceleration = 15;

		ProfileTrajectoryPoint point;
		SCurveProfileGenerator profile = new SCurveProfileGenerator(
				goalPosition, maxVelcoity, maxAcceleration, averageAcceleration);
		MotionProfileControlloop mpc = new MotionProfileControlloop(.05, .01, .1666 , .005, .01, 1);

		mpc.dataPointCallable(() -> profile.nextPoint(.01));
		mpc.updatePosition(() -> profile.currentPosition());

		mpc.start();
		ThreadUtil.sleep(500);
		assertThat(mpc.getOutput()).isNotZero();
	}




}
