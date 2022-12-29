package com.team2073.common.dev.simulation.subsys;

import com.team2073.common.dev.simulation.io.FakeTalon;
import com.team2073.common.objective.StatusChecker;

//import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class DevShooterSubsystem extends SubsystemBase {

	public enum ShooterAngle {

		FORWARD_STRAIGHT(0, -1, 1),
//		BETWEEN_FORWARD_STRAIGHT_AND_FORWARD_UP,
		FORWARD_UP(1, 29, 31),
//		BETWEEN_FORWARD_UP_AND_BACKWARD,
		BACKWARD(2, 89, 91);

		private int index;
		private double lowerBound;
		private double midPoint;
		private double upperBound;
		
		ShooterAngle(int index, double lowerBound, double upperBound) {
			this.index = index;
			this.lowerBound = lowerBound;
			this.midPoint = (lowerBound + upperBound) / 2;
			this.upperBound = upperBound;
		}

		public int getIndex() {
			return index;
		}
		
		public double getLowerBound() {
			return lowerBound;
		}
		
		public double getUpperBound() {
			return upperBound;
		}

		public double getMidPoint() {
			return midPoint;
		}
		
		public boolean withinBounds(double point) {
			boolean result =  point >= lowerBound && point <= upperBound;
//			System.out.printf("Checking if [%s] is within [%s] and [%s]. Result: [%s].\n"
//					, point, lowerBound, upperBound, result);
			return result;
		}

		public String toStringDetailed() {
			return super.toString() + "{" + lowerBound + " -> " + midPoint + " <- " + upperBound + "}";
		}
	}
	
	private ShooterAngle goalAngle = ShooterAngle.FORWARD_STRAIGHT;
	private StatusChecker goalStatus = null;
	private FakeTalon talon = new FakeTalon(ShooterAngle.FORWARD_STRAIGHT.lowerBound);

//	@Override
//	protected void initDefaultCommand() {
//
//	}
	
	@Override
	public void periodic() {
		if (goalAngle == null) {
			return;
		}
		
		double currAngle = getCurrentAngle();
		
		if(goalAngle.withinBounds(currAngle)) {
			System.out.printf("DevShooterSubsystem: Reached goal angle [%s]. Current angle [%s]\n"
					, goalAngle.toStringDetailed(), currAngle);
			stop();
			return;
		}
		
		if(currAngle < goalAngle.lowerBound) {
			moveBack();
		} else if(currAngle > goalAngle.upperBound) {
			moveForward();
		} else {
			System.out.println("Your code be broken yo!");
		}
	}

	// Public control
	// ============================================================
	public void interrupt() {
		stop();
	}

	public void moveToForwardUp() {
		moveToAngle(ShooterAngle.FORWARD_UP);
	}

	public StatusChecker moveToAngle(ShooterAngle shooterAngle) {
		System.out.println("Setting goalAngle to: " + shooterAngle);
		goalAngle = shooterAngle;
		goalStatus = new StatusChecker();
		return goalStatus;
	}

	// Public informational
	// ============================================================
	public boolean isAtAngle(ShooterAngle shooterAngle) {
		return shooterAngle.withinBounds(getCurrentAngle());
	}
	
	public double getCurrentAngle() {
		return talon.position;
	}

	public boolean isPivotBack() {
		return getCurrentAngle() > ShooterAngle.FORWARD_UP.upperBound;
	}

	// Private motor control
	// ============================================================
	private void stop() {
		System.out.println("Stopping Shooter. Setting goalAngle to null");
		talon.set(0);
		goalAngle = null;
		if(goalStatus != null) {
			System.out.println("DevShooterSubsystem completing StatusChecker");
			goalStatus.complete();
			goalStatus = null;
		}
	}
	
	private void moveBack() {
		System.out.printf("DevShooterSubsystem: Moving to back. [%s] -> [%s]\n", getCurrentAngle(), goalAngle.lowerBound);
		talon.set(2);
	}
	
	private void moveForward() {
		System.out.printf("DevShooterSubsystem: Moving to front. [%s] -> [%s]\n", getCurrentAngle(), goalAngle.upperBound);
		talon.set(-2);
	}
}
