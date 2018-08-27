package com.team2073.common.inject;

import com.google.inject.throwingproviders.CheckedProvider;

public class CheckedProviderUtils {
	public static <T> T getOrNull(CheckedProvider<T> provider) {
		try {
			return provider.get();
		} catch (Exception e) {
			return null;
		}
	}
}
