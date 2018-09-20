package com.team2073.common.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public abstract class NumberUtil {

	private static DecimalFormat defaultFormat = new DecimalFormat("#.00000");

	public static String trim(double num) {
		return defaultFormat.format(num);
	}

	public static double round(double d, int decimalPlace) {
		BigDecimal bd = new BigDecimal(d);
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return bd.doubleValue();
	}
}
