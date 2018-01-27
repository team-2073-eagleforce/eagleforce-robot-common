package com.team2073.common.util;

import org.json.JSONException;
import org.json.JSONObject;

import com.team2073.common.domain.CameraMessage;

public class CameraMessageParser {
	private static final String ARUCO_ID_JSON_KEY = "ArID";
	private static final String ARUCO_ALIGN_JSON_KEY = "ArAlign";
	private static final String ARUCO_DISTANCE_JSON_KEY = "ArDist";
	private static final String CUBE_ALIGN_JSON_KEY = "CbAlign";
	private static final String CUBE_DISTANCE_JSON_KEY = "CbDist";
	private static final String CUBE_TRACK_JSON_KEY = "CbTrk";
	private static final String TIMER_JSON_KEY = "Timer";
	

	public static CameraMessage parseJson(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			CameraMessage message = new CameraMessage();
			
			message.setArAlign(jsonObject.getDouble(ARUCO_ALIGN_JSON_KEY));
			message.setArDist(jsonObject.getDouble(ARUCO_DISTANCE_JSON_KEY));
			message.setArID(jsonObject.getInt(ARUCO_ID_JSON_KEY));
			message.setCbAlign(jsonObject.getDouble(CUBE_ALIGN_JSON_KEY));
			message.setCbDist(jsonObject.getDouble(CUBE_DISTANCE_JSON_KEY));
			message.setCbTrk(jsonObject.getBoolean(CUBE_TRACK_JSON_KEY));
			message.setTimer(jsonObject.getDouble(TIMER_JSON_KEY));
			return message;
		} catch (JSONException e) {
			e.printStackTrace();
			return new CameraMessage();
		}
	}
}
