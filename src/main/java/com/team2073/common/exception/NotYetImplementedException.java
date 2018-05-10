package com.team2073.common.exception;

/**
 * The method or operation has not yet been implemented but will be in
 * the future. Useful for creating all the empty methods in a class
 * so work can start on other classes that depend on these methods without
 * compilation errors.
 *
 * @author Preston Briggs
 */
public class NotYetImplementedException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	/** @see NotYetImplementedException */
	public NotYetImplementedException() {
	}

	/**
	 * Creates a {@link NotYetImplementedException} with a message of:
	 * "The method [" + methodName + "] has not been implemented yet."
	 * @see NotYetImplementedException
	 */
	public NotYetImplementedException(String methodName) {
		super("The method [" + methodName + "] has not been implemented yet.");
	}
}
