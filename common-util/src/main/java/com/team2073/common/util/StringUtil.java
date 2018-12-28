package com.team2073.common.util;

import com.google.common.base.CaseFormat;

public abstract class StringUtil {

	public static boolean isEmpty(String str) {
		if(str == null || str.isEmpty())
			return true;
		else
			return false;
	}
	
	public static String toFileCase(String str) {
		if(str == null)
			return null;

		str = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, str);
		return str.trim().toLowerCase().replaceAll(" +", " ").replaceAll(" ", "-");
	}
}