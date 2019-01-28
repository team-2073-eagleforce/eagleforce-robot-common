package com.team2073.common.mediator;

import com.google.common.annotations.VisibleForTesting;
import com.team2073.common.assertion.Assert;
import com.team2073.common.mediator.condition.Condition;
import com.team2073.common.mediator.conflict.Conflict;
import com.team2073.common.mediator.request.Request;
import com.team2073.common.mediator.subsys.ColleagueSubsystem;
import com.team2073.common.periodic.PeriodicRunnable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Manages how {@link ColleagueSubsystem}s interact by checking for and resolving {@link Conflict}s between
 * {@link Request}s and the {@link Condition}s {@link ColleagueSubsystem}s are in.
 *
 * <h3>Use</h3>
 * From robot commands use {@link #add(Request)} to pass in a {@link Request} with a
 * {@link Condition} which will be iterated over.
 *
 * <h3>Configuration</h3>
 * For setup {@link #registerColleague(ColleagueSubsystem)} in a {@link ColleagueSubsystem}'s constructor
 * and {@link #registerConflict(Conflict)} in an init method
 * <ul>
 * </ul>
 */
public class Mediator implements PeriodicRunnable {
    private final Map<Class<? extends ColleagueSubsystem>, ColleagueSubsystem> subsystemMap = new HashMap<>();
    private final Map<Class<? extends ColleagueSubsystem>, ArrayList<Conflict>> conflictMap = new HashMap<>();
    private final Deque<Deque<Request>> executeList = new LinkedList<>();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public Mediator() {
        autoRegisterWithPeriodicRunner();
    }

    public void registerColleague(ColleagueSubsystem... colleagueSubsystems) {
        registerColleague(Arrays.asList(colleagueSubsystems));
    }

    public void registerColleague(List<ColleagueSubsystem> colleagueSubsystems) {
        for (ColleagueSubsystem colleagueSubsystem : colleagueSubsystems) {
            registerColleague(colleagueSubsystem);
        }
    }

    /**
     * Adds to the subsystem map for the {@link Mediator} to keep track of
     * @param colleagueSubsystem
     */
    public void registerColleague(ColleagueSubsystem colleagueSubsystem) {
        Assert.assertNotNull(colleagueSubsystem, "colleagueSubsystem");
        subsystemMap.put(colleagueSubsystem.getClass(), colleagueSubsystem);
        logger.debug("Registered [{}] as Colleague", colleagueSubsystem);
    }

    public void registerConflict(Conflict... conflicts) {
        registerConflict(Arrays.asList(conflicts));
    }

    public void registerConflict(List<Conflict> conflicts) {
        for (Conflict conflict : conflicts) {
            registerConflict(conflict);
        }
    }

    /**
     * Adds to the conflict map to be checked periodically
     * Also adds the {@link Conflict} flipped
     * @param conflict
     */
    public void registerConflict(Conflict conflict) {
        if (conflictMap.get(conflict.getOriginSubsystem()) == null) {
            ArrayList<Conflict> conflicts = new ArrayList<>();
            ArrayList<Conflict> inverseConflicts = new ArrayList<>();
            conflictMap.put(conflict.getOriginSubsystem(), conflicts);
            conflictMap.put(conflict.getConflictingSubsystem(), inverseConflicts);
            logger.debug("Creating conflict list for: [{}] and [{}]", conflict.getOriginSubsystem(), conflict.getConflictingSubsystem());
        }
        conflictMap.get(conflict.getOriginSubsystem()).add(conflict);
        conflictMap.get(conflict.getConflictingSubsystem()).add(conflict.invert());
        logger.debug("Adding conflict: [{}] and [{}]", conflict.getName(), conflict.invert().getName());
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
     * This method is called continuously. Iterates through the list of {@link Request}s and attempts to execute them,
     * resolving their conflicts if any.
     *
     * After 15 periodic loops of unresponsiveness, it will clear the current {@link Request} and go to the next one
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
                Condition currentCondition = subsystemMap.get(request.getSubsystem()).getCurrentCondition();
                logger.debug("RequestedCondition: [{}]... ActualPosition: [{}]", request.getCondition(), currentCondition);
                logger.debug("Requested position is actual position: " + currentCondition);
                if (!request.getCondition().isInCondition(currentCondition)) {
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
    private void execute(Request request) {
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
            try {
                subsystemMap.get(possibleConflict.getConflictingSubsystem()).getCurrentCondition();
            } catch (Exception e) {
                logger.error("Conflicting Subsystem not registered");
                return conflicts;
            }
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
    private Request createConflictRequest(Conflict conflict) {
        return new Request<>(conflict.getConflictingSubsystem(),
                conflict.getResolution(conflict.getConflictingCondition(),
                        subsystemMap.get(conflict.getConflictingSubsystem())));
    }

    @VisibleForTesting
    Map<Class<? extends ColleagueSubsystem>, ArrayList<Conflict>> getConflictMap() {
        return Collections.unmodifiableMap(conflictMap);
    }

    @VisibleForTesting
    Map<Class<? extends ColleagueSubsystem>, ColleagueSubsystem> getSubsystemMap() {
        return Collections.unmodifiableMap(subsystemMap);
    }

    @VisibleForTesting
    List<Deque<Request>> getExecuteList() {
        return Collections.unmodifiableList(new ArrayList<>(executeList));
    }

    @VisibleForTesting
    private Request currentRequest;

    @VisibleForTesting
    Request getCurrentRequest() {
        return currentRequest;
    }
}
