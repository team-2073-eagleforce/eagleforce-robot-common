package com.team2073.common.objective;

public class PreconditionMapping {
	
	public static PreconditionMapping create(Precondition precondition, Objective resolution) {
		return new PreconditionMapping(precondition, resolution);
	}

	private Precondition precondition;
	private Objective resolution;

	public PreconditionMapping(Precondition precondition, Objective resolution) {
		this.precondition = precondition;
		this.resolution = resolution;
	}
	
	/** Shorthand for <code>getPrecondition().isMet()</code> */
	public boolean isMet() {
		return precondition.isMet();
	}
	
	public Precondition getPrecondition() {
		return precondition;
	}

	public Objective getResolution() {
		return resolution;
	}

}
