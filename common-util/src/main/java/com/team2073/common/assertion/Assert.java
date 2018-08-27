package com.team2073.common.assertion;

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
			throw new IllegalArgumentException(String.format("[%s] must not be null.", variableName));
	}
}
