package com.team2073.common.svc.camera;

public interface CameraMessageParser<T> {

	T parseMsg(String msg);
	
}
