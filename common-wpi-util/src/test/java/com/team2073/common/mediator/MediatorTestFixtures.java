package com.team2073.common.mediator;

import com.team2073.common.mediator.condition.Condition;
import com.team2073.common.mediator.condition.PositionBasedCondition;
import com.team2073.common.mediator.condition.StateBasedCondition;
import com.team2073.common.mediator.subsys.PositionBasedSubsystem;
import com.team2073.common.mediator.subsys.StateBasedSubsystem;
import com.team2073.common.mediator.subsys.SubsystemStateCondition;
import org.apache.commons.lang3.Range;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.jetbrains.annotations.NotNull;

public class MediatorTestFixtures {

    static class LinearPositionSubsystem implements PositionBasedSubsystem {

        private Double setpoint;
        private Double currentPosition = 0d;

        @Override
        public void set(Double place) {
            setpoint = place;
        }

        @Override
        public double getSafetyRange() {
            return 0;
        }

        @Override
        public void onPeriodic() {
            if (setpoint == null) {
                return;
            }

            if(!currentPosition.equals(setpoint)) {
                if (currentPosition > setpoint) {
                    currentPosition--;
                } else {
                    currentPosition++;
                }
            }

        }

        @NotNull
        @Override
        public Condition<Double> getCurrentCondition() {
            return new PositionBasedCondition(currentPosition, Range.between(currentPosition - getSafetyRange(), currentPosition + getSafetyRange()));
        }

        @NotNull
        @Override
        public Vector2D positionToPoint(double position) {
            return new Vector2D(0, currentPosition);
        }

        @Override
        public double pointToPosition(@NotNull Vector2D point) {
            return point.getY();
        }

        @NotNull
        @Override
        public Vector2D getLowerLeftBound() {
            return new Vector2D(0, 0);
        }

        @NotNull
        @Override
        public Vector2D getUpperRightBound() {
            return new Vector2D(0,100);
        }
    }

    static class HorizontalPositionSubsystem implements PositionBasedSubsystem {

        private Double setpoint;
        private Double currentPosition = 0d;

        @Override
        public void set(Double place) {
            setpoint = place;
        }

        @Override
        public double getSafetyRange() {
            return 0;
        }

        @Override
        public void onPeriodic() {

            if (setpoint == null) {
                return;
            }

            if(!setpoint.equals(currentPosition)) {
                if (currentPosition > setpoint) {
                    currentPosition--;
                } else {
                    currentPosition++;
                }
            }
        }

        @NotNull
        @Override
        public Condition<Double> getCurrentCondition() {
            return new PositionBasedCondition(currentPosition, Range.between(currentPosition - getSafetyRange(), currentPosition + getSafetyRange()));
        }

        @NotNull
        @Override
        public Vector2D positionToPoint(double position) {
            return new Vector2D(currentPosition, 0);
        }

        @Override
        public double pointToPosition(@NotNull Vector2D point) {
            return point.getX();
        }

        @NotNull
        @Override
        public Vector2D getLowerLeftBound() {
            return new Vector2D(0, 0);
        }

        @NotNull
        @Override
        public Vector2D getUpperRightBound() {
            return new Vector2D(100, 0);
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
            return new PositionBasedCondition(0, Range.between(0d, 0d));
        }

        @NotNull
        @Override
        public Vector2D positionToPoint(double position) {
            return new Vector2D(0, 0);
        }

        @Override
        public double pointToPosition(@NotNull Vector2D point) {
            return 0;
        }

        @NotNull
        @Override
        public Vector2D getLowerLeftBound() {
            return new Vector2D(0, 0);
        }

        @NotNull
        @Override
        public Vector2D getUpperRightBound() {
            return new Vector2D(0, 0);
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
