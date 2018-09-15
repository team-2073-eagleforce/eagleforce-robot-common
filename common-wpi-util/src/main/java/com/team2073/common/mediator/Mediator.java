package com.team2073.common.mediator;

import com.team2073.common.assertion.Assert;
import com.team2073.common.mediator.Tracker.Tracker;
import com.team2073.common.mediator.condition.Condition;
import com.team2073.common.mediator.conflict.Conflict;
import com.team2073.common.mediator.conflict.ConflictMap;
import com.team2073.common.mediator.request.Request;
import com.team2073.common.mediator.subsys.ColleagueSubsystem;
import com.team2073.common.mediator.subsys.SubsystemMap;
import com.team2073.common.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;

/**
 * Manages how ColleagueSubsystems interact by checking for Conflicts and resolving them
 *
 * <h3>Use</h3>
 * Call {@link #periodic()} in a periodic method like Robot.Periodic and add to the {@link ConflictMap} and {@link SubsystemMap}<br\>
 * In robot commands, call {@link #add(Request)} to pass in a movement which will be iterated over
 *
 * <h3>Configuration</h3>
 * Needs <ul>
 * <li>{@link ConflictMap}</li>
 * <li>{@link SubsystemMap}</li>
 * <li>{@link Tracker}</li>
 * </ul>
 * to know which subsystems to 'mediate'
 */
public class Mediator {
    private Map<Class, ColleagueSubsystem> subsystemMap;
    private Map<Class, ArrayList<Conflict>> conflictMap;
    private Tracker subsystemTracker;

    private ArrayList<ArrayList<Request>> executeList = new ArrayList<>();

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public void init(Map<Class, ColleagueSubsystem> subsystemMap, Map<Class, ArrayList<Conflict>> conflictMap, Tracker subsystemTracker) {
        Assert.assertNotNull(subsystemMap, "subsystemMap");
        Assert.assertNotNull(conflictMap, "conflictMap");
        Assert.assertNotNull(subsystemTracker, "subsystemTracker");

        this.subsystemMap = subsystemMap;
        this.conflictMap = conflictMap;
        this.subsystemTracker = subsystemTracker;
        LogUtil.infoInit(this.getClass(), logger);
    }

    /**
     * For requested subsystem movements, the {@link #execute(Request)} method is ran on each <br/>
     * Checks Request lists in the {@link #executeList} and removes them if they're done <br\>
     */
    public void periodic() {
        executeList.removeIf(list -> list.isEmpty());
        for (ArrayList<Request> requestList : executeList) {
            ListIterator itr = requestList.listIterator(requestList.size());
            while (itr.hasPrevious()) {
                execute((Request) itr.previous());
            }
        }
    }

    /**
     * Either {@link #execute(Request)} the request or creates new requests that resolve conflicts
     * @param request the Condition for the subsystem to be in
     * <p>
     */
    public void add(Request request) {
        Assert.assertNotNull(request, "request");

        ArrayList<Conflict> conflicts = findConflicts(request);
        if (conflicts == null || conflicts.isEmpty()) {
            execute(request);
        } else {
            ArrayList<Request> requestList = new ArrayList<>();
            for (Conflict conflict : conflicts) {
                requestList.add(request);

                logger.debug("Added request: " + request.getName());
                Request listRequest = new Request(conflict.getConflictingSubsystem(),
                        conflict.getResolution(conflict.getConflictingCondition(),
                                subsystemMap.get(conflict.getConflictingSubsystem())));
                ArrayList<Conflict> innerConflicts = findConflicts(listRequest);
                for (Conflict innerConflict : innerConflicts) {
                    Request innerConflictRequest = new Request(innerConflict.getConflictingSubsystem(),
                            innerConflict.getResolution(innerConflict.getConflictingCondition(),
                                    subsystemMap.get(conflict.getConflictingSubsystem())));
                    requestList.add(innerConflictRequest);
                    logger.debug("Added request: " + innerConflictRequest.getName());
                }
                requestList.add(listRequest);
                logger.debug("Added request: " + listRequest.getName());
            }
            executeList.add(requestList);
        }
    }

    /**
     * Checks if there are no conflicts with the request and then moves the subsystem
     *
     * @param request the request to be fulfilled <br\>
     */
    public void execute(Request request) {
        Assert.assertNotNull(request, "request");
        logger.debug("Executing request [{}]", request.getName());
        ArrayList<Conflict> conflicts = findConflicts(request);
        logger.debug("There are [{}] conflicts.", conflicts.size());
        for (Conflict conf : conflicts) {
            logger.debug(conf.toString());
            logger.debug("ORIGIN: " + conf.getOriginCondition().toString());
            logger.debug("CONFLICT: " + conf.getConflictingCondition().toString());
        }
        Condition condition = request.getCondition();
        Class subsystem = request.getSubsystem();

        if (conflicts != null || !conflicts.isEmpty()) {
            subsystemMap.get(subsystem).set(condition.getConditionValue());
            removeRequest(request);
            return;
        }

        logger.debug("Executing request [{}] complete", request.getName());
    }

    /**
     * Removes requests in the request lists within the {@link #executeList}
     *
     * @param request the request to be removed
     */
    private void removeRequest(Request request) {
        Assert.assertNotNull(request, "request");
        logger.debug("Removing request [{}]", request.getName());

        for (Iterator<ArrayList<Request>> executeListIterator = executeList.iterator(); executeListIterator.hasNext(); ) {
            ArrayList<Request> list = executeListIterator.next();
            list.removeIf(listRequest -> listRequest == request);
        }
        logger.debug("Removing request [{}] complete", request.getName());
    }


    /**
     * Uses a {@link ConflictMap}  to determine whether the requested condition is conflicting with current subsystem positions
     *
     * @param request the request that is being checked
     * @return found conflicts in a list or just an empty list if there aren't any
     * <p>
     */
    private ArrayList<Conflict> findConflicts(Request request) {
        logger.debug("finding conflicts");
        Class subsystem = request.getSubsystem();
        ArrayList<Conflict> possibleConflicts = conflictMap.get(subsystem);
        ArrayList<Conflict> conflicts = new ArrayList<>();

        if (possibleConflicts == null) {
            return conflicts;
        }

        for (Conflict conflict : possibleConflicts) {
            boolean isConflicting = conflict.isConflicting(conflict, request, subsystemTracker.findSubsystemCondition(conflict.getConflictingSubsystem()));
            logger.debug("isconflicting: " + isConflicting);
            if (isConflicting) {
                conflicts.add(conflict);
            }
        }

        return conflicts;
    }
}
