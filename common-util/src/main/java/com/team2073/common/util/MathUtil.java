package com.team2073.common.util;

import org.apache.commons.lang3.Range;

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

	public static double pythagoreanTheoremHypotenuse(double a, double b){
		return Math.sqrt((a*a) + (b*b));
	}

	public static double pythagoreanTheoremLeg(double a, double c){
		if(c >= a) {
			return Math.sqrt((c * c) - (a * a));
		} else {
			return -1;
		}
	}

	public static double gridAngle(double centerX, double centerY, double pointX, double pointY){
		double xDiff = pointX - centerX;
		double yDiff = pointY - centerY;
		if(xDiff != 0) {
			double referenceAngle = MathUtil.degreesToRadians(Math.atan(Math.abs(yDiff) / Math.abs(xDiff)));
			if (xDiff < 0 && yDiff > 0) {
				return 180 - referenceAngle;
			} else if (xDiff < 0 && yDiff < 0) {
				return 180 + referenceAngle;
			} else if (xDiff > 0 && yDiff < 0) {
				return 360 - referenceAngle;
			} else if (yDiff == 0 && xDiff < 0){
				return 180;
			}
		} else if (yDiff < 0){
			return 270;
		}
		return 90;
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

	public static double wrap(double angle, double range, double center){
		double theta = angle;
		while (theta > center + range/2d) {
			theta -= range;
		}

		while (theta < center - range/2d) {
			theta += range;
		}
		return theta;
	}

	public static boolean isInRange(double value, double target, double offset) {
		Range<Double> range = Range.between(target - offset, target + offset);
		return range.contains(value);
	}

}
