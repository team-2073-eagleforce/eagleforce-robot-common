package com.team2073.common.dev.config;

import java.util.ArrayList;
import java.util.List;

public class ApplicationContext {
	private List<String> activeProfiles = new ArrayList<>();

	public ApplicationContext() {
		activeProfiles.add("mainbot");
//		activeProfiles.add("practicebot");
	}

	public List<String> getActiveProfiles() {
		return activeProfiles;
	}
}
