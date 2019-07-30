package com.team2073.common.util;

import static java.lang.Math.PI;

public abstract class ConversionUtil {

	public static String humanReadableByteCount(long bytes) {
		return humanReadableByteCount(bytes, true);
	}

	public static String humanReadableByteCount(long bytes, boolean si) {
		int unit = si ? 1000 : 1024;
		if (bytes < unit)
			return bytes + " B";
		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
		return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

	public static double msToSeconds(int timeInMs) {
		return timeInMs * 0.001;
	}

	public static double msToSeconds(long timeInMs) {
		return timeInMs * 0.001;
	}

	public static double microSecToSec(long timeInMicroSec) {
		return timeInMicroSec * (1 / 10e6);
	}

	public static long secToMicroSec(double timeInSec) {
		return (long) (timeInSec * 10e6);
	}

	public static long secondsToMs(double timeInSeconds) {
		return (long) (timeInSeconds * 1000);
	}

	public static double kgToLb(double kg) {
		return kg * 2.20462;
	}

	public static double lbToKg(double lb) {
		return lb * 0.453592;
	}

	public static double metersToInches(double meters) {
		return meters * 39.3701;
	}

	public static double inchesToMeters(double meters) {
		return meters * 0.0254;
	}

	public static double radiansToDegrees(double radians) {
		return radians * (180d / PI);
	}

	public static double degreesToRadians(double degrees) {
		return degrees * (PI / 180d);
	}
}
