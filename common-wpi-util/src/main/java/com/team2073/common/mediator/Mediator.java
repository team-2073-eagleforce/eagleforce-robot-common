package com.team2073.common.mediator;

import com.google.common.annotations.VisibleForTesting;
import com.team2073.common.assertion.Assert;
import com.team2073.common.mediator.condition.Condition;
import com.team2073.common.mediator.conflict.Conflict;
import com.team2073.common.mediator.conflict.ConflictMap;
import com.team2073.common.mediator.request.Request;
import com.team2073.common.mediator.subsys.ColleagueSubsystem;
import com.team2073.common.mediator.subsys.SubsystemMap;
import com.team2073.common.periodic.PeriodicRunnable;
import com.team2073.common.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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
 * </ul>
 */
public class Mediator implements PeriodicRunnable {
    private Map<Class<? extends ColleagueSubsystem>, ColleagueSubsystem> subsystemMap = new HashMap<>();
    private Map<Class<? extends ColleagueSubsystem>, ArrayList<Conflict>> conflictMap;
    private Deque<Deque<Request>> executeList = new LinkedList<>();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public void init(Map<Class<? extends ColleagueSubsystem>, ArrayList<Conflict>> conflictMap) {
        Assert.assertNotNull(conflictMap, "conflictMap");

        this.conflictMap = conflictMap;
        LogUtil.infoInit(this.getClass(), logger);
        autoRegisterWithPeriodicRunner();
    }

    public void registerColleague(Class<? extends ColleagueSubsystem> colleagueClass, ColleagueSubsystem colleagueSubsystem) {
        Assert.assertNotNull(colleagueClass, "colleagueClass");
        Assert.assertNotNull(colleagueSubsystem, "colleagueSubsystem");
        subsystemMap.put(colleagueClass, colleagueSubsystem);
        logger.debug("Registered [{}] as Colleague", colleagueSubsystem);
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

    private int periodicCalls = 0;

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
            Request request = requestList.peekLast();
            if (request != null) {
                currentRequest = request;
                logger.debug("RequestedCondition: [{}]... ActualPosition: [{}]", request.getCondition(), subsystemMap.get(request.getSubsystem()).getCurrentCondition());
                logger.debug("Requested position is actual position: " + request.getCondition().isInCondition(subsystemMap.get(request.getSubsystem()).getCurrentCondition()));
                if (!request.getCondition().isInCondition(subsystemMap.get(request.getSubsystem()).getCurrentCondition())) {
                    periodicCalls += 1;
                    if (periodicCalls > 15) {
                        logger.debug("Removing request [{}]" + request.getName());
                        periodicCalls = 0;
                        requestList.removeLast();
                    } else {
                        execute(request);
                    }
                } else {
                    logger.debug("Finished request with [{}] in condition: [{}]", request.getSubsystem(), subsystemMap.get(request.getSubsystem()).getCurrentCondition());
                    requestList.removeLast();
                }
            } else {
                exeItr.remove();
            }
        }
    }

    /**
     * Resolves any conflicts with the Request and then moves the subsystem.
     *
     * @param request the request to be fulfilled <br\>
     */
    public void execute(Request request) {
        Assert.assertNotNull(request, "request");
        ArrayList<Conflict> conflicts = findRequestConflicts(request);
        Deque<Request> requestList = executeList.peekFirst();

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
    private ArrayList<Conflict> findRequestConflicts(Request request) {
        logger.debug("Finding conflicts for Request [{}]...", request.getName());
        Class subsystem = request.getSubsystem();
        ArrayList<Conflict> possibleConflicts = conflictMap.get(subsystem);
        ArrayList<Conflict> conflicts = new ArrayList<>();

        if (possibleConflicts == null) {
            return conflicts;
        }

        for (Conflict possibleConflict : possibleConflicts) {
            boolean isConflicting = possibleConflict.isRequestConflicting(request, subsystemMap.get(possibleConflict.getConflictingSubsystem()).getCurrentCondition());
            if (isConflicting) {
                logger.debug("Adding conflicting conflict: [{}].", possibleConflict.getName());
                conflicts.add(possibleConflict);
            }
        }

        logger.debug("Finding conflicts for Request [{}] complete.", request.getName());
        return conflicts;
    }

    /**
     * @return a {@link Request} that resolves the {@link Conflict}
     */
    public Request createConflictRequest(Conflict conflict) {
        return new Request<>(conflict.getConflictingSubsystem(),
                conflict.getResolution(conflict.getConflictingCondition(),
                        subsystemMap.get(conflict.getConflictingSubsystem())));
    }

    @VisibleForTesting
    Map<Class<? extends ColleagueSubsystem>, ColleagueSubsystem> getSubsystemMap() {
        return subsystemMap;
    }

    @VisibleForTesting
    Deque<Deque<Request>> getExecuteList() {
        return executeList;
    }

    private Request currentRequest;

    @VisibleForTesting
    Request getCurrentRequest() {
        return currentRequest;
    }
}
