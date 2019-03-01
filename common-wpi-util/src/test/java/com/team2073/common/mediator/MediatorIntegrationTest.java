package com.team2073.common.mediator;

import com.team2073.common.mediator.condition.Condition;
import com.team2073.common.mediator.condition.PositionBasedCondition;
import com.team2073.common.mediator.conflict.PositionBasedConflict;
import com.team2073.common.mediator.request.Request;
import com.team2073.common.mediator.subsys.PositionBasedSubsystem;
import com.team2073.common.periodic.PeriodicRunnable;
import com.team2073.common.util.MathUtil;
import org.apache.commons.lang3.Range;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static com.team2073.common.mediator.MediatorTestFixtures.TestMediator;

public class MediatorIntegrationTest {

    private void callPeriodic(int calls, PeriodicRunnable... runnables) {
        for (int i = 0; i <= calls; i++) {
            for (PeriodicRunnable runnable : runnables) {
                runnable.onPeriodic();
            }
        }
    }

    @Test
    public void mediator_SHOULD_HandleParallelConflicts_WHEN_SubsystemsAreLinearAndAngular() {
        AngularSubsystem angularSubsystem = new AngularSubsystem();
        LinearSubsystem linearSubsystem = new LinearSubsystem();

        TestMediator mediator = new TestMediator();
        mediator.registerColleague(angularSubsystem, linearSubsystem);
        mediator.registerConflict(new PositionBasedConflict(LinearSubsystem.class,
                new PositionBasedCondition(0d, Range.between(0d, 25d)),
                AngularSubsystem.class,
                new PositionBasedCondition(0d, Range.between(0d, 90d)),
                true, true));

        mediator.add(new Request<>(AngularSubsystem.class, new PositionBasedCondition(95d, Range.between(90d, 90d))));

        callPeriodic(100, mediator, angularSubsystem, linearSubsystem);
    }

    private static final double ROBOT_WIDTH = 32;
    private static final double PIVOT_LENGTH = 16;
    private static final double CARRIAGE_LENGTH = 21.5;

    public class AngularSubsystem implements PositionBasedSubsystem {

        private double setpoint;
        private double position = 0;

        @Override
        public double getSafetyRange() {
            return 5;
        }

        @Override
        public double pointToPosition(@NotNull Vector2D vector2D) {
            double positionFromX = Math.abs(MathUtil.degreeArcCosine(((ROBOT_WIDTH - vector2D.getX()) / PIVOT_LENGTH)));
            double positionFromY = Math.abs(MathUtil.degreeArcSine((vector2D.getY() / PIVOT_LENGTH)));

            return positionFromX;
        }

        @NotNull
        @Override
        public Vector2D positionToPoint(double v) {
            double y = Math.abs(PIVOT_LENGTH * MathUtil.degreeSine(position));
            double x = Math.abs(ROBOT_WIDTH - (PIVOT_LENGTH * MathUtil.degreeCosine(position)));

            return new Vector2D(x, y);
        }

        @Override
        public void set(Double place) {
            setpoint = place;
        }

        @NotNull
        @Override
        public Condition<Double> getCurrentCondition() {
            return new PositionBasedCondition(position, Range.between(position - getSafetyRange(),
                    position + getSafetyRange()));
        }

        @Override
        public void onPeriodic() {

            System.out.println("Angular Position: " + position);

            if (position != setpoint) {
                if (position > setpoint) {
                    position--;
                } else {
                    position++;
                }
            }
        }
    }

    public class LinearSubsystem implements PositionBasedSubsystem {

        private double position = 0;
        private double setpoint;

        @Override
        public double getSafetyRange() {
            return 5;
        }

        @Override
        public double pointToPosition(@NotNull Vector2D vector2D) {
            return vector2D.getY();
        }

        @NotNull
        @Override
        public Vector2D positionToPoint(double v) {
            return new Vector2D(CARRIAGE_LENGTH, v);
        }

        @NotNull
        @Override
        public Condition<Double> getCurrentCondition() {
            return new PositionBasedCondition(position, Range.between(position - getSafetyRange(), position + getSafetyRange()));
        }

        @Override
        public void set(Double place) {
            setpoint = place;
        }

        @Override
        public void onPeriodic() {

            System.out.println("Linear Position: " + position);

            if (position != setpoint) {
                if (position > setpoint) {
                    position--;
                } else {
                    position++;
                }
            }
        }
    }
}
