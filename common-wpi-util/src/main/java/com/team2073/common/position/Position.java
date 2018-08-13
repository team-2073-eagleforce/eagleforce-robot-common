package com.team2073.common.position;

/**
 * TODO
 * @author Preston Briggs
 */
public class Position {

	public final double lowerBound;
	public final double midPoint;
	public final double upperBound;

	/**
	 * Create a position, automatically calculating the upper/lower bounds based on
	 * the midpoint and the variance. For example a midpoint of 90 and a variance of
	 * 2 would result in a lowerbound of 88 and an upperbound of 92.
	 * 
	 * @param midPoint
	 *            The middle of the position 'range'
	 * @param variance
	 *            The distance from the midpoint that the upperBound and lowerBound
	 *            should be set.
	 */
	public Position(double midPoint, double variance) {
		this.lowerBound = midPoint - variance;
		this.midPoint = midPoint;
		this.upperBound = midPoint + variance;
	}

	public Position(double lowerBound, double midPoint, double upperBound) {
		this.lowerBound = lowerBound;
		this.midPoint = midPoint;
		this.upperBound = upperBound;
	}

	public boolean withinBounds(double currentPosition) {
		return currentPosition >= lowerBound && currentPosition <= upperBound;
	}

	public boolean pastBounds(double currentPosition, double startingPosition) {
		boolean movingUp = currentPosition > startingPosition || currentPosition < midPoint;
		return pastBounds(currentPosition, movingUp);
	}
	
	public boolean pastBounds(double currentPosition, boolean movingUp) {
		if(movingUp) {
			return currentPosition > upperBound;
		} else {
			return currentPosition < lowerBound;
		}
	}

	public boolean withinOrPastBounds(double currentPosition, double startingPosition) {
		return withinBounds(currentPosition) || pastBounds(currentPosition, startingPosition);
	}
	
	@Override
	public String toString() {
		return lowerBound + " -> " + midPoint + " <- " + upperBound;
	}
}
