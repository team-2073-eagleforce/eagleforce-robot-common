package com.team2073.common.position;

/**
 * A simple converter that does no conversion and reports a unit type of "tics".
 *
 * @author Preston Briggs
 */
public class NoOpPositionConverter implements PositionConverter {

	@Override
	public double asPosition(int tics) {
		return tics;
	}

	@Override
	public int asTics(double position) {
		return (int) position;
	}

	@Override
	public String positionalUnit() {
		return "tics";
	}
	
}