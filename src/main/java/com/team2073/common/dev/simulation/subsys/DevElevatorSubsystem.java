package com.team2073.common.dev.simulation.subsys;

import com.team2073.common.dev.simulation.io.FakeTalon;
import com.team2073.common.objective.StatusChecker;

import edu.wpi.first.wpilibj.command.Subsystem;

public class DevElevatorSubsystem extends Subsystem {
	
	public enum ElevatorHeight {
		ZERO(0, -1, 1),
//		BETWEEN_ZERO_AND_SWITCH,
		SWITCH(1, 8, 9),
//		BETWEEN_SWITCH_AND_PIVOT,
		PIVOT(2, 18, 19),
//		BETWEEN_PIVOT_AND_MAX,
		MAX(3, 28, 29);

		// TODO: Extract this stuff to an object?
		private int index;
		private double lowerBound;
		private double midPoint;
		private double upperBound;
		
		ElevatorHeight(int index, double lowerBound, double upperBound) {
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
			return point >= lowerBound && point <= upperBound;
		}

		public String toStringDetailed() {
			return super.toString() + "{" + lowerBound + " -> " + midPoint + " <- " + upperBound + "}";
		}
	}
	
	public enum GoalState {
		WAITING_FOR_GOAL,
		GOAL_REQUESTED,
		INITIALIZING_GOAL,
		PROCESSING_GOAL,
		INTERRUPTED,
		COMPLETED,
		ERROR,
		INVALID;
	}
	
	private GoalState goalState = null;
	private ElevatorHeight goalHeight = null;
	private StatusChecker goalStatus = null;
	private FakeTalon talon = new FakeTalon();

	@Override
	protected void initDefaultCommand() {
	}
	
	@Override
	public void periodic() {
		// Old Code:
		
//		if (goalHeight == null) {
//			return;
//		}
//		
//		if (goalStatus != null && goalStatus.isInterrupted()) {
//			stop();
//			return;
//		}
//		
//		double currHeight = getCurrentHeight();
//		
//		if(goalHeight.withinBounds(currHeight)) {
//			System.out.printf("[%s] Reached goal height [%s]. Current height [%s]\n"
//					, getClass().getSimpleName(), goalHeight.toStringDetailed(), currHeight);
//			stop();
//			return;
//		}
//		
//		if(currHeight < goalHeight.lowerBound) {
//			moveUp();
//		} else if(currHeight > goalHeight.upperBound) {
//			moveDown();
//		} else {
//			System.out.println("Your code be broken yo!");
//		}
		
		// New Code:
		
		double currHeight = getCurrentHeight();
		
		// 1) Check if we have anything to process
		if (goalHeight == null) {
			goalState = GoalState.WAITING_FOR_GOAL;
			holdCurrentPosition();
			
		// Quick assertion check
		} else if (goalStatus == null) {
			goalState = GoalState.ERROR;
			throw new IllegalStateException("goalStatus must not be null when goalHeight is not null!");
			
		// 2) Check if we're interrupted
		} else if (goalStatus.isInterrupted()) {
			goalState = GoalState.INTERRUPTED;
			completeGoal();
			
		// 3) Check if we have initialized the goal yet
		} else if (goalState == GoalState.GOAL_REQUESTED) {
			if (!isGoalPositionSafe()) {
				System.out.printf("[%s] An attempt to move to unsafe position was blocked.\n", getClass().getSimpleName());
				goalState = GoalState.INVALID;
				completeGoal();
			} else {
				goalState = GoalState.INITIALIZING_GOAL;
				prepareToMoveToGoalPosition();
			}
			
		// 4) Check if goal position reached
		} else if (goalHeight.withinBounds(currHeight)) {
			goalState = GoalState.COMPLETED;
			System.out.printf("[%s] Reached goal height [%s]. Current height [%s]\n"
					, getClass().getSimpleName(), goalHeight.toStringDetailed(), currHeight);
			completeGoal();
			
		// 5) None of the above were true. Start/continue moving towards goal position
		} else {
			goalState = GoalState.PROCESSING_GOAL;
			moveToGoalPosition();
		}
	}

	// Public control
	// ============================================================
	public StatusChecker moveToHeight(ElevatorHeight height) {
		System.out.printf("[%s] Setting goalHeight to [%s]\n", getClass().getSimpleName(), height);
		goalHeight = height;
		goalState = GoalState.GOAL_REQUESTED;
		goalStatus = new StatusChecker();
		return goalStatus;
	}

	// Public informational
	// ============================================================
//	public boolean isMovingUp(ElevatorHeight comparingState) {
//		return getCurrentHeight() < comparingState.index;
//	}
//
//	public boolean isMovingDown(ElevatorHeight comparingState) {
//		return getCurrentHeight() > comparingState.index;
//	}

	public boolean isAtHeight(ElevatorHeight height) {
		return height.withinBounds(getCurrentHeight());
	}

	public boolean isAtOrAboveHeight(ElevatorHeight height) {
		return getCurrentHeight() >= height.lowerBound;
	}
	
	public double getCurrentHeight() {
		return talon.position;
	}

	// Private motor control
	// ============================================================
//	private void stop() {
//		talon.set(0);
//		goalHeight = null;
//		if(goalStatus != null) {
////			System.out.println("DevElevatorSubsystem completing StatusChecker");
//			goalStatus.complete();
//			goalStatus = null;
//		}
//	}
//	
//	private void moveUp() {
////		System.out.printf("DevElevatorSubsystem: Moving up. [%s] -> [%s]\n", getCurrentHeight(), goalHeight.lowerBound);
//		talon.set(1);
//	}
//	
//	private void moveDown() {
////		System.out.printf("DevElevatorSubsystem: Moving down. [%s] -> [%s]\n", getCurrentHeight(), goalHeight.upperBound);
//		talon.set(-1);
//	}
	
	private void completeGoal() {
//		System.out.println("DevElevatorSubsystem completing StatusChecker");
		goalStatus.complete();
		goalStatus = null;
		goalHeight = null;
	}
	
	private void prepareToMoveToGoalPosition() {
		// TODO
	}
	
	private void moveToGoalPosition() {
		double currHeight = getCurrentHeight();
		if(currHeight < goalHeight.lowerBound) {
			System.out.printf("DevElevatorSubsystem: Moving to [%s]. [%s] -> [%s]\n", goalHeight, getCurrentHeight(), goalHeight.lowerBound);
			talon.set(1);
		} else if(currHeight > goalHeight.upperBound) {
			System.out.printf("DevElevatorSubsystem: Moving to [%s]. [%s] -> [%s]\n", goalHeight, getCurrentHeight(), goalHeight.upperBound);
			talon.set(-1);
		} else {
			System.out.println("Your code be broken yo!");
		}
	}
	
	private void holdCurrentPosition() {
		talon.set(0);
	}
	
	private boolean isGoalPositionSafe() {
		return goalHeight.midPoint >= ElevatorHeight.ZERO.lowerBound && goalHeight.midPoint <= ElevatorHeight.MAX.upperBound;
	}

}
