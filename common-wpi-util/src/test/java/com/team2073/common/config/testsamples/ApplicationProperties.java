package com.team2073.common.config.testsamples;

import com.team2073.common.config.PropertyContainer;

import javax.validation.constraints.NotNull;

@PropertyContainer
public class ApplicationProperties {

	@NotNull
	private Double startingPosition = 39.2;
	
	@NotNull
	private String name = "";

	public Double getStartingPosition() {
		return startingPosition;
	}

	public void setStartingPosition(Double startingPosition) {
		this.startingPosition = startingPosition;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
