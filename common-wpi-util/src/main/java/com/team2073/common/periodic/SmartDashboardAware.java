package com.team2073.common.periodic;

public interface SmartDashboardAware {

	void updateSmartDashboard();

	default void readSmartDashboard() {
		// do nothing
	}

}
