package com.team2073.common.util;

import com.team2073.common.position.Position;

/**
 * @deprecated Use {@link Position} instead
 */
@Deprecated
public class PositionalRange {
	private double lowerBound;
	private double upperBound;
	
	public PositionalRange(double lowerBound, double upperBound) {
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}
	
	public boolean isInRange(double position) {
		return position >= lowerBound && position <= upperBound;
	}

	public double getLowerBound() {
		return lowerBound;
	}

	public double getUpperBound() {
		return upperBound;
	}
	
	
}
