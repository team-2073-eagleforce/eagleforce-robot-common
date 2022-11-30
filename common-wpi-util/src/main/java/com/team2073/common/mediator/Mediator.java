package com.team2073.common.mediator;

import com.team2073.common.assertion.Assert;
import com.team2073.common.mediator.Tracker.Tracker;
import com.team2073.common.mediator.condition.Condition;
import com.team2073.common.mediator.conflict.Conflict;
import com.team2073.common.mediator.conflict.ConflictMap;
import com.team2073.common.mediator.request.Request;
import com.team2073.common.mediator.subsys.ColleagueSubsystem;
import com.team2073.common.mediator.subsys.SubsystemMap;
import com.team2073.common.periodic.PeriodicRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * Manages how {@link ColleagueSubsystem}s interact by checking for and resolving Conflicts between
 * {@link Request}s and {@link Condition}s.
 *
 * <h3>Use</h3>
 * Call {@link #onPeriodic()} in a periodic method like Robot.periodic() and add to the {@link ConflictMap} and {@link SubsystemMap}
 * from robot commands using {@link #add(Request)} to pass in a movement which will be iterated over.
 *
 * <h3>Configuration</h3>
 * Needs the following to know which subsystems to 'mediate':
 * <ul>
 * <li>{@link ConflictMap}</li>
 * <li>{@link SubsystemMap}</li>
 * <li>{@link Tracker}</li>
 * </ul>
 */
public class Mediator implements PeriodicRunnable {
    private Map<Class, ColleagueSubsystem> subsystemMap;
    private Map<Class, ArrayList<Conflict>> conflictMap;
    private Tracker subsystemTracker;
    private Deque<Deque<Request>> executeList = new LinkedList<>();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public void init(Map<Class, ColleagueSubsystem> subsystemMap, Map<Class, ArrayList<Conflict>> conflictMap, Tracker subsystemTracker) {
        Assert.assertNotNull(subsystemMap, "subsystemMap");
        Assert.assertNotNull(conflictMap, "conflictMap");
        Assert.assertNotNull(subsystemTracker, "subsystemTracker");

        this.subsystemMap = subsystemMap;
        this.conflictMap = conflictMap;
        this.subsystemTracker = subsystemTracker;
        autoRegisterWithPeriodicRunner();
    }

    /**
     * Add a request to the list for processing.
     *
     * @param request The Condition for the subsystem to be in
     */
    public void add(Request request) {
        Assert.assertNotNull(request, "request");
        Deque<Request> requestList = new LinkedList<>();
        logger.debug("Added request: [{}].", request.getName());

        requestList.add(request);
        executeList.add(requestList);

    }

    /**
     * Call this method continuously. Iterates through the list of {@link Request}s and attempts to execute them,
     * first resolving their conflicts if any.
     */
    @Override
    public void onPeriodic() {
        Iterator<Deque<Request>> exeItr = executeList.iterator();

        if (exeItr.hasNext()) {
            Deque<Request> requestList = exeItr.next();
            logger.trace("Iterating through requestList [{}]", requestList.toString());
            Iterator<Request> itr = requestList.descendingIterator();
            if (itr.hasNext()) {
                Request request = itr.next();
                //TODO point of interest
                logger.debug("RequestedCondition: [{}]... ActualPosition: [{}]", request.getCondition(), subsystemTracker.findSubsystemCondition(request.getSubsystem()));
                logger.debug("Requested position is actual position: [{}]", request.getCondition().isInCondition(subsystemTracker.findSubsystemCondition(request.getSubsystem())));
                if (!request.getCondition().isInCondition(subsystemTracker.findSubsystemCondition(request.getSubsystem()))) {
                    execute(request);
                } else {
                    logger.debug("Finished request with [{}] in condition: [{}]", request.getSubsystem(), subsystemTracker.findSubsystemCondition(request.getSubsystem()));
                    itr.remove();
                }
            } else {
                exeItr.remove();
            }
        }
    }

    /**
     * Resolves any conflicts with the Request and then moves the subsystem.
     *
     * @param request the request to be fulfilled
     */
    public void execute(Request request) {
        Assert.assertNotNull(request, "request");
        ArrayList<Conflict> conflicts = findConflicts(request);
        Deque<Request> requestList = new LinkedList<>();

        Condition condition = request.getCondition();
        Class subsystem = request.getSubsystem();

        if (conflicts.isEmpty()) {
            logger.debug("Executing request [{}]. Conflicts: [{}]", request.getName(), conflicts.size());
            subsystemMap.get(subsystem).set(condition.getConditionValue());
        } else {
            for (Conflict conflict : conflicts) {
                logger.debug("Conflict [{}]", conflict.toString());
                logger.debug("Origin Condition: [{}]", conflict.getOriginCondition().toString());
                logger.debug("Conflicting Condition: [{}]", conflict.getConflictingCondition().toString());

                Request listRequest = createConflictRequest(conflict);
                requestList.add(listRequest);
                logger.debug("Added request: [{}].", listRequest.getName());
                executeList.addFirst(requestList);
//                execute(listRequest);
            }
        }

        logger.debug("Executing request [{}] complete", request.getName());
    }

    /**
     * Uses a {@link ConflictMap} to determine whether the requested condition is conflicting with current subsystem positions
     *
     * @param request the request that is being checked
     * @return found conflicts in a list or just an empty list if there aren't any
     * <p>
     */
    private ArrayList<Conflict> findConflicts(Request request) {
        logger.debug("Finding conflicts for Request [{}]...", request.getName());
        Class subsystem = request.getSubsystem();
        ArrayList<Conflict> possibleConflicts = conflictMap.get(subsystem);
        ArrayList<Conflict> conflicts = new ArrayList<>();

        if (possibleConflicts == null) {
            return conflicts;
        }

        for (Conflict conflict : possibleConflicts) {
            boolean isConflicting = conflict.isConflicting(conflict, request, subsystemTracker.findSubsystemCondition(conflict.getConflictingSubsystem()));
            if (isConflicting) {
                logger.debug("Adding conflicting conflict: [{}].", conflict.getName());
                conflicts.add(conflict);
            }
        }

        logger.debug("Finding conflicts for Request [{}] complete.", request.getName());
        return conflicts;
    }


    /**
     * @return a {@link Request} that resolves the {@link Conflict}
     */
    public Request createConflictRequest(Conflict conflict) {
        return new Request(conflict.getConflictingSubsystem(),
                conflict.getResolution(conflict.getConflictingCondition(),
                        subsystemMap.get(conflict.getConflictingSubsystem())));
    }
}
