package com.team2073.common.position.zeroer;

import com.ctre.phoenix.motorcontrol.IMotorController;
import com.ctre.phoenix.motorcontrol.SensorCollection;
import com.team2073.common.assertion.Assert;
import com.team2073.common.periodic.PeriodicRunnable;
import com.team2073.common.position.converter.NoOpPositionConverter;
import com.team2073.common.position.converter.PositionConverter;
import edu.wpi.first.wpilibj.DigitalInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.team2073.common.util.ClassUtil.*;

/**
 * Manages monitoring a zero sensor for zeroing 'events' and setting a corresponding motor controller's position.
 * Records the beginning and end of a zeroing 'session' and sets the zero using the middle of the range.
 *
 * <h3>Use</h3>
 * At the bare minimum this requires a {@link DigitalInput} to monitor and a {@link IMotorController}
 * to update on zero events (required by all constructors).
 * <p>
 * After that just call {@link #onPeriodic()} once per periodic iteration and zeroing will be 100% managed for you.
 *
 * <h3>Configuration</h3>
 * <p>
 * A few of the parameters callers may customize are:
 * <p>
 * <ul>
 * 	<li>Zero position offset: {@link #setOffsetInTics(int)} (int)}</li>
 * 	<li>pidIdx of the motor controller: {@link #setPidIdx(int)} </li>
 * 	<li>Inverted: {@link #setInverted(boolean)}</li>
 * </ul>
 *
 * <h3>Improved Logging</h3>
 * <p>
 * For more readable logging use:
 * <p>
 * <ul>
 * 	<li>{@link #setConverter(PositionConverter)}</li>
 * 	<li>{@link #setPositionUnit(String)}</li>
 * 	<li>{@link #setName(String)}</li>
 * </ul>
 *
 * <h3>Zeroing Event Listening</h3>
 * <p>
 * If access to the zero events is required (to add some additional logic upon zero), use {@link #setListener(ZeroEventListener)}.
 * <p>
 *
 *
 * @author Preston Briggs
 */
public class Zeroer implements PeriodicRunnable {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	// State
	/** We are currently in the process of zeroing. */
	private boolean zeroing = false;
	private Integer zeroStartPos = null;
	private Integer zeroEndPos = null;
	private Integer positionCache;
	/** The number of times we have started a zero. Does not necessarily mean we got to the other side, causing a zero event. */
	private long zeroStartCount;
	/** The number of times we have started and finished a zero, causing a zero event. */
	private long zeroTriggerCount;
	private long lastZeroTimestamp;

	// IO
	private final DigitalInput zeroSensor;
	private final IMotorController motor;

	// Optional
	private ZeroEventListener listener = new NoOpZeroEventListener();
	private SensorCollection zeroSensorCollection;
	private boolean isForwardLimit;

	// Customizable config
	private int offset;
	private int pidIdx = 0;
	private boolean inverted;
	private ZeroingStrategy strategy = ZeroingStrategy.EVERY_TIME;
	private ZeroingLocation zeroLocation = ZeroingLocation.MIDPOINT;

	// Optional logging config
	private String name = "UNNAMED";
	private String logPrefix;
	private String positionUnit = "";
	private PositionConverter converter = new NoOpPositionConverter();

	// Constructors
	// ============================================================

	// TODO: Change these constructors to call each other

	public Zeroer(DigitalInput zeroSensor, IMotorController motor) {
		this.zeroSensor = zeroSensor;
		this.motor = motor;
		setName(motor);
		autoRegisterWithPeriodicRunner(getName());
	}

	public Zeroer(DigitalInput zeroSensor, IMotorController motor,
			int offset, int pidIdx, boolean inverted) {

		this.zeroSensor = zeroSensor;
		this.motor = motor;
		this.offset = offset;
		this.pidIdx = pidIdx;
		this.inverted = inverted;
		setName(motor);
		autoRegisterWithPeriodicRunner(getName());
	}

	public Zeroer(DigitalInput zeroSensor, IMotorController motor, PositionConverter converter, String name) {
		this.zeroSensor = zeroSensor;
		this.motor = motor;
		setConverter(converter);
		setName(name);
		setPositionUnit(converter.positionalUnit());
		autoRegisterWithPeriodicRunner(getName());
	}

	public Zeroer(DigitalInput zeroSensor, IMotorController motor, PositionConverter converter, int offset,
			int pidIdx, boolean inverted) {

		this.zeroSensor = zeroSensor;
		this.motor = motor;
		setConverter(converter);
		this.offset = offset;
		this.pidIdx = pidIdx;
		this.inverted = inverted;
		setName(motor);
		autoRegisterWithPeriodicRunner(getName());
	}

	public Zeroer(DigitalInput zeroSensor, IMotorController motor, PositionConverter converter, int offset,
			int pidIdx, boolean inverted, String name) {

		this.zeroSensor = zeroSensor;
		this.motor = motor;
		setConverter(converter);
		this.offset = offset;
		this.pidIdx = pidIdx;
		this.inverted = inverted;
		setName(name);
		setPositionUnit(converter.positionalUnit());
		autoRegisterWithPeriodicRunner(getName());
	}

	public Zeroer(DigitalInput zeroSensor, IMotorController motor, PositionConverter converter,
			ZeroEventListener listener, int offset, int pidIdx, boolean inverted, String name) {

		this.zeroSensor = zeroSensor;
		this.motor = motor;
		setConverter(converter);
		this.listener = listener;
		this.offset = offset;
		this.pidIdx = pidIdx;
		this.inverted = inverted;
		setName(name);
		setPositionUnit(converter.positionalUnit());
		autoRegisterWithPeriodicRunner(getName());
	}

	public Zeroer(SensorCollection zeroSensor, IMotorController motor, PositionConverter converter, boolean isForwardLimit){
		this.zeroSensor = null;
		this.zeroSensorCollection = zeroSensor;
		this.motor = motor;
		setConverter(converter);
		this.isForwardLimit = isForwardLimit;
		setName(motor);
		autoRegisterWithPeriodicRunner(getName());

	}


	// Public methods
	// ============================================================
	public boolean atSensor() {
		if (zeroSensor != null) {
			return inverted ? zeroSensor.get() : !zeroSensor.get();
		} else if (isForwardLimit) {
			return zeroSensorCollection.isFwdLimitSwitchClosed();
		} else {
			return zeroSensorCollection.isRevLimitSwitchClosed();
		}
	}

	/**
	 * Call this method once per periodic loop.
	 */
	public void onPeriodic() {
		checkForNewZero();
		clearCache();
	}

	// Protected methods
	// ============================================================
	protected void zeroEncoder(int zeroTicsValue) {
		debug("Zeroing");
		motor.setSelectedSensorPosition(zeroTicsValue, pidIdx, 0);
	}

	/** Used to get the current position once per periodic loop to cache the value.
	 * Subclasses may override this to provide custom sensor readings however they
	 * should not use this to get the current position. Use {@link #currentTicsCached()}
	 * instead. */
	protected int currentTics() {
		return (int)motor.getSelectedSensorPosition(pidIdx);
	}

	protected final int currentTicsCached() {
		if(positionCache == null)
			positionCache = currentTics();

		return positionCache;
	}


	// Private methods
	// ============================================================
	private void checkForNewZero() {

		if(!zeroing && atSensor()) {
			// Detected a zero for the first time
			debug("Made contact with zero");
			zeroing = true;
			onZeroStart();

		} else if(zeroing && atSensor()) {
			// We have already detected this zero, ignore
			return;

		} else if(zeroing && !atSensor()) {
			// We lost contact with the zero, end this 'contact session'
			debug("Lost contact with zero");
			zeroing = false;
			onZeroEnd();
			zeroEncoder();
			return;

		} else {
			trace("Waiting for zero");
		}
	}
	private void onZeroStart() {
		zeroStartPos = currentTicsCached();
		zeroStartCount++;
	}

	private void onZeroEnd() {
		// TODO: Check if we went all the way through the zero or came back out the same side before finishing
		zeroEndPos = currentTicsCached();
	}

	private int findZeroLocation(){
		int zero;
		int currTics = currentTicsCached();
		switch(zeroLocation){
			case MIDPOINT:
				int middleZero = (zeroStartPos + zeroEndPos) / 2;
				zero = currTics - middleZero;
				break;
			case RISING_EDGE:
				zero = currTics - zeroStartPos;
				break;
			case FALLING_EDGE:
				zero = currTics - zeroEndPos;
				break;
			case TOP_SIDE:
				int max = Math.max(zeroStartPos, zeroEndPos);
				zero = currTics - max;
				break;
			case BOTTOM_SIDE:
				int min = Math.min(zeroStartPos, zeroEndPos);
				zero = currTics - min;
				break;
			default:
				throw new IllegalStateException("state [" + zeroLocation + "] has not been implemented.");
		}
		return zero;
	}

	private void zeroEncoder() {
		if(zeroStartPos == null || zeroEndPos == null) {
			warn("Error zeroing [{}] zeroer. Start or end position was not set. Start [{} tics] End: [{} tics]", name, zeroStartPos, zeroEndPos);
			reset();
			return;
		}

		if (strategy == ZeroingStrategy.INITIAL_ONLY && zeroTriggerCount > 0) {
			debug("Zeroing strategy set to [{}]. Ignoring zero.", strategy);
			reset();
			return;
		}

		int zeroPosition = findZeroLocation();
		int currTics = currentTicsCached();
		int zeroWithOffset = offset + zeroPosition;


		String offsetMsg;
		String zeroMsg;
		String zeroWithOffsetMsg;
		String currPosMsg;
		String startMsg;
		String middleMsg;
		String endMsg;

		// TODO: Change this to use a method that converts into loggable String
		// If converter available, this method will convert and append positionUnit.
		// If not, just append tics and return
		// ...or maybe just setup a default converter that converts to tics (aka does nothing)
		if(converter != null) {
			offsetMsg = converter.asPosition(offset) + "";
			zeroMsg = converter.asPosition(zeroPosition) + positionUnit;
			zeroWithOffsetMsg = converter.asPosition(zeroWithOffset) + "";
			currPosMsg = converter.asPosition(currTics) + positionUnit;
			startMsg = converter.asPosition(zeroStartPos) + "";
			endMsg = converter.asPosition(zeroEndPos) + "";
		} else {
			offsetMsg = offset + "";
			zeroMsg = zeroPosition + " tics";
			zeroWithOffsetMsg = zeroWithOffset + "";
			currPosMsg = currTics + " tics";
			startMsg = zeroStartPos + "";
			endMsg = zeroEndPos + "";
		}

		if(logger.isTraceEnabled()) {
			logger.debug("Zeroing [{}] encoder to [{}]. Current pos: [{}]. Zero band: [{} -- {}]. Offest calc: [{} + {} = {}]"
					, name, zeroWithOffsetMsg, currPosMsg, startMsg, endMsg, zeroMsg, offsetMsg, zeroWithOffsetMsg);
		} else {
			logger.debug("Zeroing [{}] encoder to [{}]. Current pos: [{}]."
					, name, zeroWithOffsetMsg, currPosMsg);
		}

		// TODO: Pass some info to the listener? Such as current pos, start/end zero, etc.
		listener.onBeforeZero();
		zeroEncoder(zeroWithOffset);
		zeroTriggerCount++;
		reset();
	}

	private void clearCache() {
		positionCache = null;
	}

	private void reset() {
		zeroEndPos = null;
		zeroStartPos = null;
	}

	private double currentPosition() {
		if(converter == null) {
			throw new IllegalArgumentException("[" + name + "] zeroer: Cannot get current position when converter is null. "
					+ "Use currentTics or supply a PositionConverter implementation.");
		}

		return converter.asPosition(currentTicsCached());
	}


	// Logging methods
	// ============================================================
	private void debug(String msg) {
		logger.debug("[{}] zeroer: {}. {}", name, msg, positionMsg());
	}

	private void trace(String msg) {
		if(!logger.isTraceEnabled())
			return;

		logger.trace("[{}] zeroer: {}. {}", name, msg, positionMsg());
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

	private String positionMsg() {
		// Prefer logging position, fallback to logging tics
		if(converter != null)
			return "Position: [" + currentPosition() + "].";
		else
			return "Tics: [" + currentTicsCached() + "].";

	}


	// Getters/setters
	// ============================================================
	/**
	 * Register an event listener to receive events upon zeroing
	 */
	public Zeroer setListener(ZeroEventListener listener) {
		Assert.assertNotNull(listener, "listener");
		this.listener = listener;
		return this;
	}

	/**
	 * The amount in tics to offset the zero by when setting. Use this if this zero sensor is not
	 * at a 'zero' position.
	 */
	public Zeroer setOffsetInTics(int offsetTics) {
		this.offset = offsetTics;
		return this;
	}

	/**
	 * The amount in position (degrees, inches, etc.) to offset the zero by when setting. Use this if this zero sensor is not
	 * at a 'zero' position.
	 * <p>
	 * <b>Must first set the converter using {@link #setConverter(PositionConverter)} or an {@link IllegalArgumentException}
	 * will be thrown!</b> (Can't convert from positional units to tics without a converter implementation)
	 */
	public Zeroer setOffsetInUnits(double offsetUnits) {
		if(converter == null)
			throw new IllegalArgumentException("Cannot set offset in positional value when converter has not been set. "
					+ "Either set a converter or set the offset in tics.");

		this.offset = converter.asTics(offsetUnits);
		return this;
	}

	/**
	 * The {@link #pidIdx} to use when reading/setting sensor position.
	 * <p>
	 * 0 for Primary closed-loop. 1 for cascaded closed-loop. See Phoenix-Documentation for how to interpret.
	 *
	 * @see IMotorController#getSelectedSensorPosition(int)
	 * @see IMotorController#setSelectedSensorPosition(double, int, int)
	 */
	public Zeroer setPidIdx(int pidIdx) {
		this.pidIdx = pidIdx;
		return this;
	}

	/** Reverse the reading of the zero sensor. False by default which results in
	 * calling !{@link DigitalInput#get()} */
	public Zeroer setInverted(boolean inverted) {
		this.inverted = inverted;
		return this;
	}
	/**
	 * Optional setting.
	 * <p>
	 * Set a converter to convert between tics and position (degrees, inches, etc.). Only used
	 * to produce more readable logs. Recommended to also set {@link Zeroer#setName(String)} and
	 * {@link Zeroer#setPositionUnit(String)} if using this.
	 *
	 * @see Zeroer#setPositionUnit(String)
	 * @see Zeroer#setName(String)
	 */
	public Zeroer setConverter(PositionConverter converter) {
		Assert.assertNotNull(converter, "converter");
		this.converter = converter;
		setPositionUnit(converter.positionalUnit());
		PositionConverter.assertConversions(converter);
		return this;
	}

	public String getName() {
		return name;
	}

	public Zeroer setName(IMotorController motor) {
		return setName(simpleName(this) + "[Motor:" + motor.getBaseID() + "]");
	}

	/**
	 * Optional setting.
	 * <p>
	 * Used for logging. Do not include "Zero" or "Zeroer", that is included in the log messages already.
	 *
	 * @see Zeroer#setConverter(PositionConverter)
	 * @see Zeroer#setPositionUnit(String)
	 * @param name The name to give this {@link Zeroer}
	 */
	public Zeroer setName(String name) {
		Assert.assertNotNull(name, "name");
		this.name = name;
		this.logPrefix = "[" + this.name + "]: ";
		return this;
	}

	/**
	 * Optional setting.
	 * <p>
	 * Set the unit of measurement for position (degrees, inches, cm, etc.).
	 * Used for logging.
	 *
	 * @see Zeroer#setConverter(PositionConverter)
	 * @see Zeroer#setName(String)
	 */
	private void setPositionUnit(String positionUnit) {
		Assert.assertNotNull(positionUnit, "positionUnit");
		this.positionUnit = " " + positionUnit.trim();
	}

	/** @see ZeroingStrategy */
	public ZeroingStrategy getStrategy() {
		return strategy;
	}

	/** @see ZeroingStrategy */
	public void setStrategy(ZeroingStrategy strategy) {
		Assert.assertNotNull(strategy, "strategy");
		this.strategy = strategy;

	}

	public void setZeroLocation(ZeroingLocation zeroLocation) {
		Assert.assertNotNull(zeroLocation, "zeroLocation");
		this.zeroLocation = zeroLocation;
	}


	// Inner classes
	public enum ZeroingStrategy {

		/** Zero sensor every time a zero is observed. */
		EVERY_TIME,

		/** Zero sensor on the first zero observed only. Ignore all subsequent zero events. */
		INITIAL_ONLY,

	}

	/**
	 * The edge of the sensor where you would like to zero to, midpoint is default,
	 * {@link ZeroingStrategy} should be used to specify when to zero
	 */
	public enum ZeroingLocation {

		/** Zero sensor to the center of the mechanism*/
		MIDPOINT,

		/** Zero sensor to the first edge of the sensor found */
		RISING_EDGE,

		/** Zero sensor to the last edge of the sensor found */
		FALLING_EDGE,

		/** Zero sensor to the top edge of the sensor (top is relative to the sensor readings,
		 *  where top is the point that is hit first when traveling in a negative direction).
		 *  This won't change depending on direction of the motion.
		 */
		TOP_SIDE,

		/**
		 * Zero sensor to the bottom edge of the sensor (bottom is relative to the sensor readings,
		 * where "bottom" is the point that is hit first when traveling in a positive direction).
		 * This won't change depending on direction of the motion.
		 */
		BOTTOM_SIDE
	}

	// ============================================================
	private static class NoOpZeroEventListener implements ZeroEventListener {

		@Override
		public void onBeforeZero() {
		}

	}

}
