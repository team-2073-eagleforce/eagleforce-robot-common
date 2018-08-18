package com.team2073.common.util;

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
		
		return str.trim().toLowerCase().replaceAll(" +", " ").replaceAll(" ", "-");
	}
}