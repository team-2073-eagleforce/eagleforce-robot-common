package com.team2073.common.mediator.Tracker;

import com.team2073.common.mediator.condition.Condition;

public interface Tracker {
    Condition findSubsystemCondition(Class clazz);
}
