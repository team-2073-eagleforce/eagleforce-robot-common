package com.team2073.common.simulation.model;

import com.team2073.common.controlloop.MotionProfileControlloop;
import com.team2073.common.motionprofiling.ProfileConfiguration;
import com.team2073.common.motionprofiling.SCurveProfileManager;
import com.team2073.common.motionprofiling.lib.trajectory.Trajectory;
import com.team2073.common.simulation.SimulationConstants.MotorType;
import com.team2073.common.simulation.env.SimulationEnvironment;
import com.team2073.common.util.GraphCSVUtil;

import static com.team2073.common.util.ConversionUtil.*;

/**
 * For Systems like elevators =)
 *
 * @author Jason Stanley
 */
public class LinearMotionMechanism extends AbstractSimulationMechanism {

	private final double pulleyRadius;
	double time;

	public LinearMotionMechanism(double gearRatio, MotorType motor, int motorCount, double massOnSystem, double pullyRadius) {
		super(gearRatio, motor, motorCount, massOnSystem);
		this.pulleyRadius = inchesToMeters(pullyRadius);
	}

	public static void main(String[] args) {
		LinearMotionMechanism lmm = new LinearMotionMechanism(25, MotorType.PRO, 3, 15, 1.75 / 2);
		GraphCSVUtil graph = new GraphCSVUtil("ElevatorSimulation", "time", "ProfilePosition",
				"ProfileVelocity", "ProfileAcceleration", "ProfileJerk", "actual position", "actual velocity");
		SCurveProfileManager manager = new SCurveProfileManager(new MotionProfileControlloop(.05, 0, .01, .15 / 800, .05, 1),
				new ProfileConfiguration(100, 800, 8000, .01), () -> lmm.dank(30, .30));
		double time = 0;
		manager.setPoint(30d);
		while (!manager.isCurrentProfileFinished()) {
			lmm.manualUpdate(10);
			manager.newOutput();
			Trajectory.Segment seg = manager.getProfile().getSegment((int) Math.round(time / .01));
			graph.updateMainFile(time, seg.pos, seg.vel,
					seg.acc, seg.jerk, lmm.position(), lmm.velocity(), manager.getOutput());
			time += .01;
		}
		double t1 = time;
		System.out.println(t1);
		manager.setPoint(33d);
		while (!manager.isCurrentProfileFinished()) {
			manager.newOutput();
			Trajectory.Segment seg = manager.getProfile().getSegment((int) Math.round((time - t1) / .01));
			graph.updateMainFile(time, seg.pos, seg.vel,
					seg.acc, seg.jerk, lmm.position(), lmm.velocity(), manager.getOutput());
			time += .01;
		}

		graph.writeToFile();

	}

	public double dank(double posofT1, double timeOfT1) {
		if (time < timeOfT1) {
			return 10d;
		}
		return posofT1;
	}

	@Override
	public void updateVoltage(double voltage) {
		currentVoltage = voltage;
	}

	@Override
	public void cycle(SimulationEnvironment env) {
		super.cycle(env);

		setPosition(calculatePosition(env.getIntervalMs()));
		setVelocity(calculateVelocity(env.getIntervalMs()));
	}

	public void manualUpdate(int intervalMs) {
		time += msToSeconds(intervalMs);
		setPosition(calculatePosition(intervalMs));
		setVelocity(calculateVelocity(intervalMs));
	}

	/**
	 * Calculates the Mechanism's acceleration given the current mechanism velocity and voltage operating on the motors.
	 * a = (Volt * K_t * G * r)/ (m * R) - (V * K_t * G^2 * r) / ( K_v * m * R)
	 */
	@Override
	public double calculateAcceleration() {
		acceleration = ((currentVoltage * torqueConstant * gearRatio * pulleyRadius) / (massOnSystem * motorResistance))
				- (((inchesToMeters(velocity) * Math.pow(gearRatio, 2)) * pulleyRadius) / (velocityConstant * lbToKg(massOnSystem) * motorResistance));
		return metersToInches(acceleration);
	}

	/**
	 * Integrates over the Acceleration to find how much our velocity has changed in the past interval.
	 * v = a * t + v_0
	 *
	 * @param intervalInMs
	 */
	@Override
	public double calculateVelocity(int intervalInMs) {
		velocity += msToSeconds(intervalInMs) * calculateAcceleration();
		return velocity;
	}

	/**
	 * Integrates over the Velocity to find how much our position has changed in the past interval.
	 * p = v * t + p_0
	 *
	 * @param intervalInMs
	 */
	@Override
	public double calculatePosition(int intervalInMs) {
		position += msToSeconds(intervalInMs) * velocity;
		return position;
	}

}
