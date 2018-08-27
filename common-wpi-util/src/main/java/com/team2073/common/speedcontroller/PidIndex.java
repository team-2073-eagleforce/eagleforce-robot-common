package com.team2073.common.speedcontroller;

public enum PidIndex {

	PRIMARY(0),
	SECONDARY(1);
	
	public final int id;
	
	private PidIndex(int id) {
		this.id = id;
	}
}
