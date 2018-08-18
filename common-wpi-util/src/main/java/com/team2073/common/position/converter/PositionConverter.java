package com.team2073.common.position.converter;

/**
 * TODO
 * @author Preston Briggs
 *
 */
public interface PositionConverter {

	/** Convert encoder tics to position (degrees, inches, cm, etc.). */
	public double asPosition(int tics);
	
	/** Convert position (degrees, inches, cm, etc.) to encoder tics. */
	public int asTics(double position);
	
	/**
	 * The name of the positional unit this converter converts to/from (degrees,
	 * inches, cm, etc.). Only used for logging. See {@link Units} for common units to use.
	 */
	public String positionalUnit();
	
	/**
	 * Verify the subclass properly implemented the conversions between tics and position.
	 */
	public static void assertConversions(PositionConverter converter) {
		int testTics = 1000;
		double pos = converter.asPosition(testTics);
		int tics = converter.asTics(pos);
		int diff = Math.abs(testTics - tics);
		
		// Allow for one tic of rounding in each conversion and 2 additional tics
		if(diff > 4)
			throw new IllegalArgumentException(
					"Error in position/tics conversion. "
					+ "asPosition(" + testTics + ") returned " + pos
					+ "asTics(" + pos + ") returned " + tics
					+ "Difference: [" + diff + "]." 
					+ "Check asPosition(int) and asTics(double) method implementations.");
	}
	
	public abstract static class Units {
		public static final String DEGREES = "degrees";
		public static final String INCHES = "inches";
		public static final String CENTIMETERS = "cm";
	}
	
}