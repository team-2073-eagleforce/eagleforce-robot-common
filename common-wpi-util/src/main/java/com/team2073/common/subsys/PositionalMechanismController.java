package com.team2073.common.subsys;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.IMotorController;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.team2073.common.assertion.Assert;
import com.team2073.common.config.CommonProperties;
import com.team2073.common.ctx.RobotContext;
import com.team2073.common.datarecorder.model.LifecycleAwareRecordable;
import com.team2073.common.objective.StatusChecker;
import com.team2073.common.periodic.PeriodicRunnable;
import com.team2073.common.periodic.SmartDashboardAware;
import com.team2073.common.periodic.SmartDashboardAwareRunner;
import com.team2073.common.position.Position;
import com.team2073.common.position.PositionContainer;
import com.team2073.common.position.converter.NoOpPositionConverter;
import com.team2073.common.position.converter.PositionConverter;
import com.team2073.common.position.hold.DisabledHoldingStrategy;
import com.team2073.common.position.hold.HoldingStrategy;
import com.team2073.common.position.hold.PIDHoldingStrategy;
import com.team2073.common.robot.adapter.NetworkTableAdapter;
import com.team2073.common.robot.adapter.NetworkTableEntryAdapter;
import com.team2073.common.speedcontroller.PidIndex;
import com.team2073.common.subsys.PositionalMechanismController.IOGateway.Info;
import com.team2073.common.util.StringUtil;
import org.apache.commons.math3.util.Precision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * TODO
 * @author Preston Briggs
 *
 * @param <T> The enum that defines the various positions this subsystem is capable of.
 */
public class PositionalMechanismController<T extends Enum<T> & PositionContainer> implements SmartDashboardAware, PeriodicRunnable {

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
	
	public enum HoldType {
		/** See: {@link PIDHoldingStrategy}. */
		PID,
		/** Uses a {@link DisabledHoldingStrategy} without modifying the {@link NeutralMode}. */
		DISABLE,
		/** Uses a {@link DisabledHoldingStrategy} changing the {@link NeutralMode} to coast mode upon hold. */
		COAST,
		/** Uses a {@link DisabledHoldingStrategy} changing the {@link NeutralMode} to brake mode upon hold. */
		BRAKE;
	}
	
	private static AtomicInteger unnamedCount = new AtomicInteger(0);
	
	private static String unnamed() {
		return "UNNAMED-" + unnamedCount.incrementAndGet();
	}

	// Diagnostics
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final PositionalMechanismControllerData data;
	private final NetworkTableGrouping logTable;
	private String baseName;
	private String name;
	private String logPrefix;
	
	// Inner classes
	private IOGateway io;

	// State machine variables
	private boolean holdingPosition = false;
	private boolean hadGoalPositionBefore = false;
	private GoalState goalState = GoalState.WAITING_FOR_GOAL;
	private Position goalPosition = null;
	private StatusChecker goalStatus = null;
	
	// Internal fields
	private Set<IMotorController> motorList = new HashSet<>();
	private IMotorController mainMotor;
	private PositionConverter converter;
	private HoldingStrategy hold;
	
	// Configuration
	// TODO: Create setter
	private int pidIdx = PidIndex.PRIMARY.id;
	private int slotIdx = 0;
	private final RobotContext robotContext = RobotContext.getInstance();
	private CommonProperties props = robotContext.getCommonProps();


	public PositionalMechanismController(IMotorController... motors) {
		this(unnamed(), motors);
	}
	
	public PositionalMechanismController(String name, IMotorController... motors) {
		this(name, new NoOpPositionConverter(), motors);
	}
	
	public PositionalMechanismController(PositionConverter converter, IMotorController... motors) {
		this(unnamed(), converter, motors);
	}
	
	public PositionalMechanismController(String name, PositionConverter converter, IMotorController... motors) {
		this(name, converter, HoldType.DISABLE, motors);
	}
	
	public PositionalMechanismController(String name, PositionConverter converter, HoldType holdingStrategy, IMotorController... motors) {
		setName(name);
		setConverter(converter);
		add(motors);
		setHoldingStrategy(holdingStrategy);
		io = new IOGateway();
		logTable = new NetworkTableGrouping(StringUtil.toFileCase(baseName));
		data = new PositionalMechanismControllerData(this);
		robotContext.getDataRecorder().registerRecordable(data);
		autoRegisterWithPeriodicRunner(getName());
	}

	// Implementation Methods
	// ============================================================
	/** Call this if you would like to register a {@link SmartDashboardAwareRunner}. */
	public void registerSmartDashboardAware(SmartDashboardAwareRunner smartDashboardAwareRunner) {
		smartDashboardAwareRunner.registerInstance(this);
	}

	@Override
	public void updateSmartDashboard() {
		logTable.update();
	}
	
	@Override
	public void readSmartDashboard() {
	}
	

	// Public control
	// ============================================================
	
	/** Call this one time to move to a specific position. */
	// TODO: Add notes about how it interrupts what is currently happening, and listens for
	// interruptions on the StatusCheck, etc.
	public StatusChecker requestPosition(T goalPosition) {
		debug("Goal [{}] requested. Current position: [{}].", goalPosition, currentPosition());
		assertPreviousStatusCleanedUp();
		this.goalPosition = goalPosition.getPosition();
		goalState = GoalState.GOAL_REQUESTED;
		goalStatus = new StatusChecker();
		
		return goalStatus;
	}

	@Override
	public void onPeriodic() {

		io.periodic();
		double currPosition = currentPosition();
		boolean logPosition = true;
		boolean completeGoal = false;
		GoalState nextState = goalState;
		holdingPosition = false;

		// 1) Check if we have anything to process
		if (goalPosition == null) {
			nextState = GoalState.WAITING_FOR_GOAL;
			trace("[{}]: No new goals. Holding position [{}]", goalState, io.info.getHoldPosition());
			logPosition = false;
			if (hadGoalPositionBefore) {
				holdingPosition = true;
				io.holdPosition();
			}
			
		} else {

			double midPoint = goalPosition.midPoint;

			// Quick assertion check
			if(goalStatus == null) {
				// This will not be required once we incorporate the goal into the StatusChecker
				goalState = GoalState.ERROR;
				logPositon(currPosition);
				throw new IllegalStateException("goalStatus must not be null when goalPosition is not null!");


				// 2) Check if we're interrupted
			} else if (goalStatus.isInterrupted()) {
				nextState = GoalState.INTERRUPTED;
				io.info.setHoldPosition(currPosition);
				completeGoal = true;


				// 3) Check if we have initialized the goal yet
			} else if(goalState == GoalState.GOAL_REQUESTED) {

				hadGoalPositionBefore = true;

				if(!positionAllowed(midPoint)) {
					warn("Blocked attempt to move to unsafe position [{}].", midPoint);
					nextState = GoalState.INVALID;
					completeGoal = true;

				} else if(goalPosition.withinBounds(currPosition)) {
					debug("Already within requested setpoint bounds. Ignoring request. Bounds; [{} - {}]. Current Position: [{}]."
							, goalPosition.lowerBound, goalPosition.upperBound, currPosition);
					nextState = GoalState.COMPLETED;
					io.info.setHoldPosition(midPoint);
					completeGoal = true;

				} else {
					nextState = GoalState.INITIALIZING_GOAL;
					io.requestPosition(midPoint);
				}


				// 4) Check if goal position reached
			} else if (goalPosition.withinOrPastBounds(currPosition, getStartingPosition())) {
				nextState = GoalState.COMPLETED;
				io.info.setHoldPosition(midPoint, true);
				completeGoal = true;


				// 5) None of the above were true. Start/continue moving towards goal position
			} else {
				nextState = GoalState.PROCESSING_GOAL;
				movePeriodic();
			}
		}


		if(logPosition)
			logPositon(currPosition);

		goalState = nextState;
		
		if(completeGoal)
			completeGoal();
	}

	// Public informational methods
	// ============================================================
	public double currentPosition() {
		return io.info.currentPosition();
	}
	
	public double currentTics() {
		return io.info.currentTics();
	}

	// Protected methods
	// ============================================================
	protected void add(IMotorController motorController) {
		Assert.assertNotNull(motorController, "motorController");
		motorList.add(motorController);
		if(mainMotor == null)
			setMainMotor(motorController);
	}
	
	protected void add(List<IMotorController> motorControllerList) {
		Assert.assertNotNull(motorControllerList, "motorControllerList");
		motorControllerList.forEach(motor -> add(motor));
	}
	
	protected void add(IMotorController... motorControllers) {
		Assert.assertNotNull(motorControllers, "motorControllers");
		if (motorControllers.length < 1)
			throw new IllegalArgumentException(String.format(
					"Must pass at least one motor controller into [%s]", getClass().getSimpleName()));
		add(Arrays.asList(motorControllers));
	}
	
	protected void doToMotors(Consumer<IMotorController> function) {
		motorList.forEach(mtr -> function.accept(mtr));
	}
	
	protected IMotorController getMainMotor() {
		return mainMotor;
	}
	
	protected void setMainMotor(IMotorController motor) {
		Assert.assertNotNull(motor, "motor");
		this.mainMotor = motor;
		// No worry of duplicates (motorList is a set)
		motorList.add(motor);
	}

	// Subclass optional methods
	// ============================================================
	
	/**
	 * Subclasses may optionally override this method to add custom checks (beyond
	 * the positional checks already included) to mark a goal as complete (for
	 * example, hitting a banner sensor). This is called repeatedly while the goal
	 * is processing.
	 */
	protected boolean goalComplete(StatusChecker goalStatus, GoalState state, double currPosition) {
		return false;
	}

	/**
	 * Subclasses may optionally override this method to verify we are not trying to
	 * move to a position that is unsafe. This is called before the goal is started.
	 */
	protected boolean positionAllowed(double goalPosition) {
		return true;
	}
	
	protected double getStartingPosition() {
		return io.info.getStartingPosition();
	}
	
	protected void movePeriodic() {
		io.movePeriodic();
	}
	
	/**
	 * Check if the mechanism is safe to move to the requested position. If not, start/continue
	 * executing the steps to get to a safe position.
	 * <p>
	 * This is called repeatedly while attempting to move to a position.
	 * 
	 * @return Whether it is safe to start moving to the requested position.
	 */
	protected boolean resolveUnsafeMoveConditions(double requestedPosition) {
		return true;
	}

	/**
	 * Called once before attempting to move to a position.
	 */
	protected void prepareToMove(double requestedPosition) {
	}
	
	// Subclass required methods
	// ============================================================
	

	// Private methods
	// ============================================================
	private void logPositon(double currPosition) {
		debug("[{}]: [{}] -> [{}]. Goal: [{}].", goalState, currPosition, goalPosition.midPoint, goalPosition);
	}
	
	private void warn(String msg, Object... args) {
		logger.warn(logPrefix + msg, args);
	}
	
	private void info(String msg, Object... args) {
		logger.info(logPrefix + msg, args);
	}
	
	private void debug(String msg, Object... args) {
		logger.debug(logPrefix + msg, args);
	}
	
	private void trace(String msg, Object... args) {
		logger.trace(logPrefix + msg, args);
	}
	
	private void completeGoal() {
		goalStatus.complete();
		goalStatus = null;
		goalPosition = null;
	}

	/** If this fails, you broke my code. Thanks. */
	private void assertPreviousStatusCleanedUp() {
		if(goalPosition != null)
			throw new IllegalStateException("Goal position was not properly set to null before another goal was requested. "
					+ "Was original StatusChecker not interrupted or completed properly? "
					+ "Position: [" + goalPosition + "]. "
					+ "goalStatus: [" + goalStatus + "]. ");
		
		if(goalStatus != null)
			throw new IllegalStateException("Goal status was not properly set to null before another goal was requested. "
					+ "Was original StatusChecker not interrupted or completed properly? "
					+ "Completed: [" + goalStatus.isComplete() + "]. "
					+ "Interrupted: [" + goalStatus.isInterrupted() + "].");
	}


	// Inner classes
	// ============================================================
	class IOGateway {
		
		private Info info = new Info();
		
		public IOGateway() {
			info.updateCurrentPosition();
			double initialHoldPosition = info.currentPosition();
			info("Setting initial hold position to [{}].", initialHoldPosition);
			this.info.setHoldPosition(initialHoldPosition);
		}

		/** Meant to be called continuously regardless of the current state. */
		public void periodic() {
			info.updateCurrentPosition();
			info.updateHoldPosition();
		}
		
		/** Call this one time before calling {@link #movePeriodic()} continuously. */
		public void requestPosition(double position) {
			double currentPosition = io.info.currentPosition();
			debug("Position [{}] requested. Setting starting position to current position: [{}].", position, currentPosition);
			io.info.setStartingPosition(currentPosition);
			io.info.setRequestedPosition(position);
			doToMotors(motor -> motor.selectProfileSlot(slotIdx, pidIdx));
			prepareToMove(position);
		}

		/** Moves to a set point. Called continuously after first calling {@link #requestPosition(double)} */	
		public void movePeriodic() {
			boolean safeToMove = resolveUnsafeMoveConditions(io.info.getRequestedPosition());
		
			if(safeToMove)
				moveToPosition(io.info.getRequestedPosition());
		}

		public void holdPosition() {
			hold.holdPosition();
		}
		
		/**
		 * Converts the given position (height/angle) to tics and then moves the motor to the converted value.
		 * @param position The <u><b>unconverted</b></u> height/angle to move to.
		 */
		public void moveToPosition(double position) {
			int tics = converter.asTics(position);
			trace("Moving to position [{}:{}].\t Current position: [{}].", io.info.getRequestedPosition(), tics, io.info.currentPosition());
			doToMotors(motor -> motor.set(ControlMode.Position, tics));
		}
		
		private void stopMotors() {
			
		}
		
		class Info {

			private double requestedPosition;
			private boolean enabled = false;
			private boolean enabledPrevIteration = false;
			
			/** Cache the value of current position. Retrieved once per periodic. */
			private double currentPosition;
			
			/** The Position recorded at the start of a setpoint. */
			private double startingPosition;

			/** Returns the current cached tics. See {@link #updateCurrentPosition()} for more info. */
			public int currentTics() {
				return converter.asTics(currentPosition());
			}

			/** Returns the current cached position. See {@link #updateCurrentPosition()} for more info. */
			public double currentPosition() {
				return currentPosition;
			}
			
			/** To be called once per periodic to cache the current position. This reduces CAN bus utilization leading to less CAN errors. */
			public void updateCurrentPosition() {
				currentPosition = converter.asPosition(getMainMotor().getSelectedSensorPosition(pidIdx));
			}
			
			public double getRequestedPosition() {
				return requestedPosition;
			}

			public void setRequestedPosition(double requestedPosition) {
				this.requestedPosition = requestedPosition;
			}

			public double getStartingPosition() {
				return startingPosition;
			}

			public void setStartingPosition(double startingPosition) {
				this.startingPosition = startingPosition;
			}

			public double getHoldPosition() {
				return hold.getHoldPosition();
			}

			public void setHoldPosition(double holdPosition) {
				setHoldPosition(holdPosition, false);
			}

			public void setHoldPosition(double holdPosition, boolean goalCompleted) {
				String prependMsg = goalCompleted ? "Goal completed. " : "";
				debug("{}Setting hold position to [{}].", prependMsg, holdPosition);
				hold.setHoldPosition(holdPosition);
			}

			private void updateHoldPosition() {
				// TODO: Change to use robot event pattern
				enabled = robotContext.getDriverStation().isEnabled();
				
				if(enabled != enabledPrevIteration && enabled) {
					onEnabled();
				}
				enabledPrevIteration = enabled;
			}
			
			private void onEnabled() {
				double newPosition = currentPosition();
				debug("Robot enabled. Resetting hold position from [{}] to [{}].", getHoldPosition(), newPosition);
				setHoldPosition(newPosition);
			}
		}
	}
	
	private class NetworkTableGrouping {
		public static final int PRECISION = 5;
		
		private NetworkTableAdapter table;
		private NetworkTableEntryAdapter hadGoalEntry;
		private NetworkTableEntryAdapter goalStateEntry;
		private NetworkTableEntryAdapter goalStartPosEntry;
		private NetworkTableEntryAdapter goalReqPosEntry;
		private NetworkTableEntryAdapter goalNameEntry;
		private NetworkTableEntryAdapter goalStatusEntry;
		private NetworkTableEntryAdapter holdPosEntry;
		private NetworkTableEntryAdapter holdActiveEntry;
		private NetworkTableEntryAdapter currPosEntry;
		private NetworkTableEntryAdapter currTicsEntry;

		public NetworkTableGrouping(String baseTableName) {
			this(robotContext.getSmartDashboard().getTable("subsys").getSubTable(baseTableName).getSubTable("pos-ctrl"));
		}
		
		public NetworkTableGrouping(NetworkTableAdapter baseTable) {
			this.table = baseTable;
			hadGoalEntry = table.getEntry("had-goal");
			goalStateEntry = table.getEntry("goal-state");
			goalStartPosEntry = table.getEntry("goal-start-pos");
			goalReqPosEntry = table.getEntry("goal-req-pos");
			goalNameEntry = table.getEntry("goal-name");
			goalStatusEntry = table.getEntry("goal-status");
			
			holdPosEntry = table.getEntry("hold-pos");
			holdActiveEntry = table.getEntry("hold-active");
			
			currPosEntry = table.getEntry("curr-pos");
			currTicsEntry = table.getEntry("curr-tics");
		}
		
		public void update() {
			// TODO: Change this to use a flag specifically for Smartdashboard
			if(logger.isDebugEnabled()) {
				hadGoalEntry.setBoolean(hadGoalPositionBefore);
				goalStateEntry.setString(goalState == null ? null : goalState.toString());
				goalStartPosEntry.setNumber(Precision.round(io.info.getStartingPosition(), PRECISION));
				goalReqPosEntry.setNumber(Precision.round(io.info.getRequestedPosition(), PRECISION));
				goalNameEntry.setString(goalPosition == null ? null : goalPosition.toString());
				goalStatusEntry.setString(goalStatus == null ? null : goalStatus.toString());
				
				holdPosEntry.setNumber(Precision.round(io.info.getHoldPosition(), PRECISION));
				holdActiveEntry.setBoolean(holdingPosition);
				
				currPosEntry.setString(Precision.round(currentPosition(), PRECISION) + " " + converter.positionalUnit());
				currTicsEntry.setNumber(Precision.round(currentTics(), PRECISION));
			}
		}
	}

	public static class PositionalMechanismControllerData implements LifecycleAwareRecordable {

		private final PositionalMechanismController pmc;

		// pmc data
		private int goalStateOrdinal;
		private String goalStateEnum;
		private boolean holdingPosition;
		private boolean hadGoalPositionBefore;
		private double goalPositionLowerBound;
		private double goalPositionMidPoint;
		private double goalPositionUpperBound;
		private boolean statusCheckerNotNull;
		private boolean statusCheckerComplete;
		private boolean statusCheckerInterrupted;

		// io.info data
		private long tics;
		private double position;
		private double requestedPosition;
		private double startingPosition;
		private double holdPosition;


		public PositionalMechanismControllerData(PositionalMechanismController pmc) {
			this.pmc = pmc;
		}

		@Override
		public void onBeforeRecord() {
			goalStateOrdinal = pmc.goalState.ordinal();
			goalStateEnum = pmc.goalState.toString();
			holdingPosition = pmc.holdingPosition;
			hadGoalPositionBefore = pmc.hadGoalPositionBefore;
			Position goalPosition = pmc.goalPosition;
			goalPositionLowerBound = goalPosition == null ? 0 : goalPosition.lowerBound;
			goalPositionMidPoint = goalPosition == null ? 0 : goalPosition.midPoint;
			goalPositionUpperBound = goalPosition == null ? 0 : goalPosition.upperBound;
			statusCheckerNotNull = pmc.goalStatus != null;
			statusCheckerComplete = statusCheckerNotNull ? pmc.goalStatus.isComplete() : false;
			statusCheckerInterrupted = statusCheckerNotNull ? pmc.goalStatus.isInterrupted() : false;

			Info info = pmc.io.info;
			tics = info.currentTics();
			position = info.currentPosition();
			requestedPosition = info.getRequestedPosition();
			startingPosition = info.getStartingPosition();
			holdPosition = info.getHoldPosition();
		}

	}

	public GoalState getGoalState() {
		return goalState;
	}

	// Getters/setters
	// ============================================================
	public PositionalMechanismController<T> setConverter(PositionConverter converter) {
		Assert.assertNotNull(converter, "converter");
		this.converter = converter;
		PositionConverter.assertConversions(converter);
		return this;
	}
	
	public String getName() {
		return this.name;
	}

	public PositionalMechanismController<T> setName(String name) {
		Assert.assertNotNull(name, "name");
		this.baseName = name.trim();
		this.name = baseName + " Pos Mech Ctrl";
		this.logPrefix = "[" + this.name + "]: ";
		
		return this;
	}

	public PositionalMechanismController<T> setPidIdx(PidIndex pidIdx) {
		this.pidIdx = pidIdx.id;
		return this;
	}

	public PositionalMechanismController<T> setSlotIdx(int slotIdx) {
		this.slotIdx = slotIdx;
		return this;
	}

	/**
	 * Used to provide your own custom {@link HoldingStrategy} as opposed to using some
	 * of the built in implementations used with {@link #setHoldingStrategy(HoldType)}.
	 */
	public PositionalMechanismController<T> setHoldingStrategy(HoldingStrategy customHoldingStrategy) {
		this.hold = customHoldingStrategy;
		return this;
	}

	/**
	 * Sets the behavior to occur when this {@link PositionalMechanismController} reaches its goal position
	 * and needs to hold in place. See {@link HoldType} for definitions.
	 */
	public PositionalMechanismController<T> setHoldingStrategy(HoldType holdingStrategy) {
		Assert.assertNotNull(holdingStrategy, "holdingStrategy");
		debug("Setting holding strategy to [{}].", holdingStrategy);

		switch (holdingStrategy) {
		case PID:
			setHoldingStrategy(new PIDHoldingStrategy(converter, motorList));
			break;
		case DISABLE:
			setHoldingStrategy(new DisabledHoldingStrategy(motorList));
			break;
		case COAST:
			setHoldingStrategy(new DisabledHoldingStrategy(NeutralMode.Coast, motorList));
			break;
		case BRAKE:
			setHoldingStrategy(new DisabledHoldingStrategy(NeutralMode.Brake, motorList));
			break;

		default:
			throw new IllegalArgumentException(String.format("Unsupported [%s] type [%s]."
					, HoldType.class.getSimpleName(), holdingStrategy));
		}
		
		return this;
	}

}
