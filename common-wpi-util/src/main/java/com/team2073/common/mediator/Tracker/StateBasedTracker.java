package com.team2073.common.mediator.Tracker;

import com.team2073.common.mediator.subsys.SubsystemStateCondition;

public interface StateBasedTracker<T extends Enum<T>> extends Tracker<SubsystemStateCondition<T>> {

}
