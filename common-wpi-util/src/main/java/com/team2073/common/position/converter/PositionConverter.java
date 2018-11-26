package com.team2073.common.position.converter;

import com.team2073.common.util.Throw;

/**
 * TODO
 * @author Preston Briggs
 *
 */
public interface PositionConverter {

	// TODO:
	// 	-Extract this to an interface and an abstract class
	// 	-Couldn't we just supply one ratio and this class could convert to and from?

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
		
		// Allow for one tic of rounding in each conversion and 2 additional tics just to be nice
		if(diff > 4) {
			Throw.illegalState("Error in position/tics conversion. " +
					"asPosition({}) returned [{}]. Expected asTics({}) to return [{}] but found [{}] instead. " +
					"Difference: [{}]. Check math in asPosition(int) and asTics(double) method implementations."
					, testTics, pos, pos, testTics, tics, diff);
		}
	}
	
	public abstract static class Units {
		public static final String DEGREES = "degrees";
		public static final String INCHES = "inches";
		public static final String CENTIMETERS = "cm";
	}
	
}