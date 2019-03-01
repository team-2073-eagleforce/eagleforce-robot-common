package com.team2073.common.mediator;

import com.google.common.annotations.VisibleForTesting;
import com.team2073.common.assertion.Assert;
import com.team2073.common.mediator.condition.Condition;
import com.team2073.common.mediator.conflict.Conflict;
import com.team2073.common.mediator.request.Request;
import com.team2073.common.mediator.subsys.ColleagueSubsystem;
import com.team2073.common.periodic.PeriodicRunnable;
import org.apache.commons.collections4.queue.UnmodifiableQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Manages how {@link ColleagueSubsystem}s interact by checking for and resolving {@link Conflict}s between
 * {@link Request}s and the {@link Condition}s {@link ColleagueSubsystem}s are in.
 *
 * <h3>Use</h3>
 * From robot commands use {@link #add(Request)} to pass in a {@link Request} with a
 * {@link Condition} which will be iterated over.
 *
 * <h3>Configuration</h3>
 * For setup {@link #registerColleague(ColleagueSubsystem[])} in a {@link ColleagueSubsystem}'s constructor
 * and {@link #registerConflict(Conflict[])} in an init method
 * <ul>
 * </ul>
 */
public class Mediator implements PeriodicRunnable {
    private final Map<Class<? extends ColleagueSubsystem>, ColleagueSubsystem> subsystemMap = new HashMap<>();
    private final Map<Class<? extends ColleagueSubsystem>, ArrayList<Conflict>> conflictMap = new HashMap<>();
    private final Deque<Deque<Request>> executeList = new LinkedList<>();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private int periodicCalls = 0;
    public static final int MAX_CONSECUTIVE_PERIODIC_CALLS = 500;

    public Mediator() {
        autoRegisterWithPeriodicRunner();
    }

    /**
     * * Adds to the subsystem map for the {@link Mediator} to keep track of
     *
     * @param colleagueSubsystems
     */
    public void registerColleague(ColleagueSubsystem... colleagueSubsystems) {
        for (ColleagueSubsystem colleagueSubsystem : colleagueSubsystems) {
            Assert.assertNotNull(colleagueSubsystem, "colleagueSubsystem");
            subsystemMap.put(colleagueSubsystem.getClass(), colleagueSubsystem);
            logger.debug("Registered [{}] as Colleague", colleagueSubsystem);
        }
    }

    public void registerColleague(List<ColleagueSubsystem> colleagueSubsystems) {
        for (ColleagueSubsystem colleagueSubsystem : colleagueSubsystems) {
            registerColleague(colleagueSubsystem);
        }
    }

    /**
     * * Adds to the conflict map to be checked periodically
     *
     * @param conflicts
     */
    public void registerConflict(Conflict... conflicts) {
        for (Conflict conflict : conflicts) {
            if (conflictMap.get(conflict.getOriginSubsystem()) == null) {
                ArrayList<Conflict> originConflicts = new ArrayList<>();
                conflictMap.put(conflict.getOriginSubsystem(), originConflicts);
                logger.debug("Creating conflict list for: [{}] and [{}]", conflict.getOriginSubsystem(), conflict.getConflictingSubsystem());
            }
            if (conflictMap.get(conflict.getConflictingSubsystem()) == null) {
                ArrayList<Conflict> inverseConflicts = new ArrayList<>();
                conflictMap.put(conflict.getConflictingSubsystem(), inverseConflicts);
                logger.debug("Creating conflict list for: [{}]", conflict.getConflictingSubsystem());
            }
            conflictMap.get(conflict.getOriginSubsystem()).add(conflict);
            if (conflict.getCanInvert()) {
                conflictMap.get(conflict.getConflictingSubsystem()).add(conflict.invert());
            }
            logger.debug("Adding conflict: [{}] and [{}]", conflict.getName(), conflict.invert().getName());

        }
    }

    public void registerConflict(List<Conflict> conflicts) {
        for (Conflict conflict : conflicts) {
            registerConflict(conflict);
        }
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
     * This method is called continuously. Iterates through the list of {@link Request}s and attempts to execute them,
     * resolving their conflicts if any.
     * <p>
     * After 15 periodic loops of unresponsiveness, it will clear the current {@link Request} and go to the next one
     */
    @Override
    public void onPeriodic() {
        Deque<Request> currentRequestList = executeList.peekFirst();
        if (currentRequestList != null) {
            logger.trace("Iterating through requestList [{}]", Arrays.toString(currentRequestList.toArray()));
            Request request = currentRequestList.peekLast();
            if (request != null) {
                currentRequest = request;
                Condition currentCondition = subsystemMap.get(request.getSubsystem()).getCurrentCondition();
                logger.debug("RequestedCondition: [{}]... ActualPosition: [{}]", request.getCondition(), currentCondition);
                logger.debug("Requested position is actual position: " + currentCondition);
                if (!request.getCondition().isInCondition(currentCondition)) {
                    periodicCalls += 1;
                    if (periodicCalls > MAX_CONSECUTIVE_PERIODIC_CALLS) {
                        logger.warn("Removing request [{}] because of excessive periodic calls" + request.getName());
                        periodicCalls = 0;
                        currentRequestList.removeLast();
                    } else {
                        execute(request);
                    }
                } else {
                    logger.debug("Finished request with [{}] in condition: [{}]", request.getSubsystem(), subsystemMap.get(request.getSubsystem()).getCurrentCondition());
                    currentRequestList.removeLast();
                    periodicCalls = 0;
                }
            } else {
                logger.debug("Finished and removing current request list");
                executeList.removeFirst();
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

        if (conflicts.isEmpty() || request.getParallelConflict() != null) {
            if (request.getParallelConflict() != null) {
                Conflict parallelConflict = request.getParallelConflict();
                logger.debug("Executing parallel Conflict [{}]", parallelConflict.getName());
                Condition requestedParallelCondition = parallelConflict.getOriginParallelResolution(subsystemMap.get(parallelConflict.getOriginSubsystem()),
                        subsystemMap.get(parallelConflict.getConflictingSubsystem()));
                    subsystemMap.get(parallelConflict.getOriginSubsystem()).set(requestedParallelCondition.getConditionValue());

                System.out.println("parallel conflict output: " + requestedParallelCondition.getConditionValue());
            }
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
            boolean isConflicting = possibleConflict.isRequestConflicting(request,
                    subsystemMap.get(possibleConflict.getConflictingSubsystem()).getCurrentCondition(),
                    subsystemMap.get(possibleConflict.getOriginSubsystem()).getCurrentCondition());
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
        Request request = new Request<>(conflict.getConflictingSubsystem(),
                conflict.getResolution(conflict.getConflictingCondition(),
                        subsystemMap.get(conflict.getConflictingSubsystem())));

        if (conflict.getParallelism()) {
            request.setParallelConflict(conflict);
        }
        return request;
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
    Queue<Deque<Request>> getExecuteList() {
        return UnmodifiableQueue.unmodifiableQueue(executeList);
    }

    @VisibleForTesting
    private Request currentRequest;

    @VisibleForTesting
    Request getCurrentRequest() {
        return currentRequest;
    }
}
