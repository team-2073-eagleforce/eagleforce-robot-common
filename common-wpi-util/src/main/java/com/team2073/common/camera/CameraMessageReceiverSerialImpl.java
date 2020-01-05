package com.team2073.common.camera;

import com.team2073.common.assertion.Assert;
import com.team2073.common.datarecorder.model.DataPointIgnore;
import com.team2073.common.periodic.SmartDashboardAware;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;

public class CameraMessageReceiverSerialImpl implements CameraMessageReceiver, SmartDashboardAware {
	
	private static final String DEFAULT_SMARTDASHBOARD_KEY_PREFIX = "camera.message.";
	private static final int DEFAULT_CIRCUIT_BREAKER_MAX_RETRIES = 20;
	private static final double DEFAULT_CIRCUIT_BREAKER_MIN_DELAY = .03;
	private static final double DEFAULT_CIRCUIT_BREAKER_MAX_DELAY = 1;
	private static final double DEFAULT_CIRCUIT_BREAKER_MULTIPLIER = 2;
	private static final String DEFAULT_DEFAULT_MESSAGE = "";
	
	public enum State {
		NO_SERIAL_PORT_DEFINED,
		RECEIVING_MESSAGE,
		WAITING;
		
	}
	
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	// Customizable config
	@DataPointIgnore private String requestMessageCommand = null;
	@DataPointIgnore private double circuitBreakerMinDelay = DEFAULT_CIRCUIT_BREAKER_MIN_DELAY;
	@DataPointIgnore private double circuitBreakerMaxDelay = DEFAULT_CIRCUIT_BREAKER_MAX_DELAY;
	@DataPointIgnore private double circuitBreakerMultiplier = DEFAULT_CIRCUIT_BREAKER_MULTIPLIER;
	@DataPointIgnore private int circuitBreakerMaxRetries = DEFAULT_CIRCUIT_BREAKER_MAX_RETRIES;
	@DataPointIgnore private String defaultMessage = DEFAULT_DEFAULT_MESSAGE;
	@DataPointIgnore private String smartdashboardKeyPrefix = DEFAULT_SMARTDASHBOARD_KEY_PREFIX;
	
	// State
	private State state;
	private final SerialPort serialPort;
	private double circuitBreakerCurrentDelay = circuitBreakerMinDelay;
	private long circuitBreakerTotalDelay;
	
	public CameraMessageReceiverSerialImpl(SerialPort serialPort) {
		this.serialPort = serialPort;
		
		if (serialPort == null) {
			state = State.NO_SERIAL_PORT_DEFINED;
			logger.error("Camera serial port cannot be null. No Camera messages will be received!");
			return;
		}
		
		state = State.WAITING;
	}
	
	
	// ===============================================================================================================
	// CameraMessageReceiver methods
	// ===============================================================================================================
	
	@Override
	public String receiveMsg() {
		
		if(serialPort == null)
			return "";
		
		int tries = 1;
		state = State.RECEIVING_MESSAGE;
		LocalDateTime requestMessageStart = LocalDateTime.now();
		requestNextMessage();
		String msg = readNextMessage();
		
		// If the camera can't accept the string fast enough there needs to be a delay when requesting a message
		if (messageIsValid(msg)) {
			// We got the message first try, reduce the delay length
			circuitBreakerCurrentDelay = decrementCircuitBreakerDelayLength(circuitBreakerCurrentDelay);
			
		} else {
			
			while (maxCircuitBreakerTriesReached(tries) || !messageIsValid(msg)) {
				// We didn't get a message, wait and try again
				tries++;
				circuitBreakerDelay(circuitBreakerCurrentDelay);
				msg = readNextMessage();
				// Wait a little longer each time we don't get a message (circuit breaker pattern)
				circuitBreakerCurrentDelay = incrementCircuitBreakerDelayLength(circuitBreakerCurrentDelay);
				logger.trace("TEMP: Circuit Breaker Delay [{}].", circuitBreakerCurrentDelay);
			}
		}
		LocalDateTime requestMessageEnd = LocalDateTime.now();
		circuitBreakerTotalDelay = Duration.between(requestMessageStart, requestMessageEnd).toMillis();
		
		// Cant remember why we delay after we have received the message but ok
		circuitBreakerDelay(circuitBreakerCurrentDelay);
		logger.trace("Camera Message Receive Attempts [{}]. Total delay [{}]. Message: [{}]", tries, circuitBreakerCurrentDelay, msg);
		state = State.WAITING;
		
		return msg == null ? defaultMessage : msg;
	}
	
	
	// ===============================================================================================================
	// protected overridable methods
	// ===============================================================================================================
	
	protected void requestNextMessage() {
		if (requestMessageCommand != null)
			serialPort.writeString(requestMessageCommand);
	}
	
	protected String readNextMessage() {
		return serialPort.readString();
	}
	
	protected boolean maxCircuitBreakerTriesReached(int tries) {
		return tries > circuitBreakerMaxRetries;
	}
	
	protected boolean messageIsValid(String msg) {
		return !(msg == null || msg.isEmpty() || msg.equals("OK"));
	}
	
	protected void circuitBreakerDelay(double circuitBreakerDelay) {
		Timer.delay(circuitBreakerDelay);
	}
	
	protected double decrementCircuitBreakerDelayLength(double prevDaleyLength) {
		return Math.max(prevDaleyLength / circuitBreakerMultiplier, circuitBreakerMinDelay);
	}
	
	protected double incrementCircuitBreakerDelayLength(double prevDaleyLength) {
		return Math.min(prevDaleyLength * circuitBreakerMultiplier, circuitBreakerMaxDelay);
	}
	
	
	// ===============================================================================================================
	// SmartDashboardAware methods
	// ===============================================================================================================
	@Override
	public void updateSmartDashboard() {
		SmartDashboard.putString(smartdashboardKeyPrefix = "state", state.toString());
		SmartDashboard.putNumber(smartdashboardKeyPrefix + "circuitBreakerCurrentDelay", circuitBreakerCurrentDelay);
		SmartDashboard.putNumber(smartdashboardKeyPrefix + "circuitBreakerTotalDelay", circuitBreakerTotalDelay);
	}

	@Override
	public void readSmartDashboard() {
		// Do nothing
	}
	
	// ===============================================================================================================
	// Getters/Setters
	// ===============================================================================================================
	
	public String getRequestMessageCommand() {
		return requestMessageCommand;
	}
	
	public void setRequestMessageCommand(String requestMessageCommand) {
		Assert.assertNotNull(requestMessageCommand, "requestMessageCommand");
		SmartDashboard.putString(smartdashboardKeyPrefix + "requestMessageCommand", requestMessageCommand);
		this.requestMessageCommand = requestMessageCommand;
	}
	
	public double getCircuitBreakerMinDelay() {
		return circuitBreakerMinDelay;
	}
	
	public void setCircuitBreakerMinDelay(double circuitBreakerMinDelay) {
		Assert.assertNotNegative(circuitBreakerMinDelay, "circuitBreakerMinDelay");
		SmartDashboard.putNumber(smartdashboardKeyPrefix + "circuitBreakerMinDelay", circuitBreakerMinDelay);
		this.circuitBreakerMinDelay = circuitBreakerMinDelay;
	}
	
	public double getCircuitBreakerMaxDelay() {
		return circuitBreakerMaxDelay;
	}
	
	public void setCircuitBreakerMaxDelay(double circuitBreakerMaxDelay) {
		Assert.assertNotNegative(circuitBreakerMaxDelay, "circuitBreakerMaxDelay");
		SmartDashboard.putNumber(smartdashboardKeyPrefix + "circuitBreakerMaxDelay", circuitBreakerMaxDelay);
		this.circuitBreakerMaxDelay = circuitBreakerMaxDelay;
	}
	
	public double getCircuitBreakerMultiplier() {
		return circuitBreakerMultiplier;
	}
	
	public void setCircuitBreakerMultiplier(double circuitBreakerMultiplier) {
		Assert.assertNotNegative(circuitBreakerMultiplier, "circuitBreakerMultiplier");
		SmartDashboard.putNumber(smartdashboardKeyPrefix + "circuitBreakerMultiplier", circuitBreakerMultiplier);
		this.circuitBreakerMultiplier = circuitBreakerMultiplier;
	}
	
	public int getCircuitBreakerMaxRetries() {
		return circuitBreakerMaxRetries;
	}
	
	public void setCircuitBreakerMaxRetries(int circuitBreakerMaxRetries) {
		Assert.assertNotNegative(circuitBreakerMaxRetries, "circuitBreakerMaxRetries");
		SmartDashboard.putNumber(smartdashboardKeyPrefix + "circuitBreakerMaxRetries", circuitBreakerMaxRetries);
		this.circuitBreakerMaxRetries = circuitBreakerMaxRetries;
	}
	
	public String getDefaultMessage() {
		return defaultMessage;
	}
	
	public void setDefaultMessage(String defaultMessage) {
		Assert.assertNotNull(defaultMessage, "defaultMessage");
		SmartDashboard.putString(smartdashboardKeyPrefix + "defaultMessage", defaultMessage);
		this.defaultMessage = defaultMessage;
	}
	
	public String getSmartdashboardKeyPrefix() {
		return smartdashboardKeyPrefix;
	}
	
	public void setSmartdashboardKeyPrefix(String smartdashboardKeyPrefix) {
		Assert.assertNotNull(smartdashboardKeyPrefix, "smartdashboardKeyPrefix");
		if (!smartdashboardKeyPrefix.endsWith("."))
			smartdashboardKeyPrefix = smartdashboardKeyPrefix + ".";
		this.smartdashboardKeyPrefix = smartdashboardKeyPrefix;
	}
	
	public State getState() {
		return state;
	}
	
	public SerialPort getSerialPort() {
		return serialPort;
	}
	
	public double getCircuitBreakerCurrentDelay() {
		return circuitBreakerCurrentDelay;
	}
}
