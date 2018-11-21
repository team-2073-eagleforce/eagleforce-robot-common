package com.team2073.common.subsys;

import com.team2073.common.objective.StatusChecker;
import com.team2073.common.periodic.PeriodicRunnable;
import com.team2073.common.position.Position;
import com.team2073.common.position.PositionContainer;
import com.team2073.common.position.converter.PositionConverter;
import com.team2073.common.subsys.PositionalMechanismController.GoalState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * @author pbriggs
 */
public class PositionalMechanismControllerIntegrationTestFixtures {

    public enum ElevatorGoal implements PositionContainer {
        ZERO (0, 3),
        MIDPOINT (90, 3),
        TOP (180, 3);

        private final Position position;

        ElevatorGoal(double midPoint, double variance) {
            this.position = new Position(midPoint, variance);
        }

        @Override
        public Position getPosition() {
            return position;
        }
    }

    public static class ElevatorPositionConverter implements PositionConverter {

        public static final double TICS_PER_DEGREE = 1000d;

        @Override
        public double asPosition(int tics) {
            return tics / TICS_PER_DEGREE;
        }

        @Override
        public int asTics(double position) {
            return (int) Math.round(position * TICS_PER_DEGREE);
        }

        @Override
        public String positionalUnit() {
            return "inches";
        }
    }

    public static class ElevatorGoalSupplier implements PeriodicRunnable {

        private Logger log = LoggerFactory.getLogger(getClass());

        private final PositionalMechanismController<ElevatorGoal> mechanismController;
        private Queue<ElevatorGoal> goalList = new ArrayDeque<>();
        private StatusChecker statusChecker;
        private int wait = 0;

        public ElevatorGoalSupplier(PositionalMechanismController<ElevatorGoal> mechanismController) {
            this.mechanismController = mechanismController;
            goalList.add(ElevatorGoal.MIDPOINT);
            goalList.add(ElevatorGoal.ZERO);
            goalList.add(ElevatorGoal.TOP);
            statusChecker = mechanismController.requestPosition(goalList.poll());
        }

        @Override
        public void onPeriodic() {
            GoalState goalState = mechanismController.getGoalState();
//            log.debug("Goal State: [{}]. Complete: [{}]. Position: [{}].", goalState, statusChecker.isComplete(), mechanismController.currentPosition());
            if (!goalList.isEmpty() && (statusChecker.isComplete())) {
                if (wait < 5) {
                    wait++;
                    log.debug("Waiting...");
                } else {
                    wait = 0;
                    statusChecker = mechanismController.requestPosition(goalList.poll());
                }
            }
        }
    }
}
