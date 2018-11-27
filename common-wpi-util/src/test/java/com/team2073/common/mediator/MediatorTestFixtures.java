package com.team2073.common.mediator;

import com.team2073.common.mediator.Tracker.PositionBasedTrackee;
import com.team2073.common.mediator.Tracker.StateBasedTrackee;
import com.team2073.common.mediator.request.Request;
import com.team2073.common.mediator.subsys.PositionBasedSubsystem;
import com.team2073.common.mediator.subsys.StateBasedSubsystem;
import com.team2073.common.mediator.subsys.SubsystemStateCondition;

import java.util.Deque;
import java.util.LinkedList;

public class MediatorTestFixtures {

    static class PositionSubsystem extends PositionBasedSubsystem implements PositionBasedTrackee {

        PositionSubsystem() {
//            tracker.registerTrackee(this);
        }

        private Double currentPosition = 0d;

        @Override
        public <Double> void set(Double place) {
            currentPosition = (java.lang.Double) place;
        }

        @Override
        public double getSafetyRange() {
            return 0;
        }

        @Override
        public void onPeriodic() {

        }

        @Override
        public Double updateTracker() {
            return currentPosition;
        }


    }

    enum State implements SubsystemStateCondition {
        STOP,
        RUNNING,
        OPEN,
        CLOSE
    }

    static class StateSubsystem extends StateBasedSubsystem implements StateBasedTrackee {

        StateSubsystem() {
//            tracker.registerTrackee(this);
        }

        private State currentState;

        @Override
        public <SubsystemStateCondition> void set(SubsystemStateCondition place) {
            currentState = (State) place;
        }

        @Override
        public void onPeriodic() {

        }

        @Override
        public SubsystemStateCondition updateTracker() {
            return currentState;
        }
    }

    static class TestMediator extends Mediator {
        private Deque<Deque<Request>> executeList = new LinkedList<>();

//        public Deque<Deque<Request>> getExecuteList(){
//            return executeList;
//        }
    }
}
