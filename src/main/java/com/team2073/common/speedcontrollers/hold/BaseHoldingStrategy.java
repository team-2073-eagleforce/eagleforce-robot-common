package com.team2073.common.speedcontrollers.hold;

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
	private double holdPosition = -1;

	public BaseHoldingStrategy() {
		
	}
	
	public BaseHoldingStrategy(double initialHoldPosition) {
		logger.debug("Setting initial hold position to [{}].", initialHoldPosition);
		setHoldPosition(initialHoldPosition);
	}

	@Override
	public double getHoldPosition() {
		// Prevent trying to hold at an incorrect position
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
