package com.team2073.common.mediator;

import com.team2073.common.mediator.condition.Condition;
import com.team2073.common.mediator.condition.PositionBasedCondition;
import com.team2073.common.mediator.condition.StateBasedCondition;
import com.team2073.common.mediator.subsys.PositionBasedSubsystem;
import com.team2073.common.mediator.subsys.StateBasedSubsystem;
import com.team2073.common.mediator.subsys.SubsystemStateCondition;
import org.jetbrains.annotations.NotNull;

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

    enum State implements SubsystemStateCondition<State> {
        STOP,
        RUNNING,
        OPEN,
        CLOSE
    }

    static class StateSubsystem implements StateBasedSubsystem<State> {

        private State currentState = State.STOP;

        @Override
        public void set(@NotNull State place) {
            currentState = place;
        }

        @Override
        public void onPeriodic() {

        }

        @NotNull
        @Override
        public Condition<State> getCurrentCondition() {
            return new StateBasedCondition(currentState);
        }
    }

    enum StateDeux implements SubsystemStateCondition {
        STOP,
        OPEN,
        RUNNING
    }

    static class StateSubsystemDeux implements StateBasedSubsystem<StateDeux> {

        StateDeux currentState = StateDeux.STOP;

        @Override
        public void set(StateDeux place) {
            currentState = place;
        }

        @NotNull
        @Override
        public Condition<StateDeux> getCurrentCondition() {
            return new StateBasedCondition<>(currentState);
        }

        @Override
        public void onPeriodic() {

        }
    }

    static class TestMediator extends Mediator {
    }
}
