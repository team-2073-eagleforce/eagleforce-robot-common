package com.team2073.common.svc.camera;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.wpi.first.wpilibj.Timer;

/**
 * 
 * @author Preston Briggs
 *
 * @param <T>
 *            The type of camera message this class is capable of handling. Must
 *            match the {@link CameraMessageParser} implementation that is
 *            passed into the constructor.
 */
public class CameraMessageService<T> {
	private static final String CAMERA_MSG_NULL_ERROR_LOG = 
			"An attempt to retrieve a camera message was made but a message has not yet been received. "
					+ "This will return a null camera message and thus a NullPointerException may occur. "
					+ "Either this method was called too early (the first camera message hasn't been received yet) "
					+ "or there was an error receiving the camera message. Consider using the constructor that contains "
					+ "an initial camera message to avoid receiving a null camera message.";
	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final CameraMessageParser<T> parser;
	private final CameraMessageReceiver receiver;
	private T lastMessage;
	private boolean cameraMsgNullMsgLogged = false;
	
	public CameraMessageService(CameraMessageParser<T> parser, CameraMessageReceiver receiver) {
		this.parser = parser;
		this.receiver = receiver;
		startThread();
	}
	
	public CameraMessageService(CameraMessageParser<T> parser, CameraMessageReceiver receiver, T initialMessage) {
		this(parser, receiver);
		this.lastMessage = initialMessage;
	}
	
	public T currentMessage() {
		if (lastMessage == null && !cameraMsgNullMsgLogged) {
			logger.warn(CAMERA_MSG_NULL_ERROR_LOG);
			cameraMsgNullMsgLogged = true;
		}
		return lastMessage;
	}
	
	private void startThread() {
		Thread thread = new Thread(() -> {
			while (!Thread.interrupted()) {
				String msg = "NEVER RECEIVED";
				try {
					msg = receiver.receiveMsg();
					if (!msg.isEmpty()) {
						lastMessage = parser.parseMsg(msg);
					}
				} catch(Exception e){
					logger.error("Exception while receiving camera message. Message: [{}]", msg, e);
				}
				Timer.delay(.005);
			}
		});
		thread.start();
	}
}
