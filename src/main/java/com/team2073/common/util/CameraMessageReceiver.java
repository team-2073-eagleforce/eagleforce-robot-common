package com.team2073.common.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.team2073.common.domain.CameraMessage;

import edu.wpi.first.wpilibj.SerialPort;

public class CameraMessageReceiver {
	private static final Object INSTANCE_LOCK = new Object();
	private static CameraMessageReceiver instance = null;
	private static SerialPort serialPort;
	
	private CameraMessage lastMessage = new CameraMessage();
	
	private static CameraMessageReceiver getInstance(SerialPort serialPort ) {
		synchronized (INSTANCE_LOCK) {
			if (instance == null) {
				instance = new CameraMessageReceiver();
			}
		}
		return instance;
	}
	
	private CameraMessageReceiver() {
		// only allow one thread to be created
		Thread thread = new Thread(() -> {
			while (!Thread.interrupted()) {
				try {
					String json = serialPort.readString();
					json = json.endsWith("\n") ? json.substring(0, json.length() -1) : json;
					if(!json.isEmpty())
						lastMessage = CameraMessageParser.parseJson(json);
				} catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}
	
	public static CameraMessage getLastMessage() {
		return getInstance(serialPort).lastMessage;
	}
}
