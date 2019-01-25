package com.team2073.common.mediator;

import com.team2073.common.mediator.MediatorTestFixtures.*;
import com.team2073.common.mediator.condition.PositionBasedCondition;
import com.team2073.common.mediator.condition.StateBasedCondition;
import com.team2073.common.mediator.conflict.*;
import com.team2073.common.mediator.request.Request;
import com.team2073.common.mediator.subsys.SubsystemStateCondition;
import com.team2073.common.test.annon.TestNotWrittenYet;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Deque;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class MediatorTests {
    private PositionSubsystem positionSubsystem = new PositionSubsystem();
    private StateSubsystem stateSubsystem = new StateSubsystem();
    private StateSubsystemDeux stateSubsystemDeux = new StateSubsystemDeux();
    private DeadPositionSubsystem deadPositionSubsystem = new DeadPositionSubsystem();

    private ArrayList<Conflict> positionConflicts = new ArrayList<>();
    private ArrayList<Conflict> stateConflicts = new ArrayList<>();

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
                new PositionBasedCondition(85, 90, 95), State.CLOSE));
    }

    @BeforeEach
    void setUp() {
        makeConflicts();
    }

    @Test
    public void mediator_SHOULD_WorkWithoutRegisteringConflicts() {
        TestMediator testMediator = new TestMediator();
        testMediator.registerColleague(positionSubsystem);
        testMediator.add(new Request<>(PositionSubsystem.class, new PositionBasedCondition(0, 0, 0)));

        callPeriodic(testMediator, 5);

        assertThat(positionSubsystem.getCurrentCondition().getConditionValue()).isCloseTo(0, Offset.offset(0d));
    }

    @Test
    public void mediator_SHOULD_RegisterSubsystemsProperly() {
        TestMediator testMediator = new TestMediator();
        testMediator.registerConflict(positionConflicts);

        testMediator.registerColleague(positionSubsystem);
        testMediator.registerColleague(stateSubsystem);
        testMediator.registerColleague(deadPositionSubsystem);
        testMediator.registerColleague(deadPositionSubsystem, deadPositionSubsystem, positionSubsystem);

        assertThat(testMediator.getSubsystemMap().size()).isEqualTo(3);
    }

    @Test
    public void mediator_SHOULD_RegisterConflictsCorrectly() {
        TestMediator testMediator = new TestMediator();

        testMediator.registerConflict(new PositionBasedConflict(PositionSubsystem.class,
                new PositionBasedCondition(0, 0, 0), DeadPositionSubsystem.class,
                new PositionBasedCondition(0, 0, 0)));

        assertThat(testMediator.getConflictMap().size()).isEqualTo(2);
    }

    @Test
    public void mediator_WHEN_PositionSubsystemAskedToMove_SHOULD_Move() {
        TestMediator testMediator = new TestMediator();
        testMediator.registerColleague(positionSubsystem);

        Request<Double> request = new Request<>(PositionSubsystem.class, new PositionBasedCondition(5, 10, 15));
        testMediator.add(request);
        testMediator.onPeriodic();

        assertThat(positionSubsystem.getCurrentCondition().getConditionValue()).isEqualTo(10);
    }

    @Test
    public void mediator_WHEN_StateSubsystemAskedToMove_SHOULD_Move() {
        TestMediator testMediator = new TestMediator();
        testMediator.registerColleague(stateSubsystem);

        Request<SubsystemStateCondition<State>> request = new Request<>(StateSubsystem.class, new StateBasedCondition(State.OPEN));
        testMediator.add(request);
        testMediator.onPeriodic();

        assertThat(stateSubsystem.getCurrentCondition().getConditionValue()).isEqualTo(State.OPEN);
    }

    @Test
    public void mediator_WHEN_FinishedWithRequest_SHOULD_RemoveFromList() {
        TestMediator testMediator = new TestMediator();
        testMediator.registerColleague(positionSubsystem);

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
        testMediator.registerColleague(stateSubsystem);
        testMediator.registerColleague(positionSubsystem);
        testMediator.registerConflict(positionConflicts);

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
        testMediator.registerColleague(stateSubsystem);
        testMediator.registerColleague(positionSubsystem);
        testMediator.registerConflict(positionConflicts);
        testMediator.registerConflict(stateConflicts);

        Request request = new Request<>(PositionSubsystem.class, new PositionBasedCondition(85d, 90d, 95d));
        Request request2 = new Request<>(StateSubsystem.class, new StateBasedCondition(State.OPEN));

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
        testMediator.registerColleague(stateSubsystem);
        testMediator.registerColleague(positionSubsystem);
        testMediator.registerConflict(positionConflicts);
        testMediator.registerConflict(stateConflicts);

        Request positionRequest = new Request<>(PositionSubsystem.class, new PositionBasedCondition(85d, 90d, 95d));
        Request stateRequest = new Request<>(StateSubsystem.class, new StateBasedCondition(State.OPEN));

        testMediator.add(stateRequest);
        callPeriodic(testMediator, 2);
        assertThat(stateSubsystem.getCurrentCondition().getConditionValue()).isEqualTo(State.OPEN);
        testMediator.add(positionRequest);

        callPeriodic(testMediator, 6);

        assertThat(stateSubsystem.getCurrentCondition().getConditionValue()).isEqualTo(State.CLOSE);
        assertThat(positionSubsystem.getCurrentCondition().getConditionValue()).isEqualTo(90d);

    }

    @Test
    public void mediator_WHEN_EncounteredStateConflictWithTwoEnums_SHOULD_ResolveConflict() {
        TestMediator testMediator = new TestMediator();

        testMediator.registerColleague(stateSubsystem);
        testMediator.registerColleague(stateSubsystemDeux);
        testMediator.registerConflict(new StateBasedConflict(StateSubsystem.class,
                new StateBasedCondition(State.OPEN),
                StateSubsystemDeux.class,
                new StateBasedCondition<StateDeux>(StateDeux.OPEN), StateDeux.STOP, State.STOP));

        testMediator.add(new Request(StateSubsystemDeux.class, new StateBasedCondition(StateDeux.OPEN)));
        testMediator.add(new Request(StateSubsystem.class, new StateBasedCondition(State.OPEN)));

        callPeriodic(testMediator, 6);

        assertThat(stateSubsystemDeux.getCurrentCondition().getConditionValue()).isEqualTo(StateDeux.STOP);
    }

    @Test
    public void mediator_WHEN_ConflictFlipped_SHOULD_ResolveConflict() {
        TestMediator testMediator = new TestMediator();

        testMediator.registerColleague(stateSubsystem);
        testMediator.registerColleague(stateSubsystemDeux);

        testMediator.registerConflict(new StateBasedConflict(StateSubsystemDeux.class,
                new StateBasedCondition<StateDeux>(StateDeux.OPEN),
                StateSubsystem.class,
                new StateBasedCondition<State>(State.OPEN), State.CLOSE, StateDeux.STOP));

        stateSubsystemDeux.set(StateDeux.OPEN);
        testMediator.add(new Request(StateSubsystem.class, new StateBasedCondition(State.OPEN)));
        callPeriodic(testMediator, 3);

        assertThat(stateSubsystemDeux.getCurrentCondition().getConditionValue()).isEqualTo(StateDeux.STOP);
    }

    @Test
    public void mediator_WHEN_RequestTakesTooLong_SHOULD_ClearOutRequest() {
        TestMediator testMediator = new TestMediator();
        testMediator.registerColleague(positionSubsystem);
        testMediator.registerColleague(deadPositionSubsystem);
        testMediator.registerConflict(positionConflicts);

        testMediator.add(new Request<>(PositionSubsystem.class, new PositionBasedCondition(30, 35, 40)));
        testMediator.add(new Request<>(DeadPositionSubsystem.class, new PositionBasedCondition(20, 25, 30)));
        testMediator.add(new Request<>(PositionSubsystem.class, new PositionBasedCondition(0, 5, 15)));

        callPeriodic(testMediator, 30);

        assertThat(testMediator.getExecuteList().size()).isEqualTo(0);
        assertThat(positionSubsystem.getCurrentCondition().getConditionValue()).isCloseTo(5d, Offset.offset(0d));
    }

    //TODO finish filling out
    @TestNotWrittenYet
    @Test
    public void mediator_WHEN_FinishedWithLargeRequestList_SHOULD_ProperlyReset() {
//        TestMediator testMediator = new TestMediator();
//        testMediator.init(conflictMap, tracker);
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
}


