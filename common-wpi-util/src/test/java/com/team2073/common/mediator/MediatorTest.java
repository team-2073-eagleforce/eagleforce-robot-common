package com.team2073.common.mediator;

import com.team2073.common.mediator.MediatorTestFixtures.PositionSubsystem;
import com.team2073.common.mediator.MediatorTestFixtures.State;
import com.team2073.common.mediator.MediatorTestFixtures.StateSubsystem;
import com.team2073.common.mediator.MediatorTestFixtures.TestMediator;
import com.team2073.common.mediator.Tracker.PositionBasedTrackee;
import com.team2073.common.mediator.Tracker.StateBasedTrackee;
import com.team2073.common.mediator.Tracker.SubsystemTrackee;
import com.team2073.common.mediator.condition.Condition;
import com.team2073.common.mediator.condition.PositionBasedCondition;
import com.team2073.common.mediator.condition.StateBasedCondition;
import com.team2073.common.mediator.conflict.Conflict;
import com.team2073.common.mediator.conflict.PositionBasedConflict;
import com.team2073.common.mediator.conflict.StateBasedConflict;
import com.team2073.common.mediator.request.Request;
import com.team2073.common.mediator.subsys.ColleagueSubsystem;
import com.team2073.common.mediator.subsys.SubsystemStateCondition;
import com.team2073.common.util.ThreadUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class MediatorTest {
    private Tracker tracker = new Tracker();
    private PositionSubsystem positionSubsystem = new PositionSubsystem();
    private MediatorTestFixtures.StateSubsystem stateSubsystem = new StateSubsystem();

    private TestMediator testMediator = new TestMediator();

    private ArrayList<Conflict> positionConflicts = new ArrayList<>();
    private ArrayList<Conflict> stateConflicts = new ArrayList<>();

    private static Map<Class, ColleagueSubsystem> subsystemMap = new HashMap<>();
    private static Map<Class, ArrayList<Conflict>> conflictMap = new HashMap<>();

    private void makeConflicts() {
        positionConflicts.add(new PositionBasedConflict<>(PositionSubsystem.class,
                new PositionBasedCondition(85d, 95d, 90d),
                MediatorTestFixtures.StateSubsystem.class,
                new StateBasedCondition(State.OPEN)));

        stateConflicts.add(new StateBasedConflict<>(StateSubsystem.class,
                new StateBasedCondition(State.OPEN),
                PositionSubsystem.class,
                new PositionBasedCondition(85, 90, 85), State.CLOSE));

//        conflictMap.put(PositionSubsystem.class, positionConflicts);
//        conflictMap.put(MediatorTestFixtures.StateSubsystem.class, stateConflicts);
    }

    private void makeSubsystemMap() {
        subsystemMap.put(PositionSubsystem.class, positionSubsystem);
        subsystemMap.put(StateSubsystem.class, stateSubsystem);
    }

    @BeforeEach
    void setUp() {
        makeConflicts();
        makeSubsystemMap();

        testMediator.init(subsystemMap, conflictMap, tracker);
    }

    @Test
    public void mediator_WHEN_PositionSubsystemAskedToMove_SHOULD_Move() {
        tracker.registerTrackee(positionSubsystem);
        Request<PositionBasedCondition, PositionSubsystem> request = new Request<>(PositionSubsystem.class, new PositionBasedCondition(5, 15, 10));
        testMediator.add(request);
        testMediator.onPeriodic();

        assertThat(positionSubsystem.updateTracker()).isEqualTo(10);
    }

    @Test
    public void mediator_WHEN_StateSubsystemAskedToMove_SHOULD_Move() {
        tracker.registerTrackee(stateSubsystem);
        Request<StateBasedCondition, StateSubsystem> request = new Request<>(StateSubsystem.class, new StateBasedCondition(State.OPEN));
        testMediator.add(request);
        testMediator.onPeriodic();

        assertThat(stateSubsystem.updateTracker()).isEqualTo(State.OPEN);
    }

    @Test
    public void mediator_WHEN_FinishedWithRequest_SHOULD_RemoveFromList() {
        Request<PositionBasedCondition, PositionSubsystem> request = new Request<>(PositionSubsystem.class, new PositionBasedCondition(5, 15, 10));

        testMediator.add(request);
        Deque<Deque<Request>> list = testMediator.getExecuteList();

        assertThat(list.size()).isEqualTo(1);

        testMediator.onPeriodic();

        ThreadUtil.sleep(20);

        testMediator.onPeriodic();

        assertThat(list.size()).isEqualTo(0);
    }

    @Test
    public void mediator_WHEN_IteratingThroughRequests_SHOULD_IterateOverMostRecentlyAdded() {

    }

    @Test
    public void mediator_WHEN_FoundConflict_SHOULD_AddRequest() {

    }

    @Test
    public void mediator_WHEN_EncounteredConflict_SHOULD_ResolveConflict() {

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
                condition = new PositionBasedCondition(position - 5, position + 5, position);

            } else if (subsystem instanceof StateBasedTrackee) {
                condition = new StateBasedCondition((SubsystemStateCondition) subsystem.updateTracker());
            }

            return condition;
        }
    }

}


