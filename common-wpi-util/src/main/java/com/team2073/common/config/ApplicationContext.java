package com.team2073.common.config;

import java.util.ArrayList;
import java.util.List;

// TODO: change this to an interface and then implement it in unit test (and robot projects)
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
