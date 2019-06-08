package com.team2073.common.util;

public abstract class MathUtil {

	public static double degreeCosine(double degrees) {
		return Math.cos(degreesToRadians(degrees));
	}

	public static double degreeSine(double degrees) {
		return Math.sin(degreesToRadians(degrees));
	}

	public static double degreeTangent(double degrees) {
		return Math.tan(degreesToRadians(degrees));
	}

	public static double degreeArcTangent(double input) {
		return radiansToDegrees(Math.atan(input));
	}

	public static double degreeArcSine(double input) {
		return radiansToDegrees(Math.asin(input));
	}

	public static double degreeArcCosine(double input) {
		return radiansToDegrees(Math.acos(input));
	}

	public static double radiansToDegrees(double rad) {
		return (180. / Math.PI) * rad;
	}

	public static double degreesToRadians(double degrees) {
		return degrees * (Math.PI / 180.);
	}

	public static double average(double... inputs) {
		double total = 0;
		for (double input : inputs) {
			total = total + input;
		}

		return total / inputs.length;
	}

	public static double map(double x, double in_min, double in_max, double out_min, double out_max) {
		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}

}
