package com.team2073.common.position.hold;

import com.team2073.common.subsys.PositionalMechanismController;

/**
 * The strategy to execute to hold a specific position in a {@link PositionalMechanismController}.
 * 
 * @author Preston Briggs
 */
public interface HoldingStrategy {

	/** The position to hold in converted form (degrees, inches, etc.), <b>not</b> in tics. */
	double getHoldPosition();

	/** Sets the position to hold in converted form (degrees, inches, etc.), <b>not</b> in tics. */
	void setHoldPosition(double holdPosition);

	/** Hold the position defined by this strategy. Called repeatedly while hold is requested. */
	void holdPosition();
}
