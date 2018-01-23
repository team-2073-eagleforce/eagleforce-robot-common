package com.team2073.common.util;

import org.json.JSONException;
import org.json.JSONObject;

import com.team2073.common.domain.CameraMessage;

public class CameraMessageParser {
	private static final String TRACKING_JSON_KEY = "Trk";
	private static final String TIME_JSON_KEY = "Tm";
	private static final String DEGREES_JSON_KEY = "Deg";
	private static final String DISTANCE_JSON_KEY = "Dist";

	public static CameraMessage parseJson(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			CameraMessage message = new CameraMessage();
			message.setDistanceToTarget(jsonObject.getDouble(DISTANCE_JSON_KEY));
			message.setAngleToTarget(jsonObject.getDouble(DEGREES_JSON_KEY));
			message.setTimeOfImage(jsonObject.getDouble(TIME_JSON_KEY));
			message.setTracking(jsonObject.getBoolean(TRACKING_JSON_KEY));
			return message;
		} catch (JSONException e) {
			e.printStackTrace();
			return new CameraMessage();
		}
	}
}
