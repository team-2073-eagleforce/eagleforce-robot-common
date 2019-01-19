package com.team2073.common.mediator;

import com.team2073.common.mediator.MediatorTestFixtures.*;
import com.team2073.common.mediator.Tracker.PositionBasedTrackee;
import com.team2073.common.mediator.Tracker.StateBasedTrackee;
import com.team2073.common.mediator.Tracker.SubsystemTrackee;
import com.team2073.common.mediator.condition.Condition;
import com.team2073.common.mediator.condition.PositionBasedCondition;
import com.team2073.common.mediator.condition.StateBasedCondition;
import com.team2073.common.mediator.conflict.Conflict;
import com.team2073.common.mediator.conflict.PositionStateBasedConflict;
import com.team2073.common.mediator.conflict.StatePositionBasedConflict;
import com.team2073.common.mediator.request.Request;
import com.team2073.common.mediator.subsys.ColleagueSubsystem;
import com.team2073.common.mediator.subsys.PositionBasedSubsystem;
import com.team2073.common.mediator.subsys.SubsystemStateCondition;
import com.team2073.common.position.Position;
import com.team2073.common.test.annon.TestNotWrittenYet;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class MediatorTests {
    private Tracker tracker = new Tracker();
    private PositionSubsystem positionSubsystem = new PositionSubsystem();
    private StateSubsystem stateSubsystem = new StateSubsystem();
    private DeadPositionSubsystem deadPositionSubsystem = new DeadPositionSubsystem();

    private ArrayList<Conflict> positionConflicts = new ArrayList<>();
    private ArrayList<Conflict> stateConflicts = new ArrayList<>();

    private static Map<Class<? extends ColleagueSubsystem>, ColleagueSubsystem> subsystemMap = new HashMap<>();
    private static Map<Class<? extends ColleagueSubsystem>, ArrayList<Conflict>> conflictMap = new HashMap<>();

    private void callPeriodic(Mediator mediator, int calls) {
        for (int i = 0; i <= calls; i++) {
            mediator.onPeriodic();
        }
    }

    private void makeConflicts() {
        positionConflicts.add(new PositionStateBasedConflict(PositionSubsystem.class,
                new PositionBasedCondition(85d, 90d, 95d),
                MediatorTestFixtures.StateSubsystem.class,
                new StateBasedCondition(State.OPEN),
                State.CLOSE));

        stateConflicts.add(new StatePositionBasedConflict(StateSubsystem.class,
                new StateBasedCondition(State.OPEN),
                PositionSubsystem.class,
                new PositionBasedCondition(85, 90, 95)));

        conflictMap.put(PositionSubsystem.class, positionConflicts);
        conflictMap.put(MediatorTestFixtures.StateSubsystem.class, stateConflicts);
    }

    private void makeSubsystemMap() {
        subsystemMap.put(PositionSubsystem.class, positionSubsystem);
        subsystemMap.put(StateSubsystem.class, stateSubsystem);
        subsystemMap.put(DeadPositionSubsystem.class, deadPositionSubsystem);
    }

    @BeforeEach
    void setUp() {
        makeConflicts();
        makeSubsystemMap();
    }

    @Test
    public void mediator_WHEN_PositionSubsystemAskedToMove_SHOULD_Move() {
        TestMediator testMediator = new TestMediator();
        testMediator.init(subsystemMap, conflictMap, tracker);

        tracker.registerTrackee(positionSubsystem);
        Request<Double> request = new Request<Double>(PositionSubsystem.class, new PositionBasedCondition(5, 10, 15));
        testMediator.add(request);
        testMediator.onPeriodic();

        assertThat(positionSubsystem.updateTracker()).isEqualTo(10);
    }

    @Test
    public void mediator_WHEN_StateSubsystemAskedToMove_SHOULD_Move() {
        TestMediator testMediator = new TestMediator();
        testMediator.init(subsystemMap, conflictMap, tracker);

        tracker.registerTrackee(stateSubsystem);
        Request<SubsystemStateCondition> request = new Request<SubsystemStateCondition>(StateSubsystem.class, new StateBasedCondition(State.OPEN));
        testMediator.add(request);
        testMediator.onPeriodic();

        assertThat(stateSubsystem.updateTracker()).isEqualTo(State.OPEN);
    }

    @Test
    public void mediator_WHEN_FinishedWithRequest_SHOULD_RemoveFromList() {
        TestMediator testMediator = new TestMediator();
        testMediator.init(subsystemMap, conflictMap, tracker);

        Request<Double> request = new Request<>(PositionSubsystem.class, new PositionBasedCondition(5, 10, 15));

        testMediator.add(request);
        Deque<Deque<Request>> list = testMediator.getExecuteList();

        assertThat(list.size()).isEqualTo(1);

        callPeriodic(testMediator, 3);

        assertThat(list.size()).isEqualTo(0);
    }

    @Test
    public void mediator_WHEN_IteratingThroughRequests_SHOULD_IterateOverMostRecentlyAdded() {
        TestMediator testMediator = new TestMediator();
        testMediator.init(subsystemMap, conflictMap, tracker);

        Request<Double> request = new Request<>(PositionSubsystem.class, new PositionBasedCondition(5, 10, 15));
        Request<Double> request2 = new Request<>(PositionSubsystem.class, new PositionBasedCondition(0, 5, 10));

        testMediator.add(request);
        testMediator.onPeriodic();
        Request expectedRequest = testMediator.getCurrentRequest();
        assertThat(expectedRequest).isEqualTo(request);
        callPeriodic(testMediator, 2);

        testMediator.add(request2);
        testMediator.onPeriodic();
        expectedRequest = testMediator.getCurrentRequest();
        assertThat(expectedRequest).isEqualTo(request2);
    }

    @Test
    public void mediator_WHEN_FoundConflict_SHOULD_AddRequest() {
        TestMediator testMediator = new TestMediator();
        testMediator.init(subsystemMap, conflictMap, tracker);

        Request request = new Request<Double>(PositionSubsystem.class, new PositionBasedCondition(85d, 90d, 95d));
        Request request2 = new Request<SubsystemStateCondition>(StateSubsystem.class, new StateBasedCondition(State.OPEN));

        testMediator.add(request);
        testMediator.add(request2);
        Deque<Deque<Request>> list = testMediator.getExecuteList();
        assertThat(list.size()).isEqualTo(2);

        callPeriodic(testMediator, 4);

        assertThat(testMediator.getCurrentRequest()).isNotEqualTo(request).isNotEqualTo(request2);
    }

    @Test
    public void mediator_WHEN_EncounteredConflict_SHOULD_ResolveConflict() {
        TestMediator testMediator = new TestMediator();
        testMediator.init(subsystemMap, conflictMap, tracker);

        Request positionRequest = new Request<Double>(PositionSubsystem.class, new PositionBasedCondition(85d, 90d, 95d));
        Request stateRequest = new Request<SubsystemStateCondition>(StateSubsystem.class, new StateBasedCondition(State.OPEN));

        testMediator.add(stateRequest);
        callPeriodic(testMediator, 2);
        assertThat(stateSubsystem.updateTracker()).isEqualTo(State.OPEN);
        testMediator.add(positionRequest);

        callPeriodic(testMediator, 6);

        assertThat(stateSubsystem.updateTracker()).isEqualTo(State.CLOSE);
        assertThat(positionSubsystem.updateTracker()).isEqualTo(90d);

    }

    @Test
    public void mediator_WHEN_RequestTakesTooLong_SHOULD_ClearOutRequest() {
        TestMediator testMediator = new TestMediator();
        testMediator.init(subsystemMap, conflictMap, tracker);
        tracker.registerTrackee(deadPositionSubsystem);

        testMediator.add(new Request<Double>(PositionSubsystem.class, new PositionBasedCondition(30, 35, 40)));
        testMediator.add(new Request<Double>(DeadPositionSubsystem.class, new PositionBasedCondition(20, 25, 30)));
        testMediator.add(new Request<Double>(PositionSubsystem.class, new PositionBasedCondition(0, 5, 15)));

        callPeriodic(testMediator, 30);

        assertThat(testMediator.getExecuteList().size()).isEqualTo(0);
        assertThat(positionSubsystem.updateTracker()).isCloseTo(5d, Offset.offset(0d));
    }

    //TODO finish filling out
    @TestNotWrittenYet
    @Test
    public void mediator_WHEN_FinishedWithLargeRequestList_SHOULD_ProperlyReset() {
//        TestMediator testMediator = new TestMediator();
//        testMediator.init(subsystemMap, conflictMap, tracker);
//
//        RobotContext robotContext = RobotContext.getInstance();
//        PeriodicRunner periodicRunner = robotContext.getPeriodicRunner();
//
//        ArmMechanism deadArm = new ArmMechanism(100d, SimulationConstants.MotorType.BAG, 1, 400, 300);
//        PidfControlLoop pid = new PidfControlLoop(0, 0, 0, 0, 0);
//        SimulationEagleSRX srx = new SimulationEagleSRX("DeadArm", deadArm, 2048);
//
//        testMediator.add(new Request(deadArm, new PositionBasedCondition(20, 30, 25)));
//        SimulationEnvironmentRunner.create()
//                .withCycleComponent(deadArm)
//                .withPeriodicComponent(() -> periodicRunner.invokePeriodicInstances())
//                .withPeriodicComponent(testMediator)
//                .withIterationCount(100)
//                .run(e -> {
//                    assertThat(testMediator.getExecuteList().size()).isEqualTo(0);
//                });

    }


    class Tracker implements com.team2073.common.mediator.Tracker.Tracker {

        private ArrayList<SubsystemTrackee> instanceList = new ArrayList<>();

        public void registerTrackee(SubsystemTrackee instance) {
            System.out.println("registering: " + instance.toString());
            instanceList.add(instance);
        }

        @Override
        public Condition findSubsystemCondition(Class clazz) {
            SubsystemTrackee subsystem = (SubsystemTrackee) subsystemMap.get(clazz);
            Condition condition = null;

            if (subsystem instanceof PositionBasedTrackee) {
                double position = (double) subsystem.updateTracker();
                condition = new PositionBasedCondition(position - 5, position, position + 5);

            } else if (subsystem instanceof StateBasedTrackee) {
                condition = new StateBasedCondition((SubsystemStateCondition) subsystem.updateTracker());
            }

            return condition;
        }
    }

}


