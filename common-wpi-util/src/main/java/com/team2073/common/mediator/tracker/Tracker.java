package com.team2073.common.mediator.tracker;

import com.team2073.common.mediator.condition.Condition;

public interface Tracker {
    Condition findSubsystemCondition(Class clazz);
}
