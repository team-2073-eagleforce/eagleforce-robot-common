package com.team2073.common.util;

import java.text.DecimalFormat;

public abstract class NumberUtil {
	private static DecimalFormat defaultFormat = new DecimalFormat("#.00000");

	public static String trim(double num) {
		return defaultFormat.format(num);
	}
}
