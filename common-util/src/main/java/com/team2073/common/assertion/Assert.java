package com.team2073.common.assertion;

import com.team2073.common.util.Ex;

public abstract class Assert {

	/**
	 * Use to check that a field is not null. If null, an {@link IllegalArgumentException} will be
	 * thrown with a message of: <i>{variableName} must not be null.</i>
	 * <p>
	 * Ex use:
	 * <pre>
	 * public void someMethod(SomeClass initialReceiver) {
	 * 
	 * 	<b>Assert.assertNotNull(initialReceiver, "initialReceiver");</b>
	 * 	// initialReceiver will not be null below here
	 * 
	 * 	initialReceiver.doSomeStuff();
	 * }
	 * </pre>
	 * @param variable The variable to assert is not null
	 * @param variableName The name of the variable. Used in the error message.
	 */
	public static void assertNotNull(Object variable, String variableName) {
		if(variable == null)
			throw Ex.illegalArg("[%s] must not be null.", variableName);
	}
	
	public static void assertNotNegative(double number, String variableName) {
		if (number < 0)
			throw Ex.illegalArg("[%s] must be positive.", variableName);
	}
	
	public static void assertNotPositive(double number, String variableName) {
		if (number > 0)
			throw Ex.illegalArg("[%s] must be positive.", variableName);
	}
}
