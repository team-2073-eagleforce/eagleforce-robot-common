package com.team2073.common.camera;

public interface CameraMessageParser<T> {

	T parseMsg(String msg);
	
}
