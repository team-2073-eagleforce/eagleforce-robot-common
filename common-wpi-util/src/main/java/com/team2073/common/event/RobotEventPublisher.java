package com.team2073.common.event;

import com.team2073.common.periodic.PeriodicRunnable;
import com.team2073.common.util.ExceptionUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author pbriggs
 */
public class RobotEventPublisher implements PeriodicRunnable {

    // TODO:
    // -Create shutdown event.
    //      -Either:
    //          -Tie this to the user button on the RIO
    //          -Setup a capacitor to give us 2 seconds after kill switch (create trigger on main voltage loss)

    // Places to integrate:
    // -PositionalMechanismController#updateHoldPosition()

    public enum RobotStateEvent {
        PERIODIC(-1),
        ENABLED(-1),
        DISABLED(-1),
        AUTONOMOUS_START(3),
        AUTONOMOUS_END(4),
        TELEOP_START(5),
        TELEOP_END(6),
        TEST_START(7),
        TEST_END(8);

        private int eventNumber;

        private RobotStateEvent(int eventNumber) {
            this.eventNumber = eventNumber;
        }

        public int getEventNumber() {
            return eventNumber;
        }

        public RobotStateEvent getRobotStateEvent(int eventNumber) {
            for (RobotStateEvent l : RobotStateEvent.values()) {
                if (l.eventNumber == eventNumber) return l;
            }
            throw new IllegalArgumentException("Number not in range of index");
        }
    }

    private RobotStateEvent currentEvent;
    private Map<RobotStateEvent, LinkedList<EventListener>> instancesMap = new HashMap<>();

    public RobotEventPublisher() {
        autoRegisterWithPeriodicRunner();
        for (RobotStateEvent event : RobotStateEvent.values())
            instancesMap.put(event, new LinkedList<>());
    }

    public void setCurrentEvent(RobotStateEvent currentEvent) {
        // TODO: What the heck is this doing?
        if((currentEvent.getEventNumber() & 1) != 0 && currentEvent.getEventNumber() != -1) {
            this.currentEvent = currentEvent.getRobotStateEvent(currentEvent.getEventNumber()+1);
        }else {
            this.currentEvent = currentEvent;
        }
    }

    public void register(RobotStateEvent event, EventListener eventListener) {
        instancesMap.get(event).add(eventListener);
    }

    @Override
    public void onPeriodic() {
        // This will call every instance every periodic loop. Need to fix this.
        instancesMap.get(currentEvent).forEach(instance ->
                ExceptionUtil.suppressVoid(instance::onEvent, instance.getClass().getSimpleName() + " ::onEvent"));
    }
}
