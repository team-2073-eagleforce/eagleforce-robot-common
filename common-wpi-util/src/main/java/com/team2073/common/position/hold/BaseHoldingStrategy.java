package com.team2073.common.position.hold;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides some base functionality most {@link HoldingStrategy} implementations will need.
 * @author Preston Briggs
 */
public abstract class BaseHoldingStrategy implements HoldingStrategy {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	private static final String MSG = "Cannot access hold position before it has been set. "
			+ "Either set the hold position first or use the constructor accepting an initial position.";

	private boolean initialized = false;

	/**
	 * Initialized to -1 so we can see in debugging that it has not been set yet. There is no risk of accidentally
	 * returning -1 (which would cause a mechanism to try to hold at -1) because we check if initialized in the getter.
	 */
	private double holdPosition = -1;

	public BaseHoldingStrategy() {
		
	}
	
	public BaseHoldingStrategy(double initialHoldPosition) {
		logger.debug("Setting initial hold position to [{}].", initialHoldPosition);
		setHoldPosition(initialHoldPosition);
	}

	@Override
	public double getHoldPosition() {
		// Prevent trying to hold at an incorrect position and potentially breaking
		// a mechanism (trying to hold at a place it cannot get to)
		if(!initialized)
			throw new IllegalStateException(MSG);
		
		return holdPosition;
	}

	@Override
	public void setHoldPosition(double holdPosition) {
		this.holdPosition = holdPosition;
		initialized = true;
	}
		
}
