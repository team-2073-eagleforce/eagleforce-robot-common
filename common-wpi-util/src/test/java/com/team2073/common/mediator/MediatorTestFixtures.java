package com.team2073.common.mediator;

import com.team2073.common.mediator.Tracker.PositionBasedTrackee;
import com.team2073.common.mediator.Tracker.StateBasedTrackee;
import com.team2073.common.mediator.Tracker.Tracker;
import com.team2073.common.mediator.condition.Condition;
import com.team2073.common.mediator.condition.PositionBasedCondition;
import com.team2073.common.mediator.condition.StateBasedCondition;
import com.team2073.common.mediator.conflict.Conflict;
import com.team2073.common.mediator.request.Request;
import com.team2073.common.mediator.subsys.ColleagueSubsystem;
import com.team2073.common.mediator.subsys.PositionBasedSubsystem;
import com.team2073.common.mediator.subsys.StateBasedSubsystem;
import com.team2073.common.mediator.subsys.SubsystemStateCondition;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;

public class MediatorTestFixtures {

    static class PositionSubsystem implements PositionBasedSubsystem {

        private Double currentPosition = 0d;

        @Override
        public void set(Double place) {
            currentPosition = place;
        }

        @Override
        public double getSafetyRange() {
            return 0;
        }

        @Override
        public void onPeriodic() {

        }

        @NotNull
        @Override
        public Condition<Double> getCurrentCondition() {
            return new PositionBasedCondition(currentPosition - getSafetyRange(), currentPosition, currentPosition + getSafetyRange());
        }
    }

    static class DeadPositionSubsystem implements PositionBasedSubsystem {

        //empty class

        @Override
        public void set(Double place) {
        }

        @Override
        public double getSafetyRange() {
            return 0;
        }

        @Override
        public void onPeriodic() {

        }

        @NotNull
        @Override
        public Condition<Double> getCurrentCondition() {
            return new PositionBasedCondition(0, 0, 0);
        }
    }

    enum State implements SubsystemStateCondition {
        STOP,
        RUNNING,
        OPEN,
        CLOSE
    }

    static class StateSubsystem implements StateBasedSubsystem {

        private State currentState = State.STOP;

        @Override
        public void set(@NotNull SubsystemStateCondition place) {
            currentState = (State) place;
        }

        @Override
        public void onPeriodic() {

        }

        @NotNull
        @Override
        public Condition<SubsystemStateCondition> getCurrentCondition() {
            return new StateBasedCondition(currentState);
        }
    }

    static class TestMediator extends Mediator {
    }
}
