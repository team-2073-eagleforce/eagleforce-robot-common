package com.team2073.common.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.team2073.common.domain.CameraMessage;

public class CameraMessageReceiver {
	private static final int RECEIVE_PORT = 2073;
	private static final int RECEIVE_BUFFER_SIZE = 300;
	
	private static final Object INSTANCE_LOCK = new Object();
	private static CameraMessageReceiver instance = null;
	
	private CameraMessage lastMessage = new CameraMessage();
	
	private static CameraMessageReceiver getInstance() {
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
				DatagramSocket socket = null;
				try {
					socket = new DatagramSocket(RECEIVE_PORT);
					DatagramPacket packet = new DatagramPacket(new byte[RECEIVE_BUFFER_SIZE], RECEIVE_BUFFER_SIZE);
					socket.receive(packet);
					String json = new String(packet.getData());
					lastMessage = CameraMessageParser.parseJson(json);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (socket != null) {
						socket.close();
					}
				}
			}
		});
		thread.start();
	}
	
	public static CameraMessage getLastMessage() {
		return getInstance().lastMessage;
	}
}
