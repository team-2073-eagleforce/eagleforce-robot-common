package com.team2073.common.svc.camera;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.usfirst.frc.team2073.robot.ctx.SerialPortProvider;
import org.usfirst.frc.team2073.robot.domain.CameraMessage;

import com.team2073.common.inject.CheckedProviderUtils;
import com.team2073.common.smartdashboard.SmartDashboardAware;
import com.team2073.common.smartdashboard.SmartDashboardAwareRegistry;

import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class CameraMessageReceiverSerialImpl implements CameraMessageReceiver, SmartDashboardAware {
	public static final String STATE_SMARTDASHBOARD_KEY = "Camera Message Receiver State";
	private String stateSmartdashboardKey = STATE_SMARTDASHBOARD_KEY;

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private State state = State.CONSTRUCTING;
	private SerialPort serialPort;
	
	enum State {
		CONSTRUCTING,
		NO_SERIAL_PORT_DEFINED,
		RECEIVING_MESSAGE,
		WAITING
	}
	
	public CameraMessageReceiverSerialImpl(SerialPort serialPort) {
		if (serialPort == null) {
			state = State.NO_SERIAL_PORT_DEFINED;
			logger.error("Camera serial port cannot be null. No Camera messages will be received!");
			return;
		}
		
		this.serialPort = serialPort;
		state = State.WAITING;
	}
	
	public CameraMessageReceiverSerialImpl(SerialPortProvider serialPortProvider, SmartDashboardAwareRegistry smartDashboardAwareRegistry) {
		this(CheckedProviderUtils.getOrNull(serialPortProvider));
		smartDashboardAwareRegistry.registerInstance(this);
	}

	// CameraMessageReceiver methods
	// ====================================================================================================
	@Override
	public String receiveMsg() {
		int attempts = 1;
		if(serialPort == null)
			return "";
		state = State.RECEIVING_MESSAGE;
		serialPort.writeString(CameraMessage.REQUEST_MESSAGE);
		String json = serialPort.readString();
		
		//The JeVois can't accept the string fast enough so there needs to be a delay when requesting a message
		while (json == null || json.isEmpty() || json.trim().equals("OK")) {
			attempts++;
			Timer.delay(.005);
			json = serialPort.readString();
		}
		logger.trace("JSON Receive Attempts [{}]: {}", attempts, json);
		state = State.WAITING;
		return json == null ? "" : json;
	}
	
	// SmartDashboardAware methods
	// ====================================================================================================
	@Override
	public void updateSmartDashboard() {
		SmartDashboard.putString(stateSmartdashboardKey, state.toString());
	}

	@Override
	public void readSmartDashboard() {
		// Do nothing
	}
	
	// Getters/Setters
	// ====================================================================================================
	public String getStateSmartdashboardKey() {
		return stateSmartdashboardKey;
	}

	public void setStateSmartdashboardKey(String stateSmartdashboardKey) {
		this.stateSmartdashboardKey = stateSmartdashboardKey;
	}
}
