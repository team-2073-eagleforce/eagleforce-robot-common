package com.team2073.common.listeners;

import com.team2073.common.util.ExceptionUtil;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author pbriggs
 */
public class ListenerRegistry {
    private static RobotStateEvent currentEvent;
    private static Map<RobotStateEvent, LinkedList<EventListener>> instancesMap = new HashMap<>();

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

    public ListenerRegistry() {
        for (RobotStateEvent event : RobotStateEvent.values())
            instancesMap.put(event, new LinkedList<>());
    }

    public static void setCurrentEvent(RobotStateEvent currentEvent) {
        if((currentEvent.getEventNumber() & 1) != 0 && currentEvent.getEventNumber() != -1) {
            ListenerRegistry.currentEvent = currentEvent.getRobotStateEvent(currentEvent.getEventNumber()+1);
        }else {
            ListenerRegistry.currentEvent = currentEvent;
        }
    }

    public static void registerInstance(RobotStateEvent event, EventListener eventListener) {
        instancesMap.get(event).add(eventListener);
    }


    public static void runEventListeners() {
        instancesMap.get(currentEvent).forEach(instance ->
                ExceptionUtil.suppressVoid(instance::onEvent, instance.getClass().getSimpleName() + " ::onEvent"));
    }
}
