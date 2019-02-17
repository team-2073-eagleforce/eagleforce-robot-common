package com.team2073.common.mediator;

import com.team2073.common.datarecorder.DataRecorder;
import com.team2073.common.mediator.MediatorTestFixtures.*;
import com.team2073.common.mediator.condition.PositionBasedCondition;
import com.team2073.common.mediator.condition.StateBasedCondition;
import com.team2073.common.mediator.conflict.*;
import com.team2073.common.mediator.request.Request;
import com.team2073.common.mediator.subsys.SubsystemStateCondition;
import com.team2073.common.periodic.PeriodicRunnable;
import com.team2073.common.test.annon.TestNotWrittenYet;
import org.apache.commons.lang3.Range;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Queue;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class MediatorTests {
    private LinearPositionSubsystem linearPositionSubsystem = new LinearPositionSubsystem();
    private HorizontalPositionSubsystem horizontalPositionSubsystem = new HorizontalPositionSubsystem();
    private StateSubsystem stateSubsystem = new StateSubsystem();
    private StateSubsystemDeux stateSubsystemDeux = new StateSubsystemDeux();
    private DeadPositionSubsystem deadPositionSubsystem = new DeadPositionSubsystem();

    private ArrayList<Conflict> positionConflicts = new ArrayList<>();
    private ArrayList<Conflict> stateConflicts = new ArrayList<>();

    private void callPeriodic(int calls, PeriodicRunnable... runnables) {
        for (int i = 0; i <= calls; i++) {
            for (PeriodicRunnable runnable: runnables) {
                runnable.onPeriodic();
            }
        }
    }

    private void makeConflicts() {
        positionConflicts.add(new PositionStateBasedConflict(LinearPositionSubsystem.class,
                new PositionBasedCondition(90, Range.between(85d, 95d)),
                MediatorTestFixtures.StateSubsystem.class,
                new StateBasedCondition(State.OPEN),
                State.CLOSE, true));

        stateConflicts.add(new StatePositionBasedConflict(StateSubsystem.class,
                new StateBasedCondition(State.OPEN),
                LinearPositionSubsystem.class,
                new PositionBasedCondition(90, Range.between(85d, 95d)), State.CLOSE, true));
    }

    @BeforeEach
    void setUp() {
        makeConflicts();
    }

    @Test
    public void mediator_SHOULD_registerWithPeriodicRunner() {

    }

    @Test
    public void mediator_SHOULD_AddInverseConflictToConflictMap() {
        TestMediator testMediator = new TestMediator();

        PositionBasedConflict conflict = new PositionBasedConflict(LinearPositionSubsystem.class, new PositionBasedCondition(0d, Range.between(0d,0d)),
                DeadPositionSubsystem.class, new PositionBasedCondition(0,Range.between(0d, 0d)), true);

        testMediator.registerConflict(conflict);

        assertThat(testMediator.getConflictMap().get(DeadPositionSubsystem.class).size()).isEqualTo(1);
    }

    @Test
    public void mediator_SHOULD_AddRequestsProperly() {
        TestMediator testMediator = new TestMediator();

        Request request = new Request<>(LinearPositionSubsystem.class, new PositionBasedCondition(0, Range.between(0d, 0d)));

        testMediator.add(request);

        assertThat(testMediator.getExecuteList().size()).isEqualTo(1);
    }

    @Test
    public void mediator_SHOULD_WorkWithoutRegisteringConflicts() {
        TestMediator testMediator = new TestMediator();
        testMediator.registerColleague(linearPositionSubsystem);
        testMediator.add(new Request<>(LinearPositionSubsystem.class, new PositionBasedCondition(0, Range.between(0d, 0d))));

        callPeriodic(5, testMediator);

        assertThat(linearPositionSubsystem.getCurrentCondition().getConditionValue()).isCloseTo(0, Offset.offset(0d));
    }

    @Test
    public void mediator_SHOULD_RegisterSubsystemsProperly() {
        TestMediator testMediator = new TestMediator();
        testMediator.registerConflict(positionConflicts);

        testMediator.registerColleague(linearPositionSubsystem);
        testMediator.registerColleague(stateSubsystem);
        testMediator.registerColleague(deadPositionSubsystem);
        testMediator.registerColleague(deadPositionSubsystem, deadPositionSubsystem, linearPositionSubsystem);

        assertThat(testMediator.getSubsystemMap().size()).isEqualTo(3);
    }

    @Test
    public void mediator_SHOULD_RegisterConflictsCorrectly() {
        TestMediator testMediator = new TestMediator();

        Conflict inverseConflict = new PositionBasedConflict(DeadPositionSubsystem.class, new PositionBasedCondition(0, Range.between(0d, 0d)),
                LinearPositionSubsystem.class, new PositionBasedCondition(0, Range.between(0d, 0d)), true);

        testMediator.registerConflict(new PositionBasedConflict(LinearPositionSubsystem.class,
                new PositionBasedCondition(0, Range.between(0d, 0d)), DeadPositionSubsystem.class,
                new PositionBasedCondition(0, Range.between(0d, 0d)), true));

        assertThat(testMediator.getConflictMap().size()).isEqualTo(2);
    }

    @Test
    public void mediator_WHEN_PositionSubsystemAskedToMove_SHOULD_Move() {
        TestMediator testMediator = new TestMediator();
        testMediator.registerColleague(linearPositionSubsystem);

        Request<Double> request = new Request<>(LinearPositionSubsystem.class, new PositionBasedCondition(10, Range.between(5d, 15d)));
        testMediator.add(request);
        callPeriodic(20, testMediator, linearPositionSubsystem);

        assertThat(linearPositionSubsystem.getCurrentCondition().getConditionValue()).isEqualTo(10);
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
        testMediator.registerColleague(linearPositionSubsystem);

        Request<Double> request = new Request<>(LinearPositionSubsystem.class, new PositionBasedCondition(10, Range.between(5d, 15d)));

        testMediator.add(request);
        Queue<Deque<Request>> list = testMediator.getExecuteList();

        assertThat(list.size()).isEqualTo(1);

        callPeriodic(50, testMediator, linearPositionSubsystem);

        list = testMediator.getExecuteList();
        assertThat(list.size()).isEqualTo(0);
    }

    @Test
    public void mediator_WHEN_IteratingThroughRequests_SHOULD_IterateOverMostRecentlyAdded() {
        TestMediator testMediator = new TestMediator();
        testMediator.registerColleague(stateSubsystem);
        testMediator.registerColleague(linearPositionSubsystem);
        testMediator.registerConflict(positionConflicts);

        Request<Double> request = new Request<>(LinearPositionSubsystem.class, new PositionBasedCondition(10, Range.between(5d, 15d)));
        Request<Double> request2 = new Request<>(LinearPositionSubsystem.class, new PositionBasedCondition(5, Range.between(0d, 10d)));

        testMediator.add(request);
        testMediator.onPeriodic();
        Request expectedRequest = testMediator.getCurrentRequest();
        assertThat(expectedRequest).isEqualTo(request);
        callPeriodic(20, testMediator, linearPositionSubsystem);

        testMediator.add(request2);
        testMediator.onPeriodic();
        expectedRequest = testMediator.getCurrentRequest();
        assertThat(expectedRequest).isEqualTo(request2);
    }

    @Test
    public void mediator_WHEN_FoundConflict_SHOULD_AddRequest() {
        TestMediator testMediator = new TestMediator();
        makeConflicts();
        testMediator.registerColleague(stateSubsystem);
        testMediator.registerColleague(linearPositionSubsystem);
        testMediator.registerConflict(positionConflicts);
        testMediator.registerConflict(stateConflicts);

        Request request = new Request<>(LinearPositionSubsystem.class, new PositionBasedCondition(90, Range.between(85d, 95d)));
        Request request2 = new Request<>(StateSubsystem.class, new StateBasedCondition(State.OPEN));

        testMediator.add(request);
        testMediator.add(request2);
        Queue<Deque<Request>> list = testMediator.getExecuteList();
        assertThat(list.size()).isEqualTo(2);

        callPeriodic(1, testMediator, linearPositionSubsystem);

        assertThat(testMediator.getCurrentRequest()).isNotEqualTo(request).isNotEqualTo(request2);
    }

    @Test
    public void mediator_WHEN_EncounteredConflict_SHOULD_ResolveConflict() {
        TestMediator testMediator = new TestMediator();
        testMediator.registerColleague(stateSubsystem);
        testMediator.registerColleague(linearPositionSubsystem);
        testMediator.registerConflict(new PositionStateBasedConflict(LinearPositionSubsystem.class,
                new PositionBasedCondition(90, Range.between(85d, 95d)),
                MediatorTestFixtures.StateSubsystem.class,
                new StateBasedCondition(State.OPEN),
                State.CLOSE, true));
        testMediator.registerConflict(stateConflicts);

        Request positionRequest = new Request<>(LinearPositionSubsystem.class, new PositionBasedCondition(90, Range.between(85d, 95d)));
        Request stateRequest = new Request<>(StateSubsystem.class, new StateBasedCondition(State.OPEN));

        testMediator.add(stateRequest);
        callPeriodic(2, testMediator);
        assertThat(stateSubsystem.getCurrentCondition().getConditionValue()).isEqualTo(State.OPEN);
        testMediator.add(positionRequest);

        callPeriodic(90, testMediator, linearPositionSubsystem);

        assertThat(stateSubsystem.getCurrentCondition().getConditionValue()).isEqualTo(State.CLOSE);
        assertThat(linearPositionSubsystem.getCurrentCondition().getConditionValue()).isEqualTo(90d);

    }

    @Test
    public void mediator_WHEN_EncounteredStateConflictWithTwoEnums_SHOULD_ResolveConflict() {
        TestMediator testMediator = new TestMediator();

        testMediator.registerColleague(stateSubsystem);
        testMediator.registerColleague(stateSubsystemDeux);
        testMediator.registerConflict(new StateBasedConflict(StateSubsystem.class,
                new StateBasedCondition(State.OPEN),
                StateSubsystemDeux.class,
                new StateBasedCondition<StateDeux>(StateDeux.OPEN), StateDeux.STOP, State.STOP, true));

        testMediator.add(new Request(StateSubsystemDeux.class, new StateBasedCondition(StateDeux.OPEN)));
        testMediator.add(new Request(StateSubsystem.class, new StateBasedCondition(State.OPEN)));

        callPeriodic(6, testMediator);

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
                new StateBasedCondition<State>(State.OPEN), State.CLOSE, StateDeux.STOP, true));

        stateSubsystemDeux.set(StateDeux.OPEN);
        testMediator.add(new Request(StateSubsystem.class, new StateBasedCondition(State.OPEN)));
        callPeriodic(3, testMediator);

        assertThat(stateSubsystemDeux.getCurrentCondition().getConditionValue()).isEqualTo(StateDeux.STOP);
    }

    @Test
    public void mediator_WHEN_RequestTakesTooLong_SHOULD_ClearOutRequest() {
        TestMediator testMediator = new TestMediator();
        testMediator.registerColleague(linearPositionSubsystem);
        testMediator.registerColleague(deadPositionSubsystem);
        testMediator.registerConflict(positionConflicts);

        testMediator.add(new Request<>(LinearPositionSubsystem.class, new PositionBasedCondition(35, Range.between(30d ,40d))));
        testMediator.add(new Request<>(DeadPositionSubsystem.class, new PositionBasedCondition(25, Range.between(20d, 30d))));
        testMediator.add(new Request<>(LinearPositionSubsystem.class, new PositionBasedCondition(5, Range.between(0d, 10d))));

        callPeriodic(Mediator.MAX_CONSECUTIVE_PERIODIC_CALLS * 2, testMediator, linearPositionSubsystem);

        assertThat(testMediator.getExecuteList().size()).isEqualTo(0);
        assertThat(linearPositionSubsystem.getCurrentCondition().getConditionValue()).isCloseTo(5d, Offset.offset(0d));
    }

    @Test
    public void mediator_SHOULD_HandlePositionBasedConflict_WITH_ParallelActions(){
        TestMediator testMediator = new TestMediator();
        DataRecorder dataRecorder = new DataRecorder();
        testMediator.registerColleague(linearPositionSubsystem, horizontalPositionSubsystem);
        testMediator.registerConflict(new PositionBasedConflict(HorizontalPositionSubsystem.class,
                new PositionBasedCondition(10, Range.between(5d, 20d)),
                LinearPositionSubsystem.class,
                new PositionBasedCondition(10, Range.between(5d, 20d)), false));

        testMediator.add(new Request<>(HorizontalPositionSubsystem.class, new PositionBasedCondition(10d, Range.between(10d, 10d))));
        testMediator.add(new Request<>(LinearPositionSubsystem.class, new PositionBasedCondition(10d, Range.between(10d, 10d))));

        callPeriodic(20, testMediator, horizontalPositionSubsystem, linearPositionSubsystem);

        assertThat(horizontalPositionSubsystem.getCurrentCondition().getConditionValue()).isEqualTo(10d);
        assertThat(linearPositionSubsystem.getCurrentCondition().getConditionValue()).isEqualTo(20d);

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


