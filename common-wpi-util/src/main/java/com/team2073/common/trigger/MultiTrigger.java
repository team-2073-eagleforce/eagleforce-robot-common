package com.team2073.common.trigger;

import edu.wpi.first.wpilibj.buttons.Trigger;

import java.util.Arrays;
import java.util.List;

public class MultiTrigger extends Trigger {

	private final List<Trigger> triggers;

	public MultiTrigger(List<Trigger> triggers) {
		this.triggers = triggers;
	}

	public MultiTrigger(Trigger... triggers) {
		this(Arrays.asList(triggers));
	}

	@Override
	public boolean get() {
		// TODO: Creating a stream is probably pretty expensive here and this gets called a lot
		return triggers.stream().allMatch(Trigger::get);
	}
}
