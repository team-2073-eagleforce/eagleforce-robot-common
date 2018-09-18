package com.team2073.common.mediator;

import com.team2073.common.assertion.Assert;
import com.team2073.common.mediator.Tracker.Tracker;
import com.team2073.common.mediator.condition.Condition;
import com.team2073.common.mediator.conflict.Conflict;
import com.team2073.common.mediator.conflict.ConflictMap;
import com.team2073.common.mediator.request.Request;
import com.team2073.common.mediator.subsys.ColleagueSubsystem;
import com.team2073.common.mediator.subsys.SubsystemMap;
import com.team2073.common.periodic.PeriodicAware;
import com.team2073.common.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
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
public class Mediator implements PeriodicAware {
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
     * Call this method continuously. Iterates through the list of {@link Request}s and attempts to execute them,
     * first resolving their conflicts if any.
     */
//    /**
//     * Call this method continuously. Tries to execute the current Request, first resolving its conflicts, if any.
//     */
    @Override
    public void onPeriodic() {
        executeList.removeIf(list -> list.isEmpty());
        // I feel like you will only actually want to process the top Request here, remember this needs to run really
        // fast. Each time it should check if the top Request has finished and either remove it or execute it
        // If this is true change the javadoc to the commented one
        for (ArrayList<Request> requestList : executeList) {
            ListIterator<Request> itr = requestList.listIterator(requestList.size());
            while (itr.hasPrevious()) {
                execute(itr.previous());
            }
        }
    }

    /**
     * Add a request to the list for processing.
     * @param request The Condition for the subsystem to be in
     */
    public void add(Request request) {
        Assert.assertNotNull(request, "request");

        ArrayList<Conflict> conflicts = findConflicts(request);
        ArrayList<Request> requestList = new ArrayList<>();
        for (Conflict conflict : conflicts) {
            requestList.add(request);

            // What is the difference between these different places
            logger.trace("Added request: [{}].", request.getName());
            // IDEA is complaining about unchecked casting here. There's probably something missing from your class
            // hierarchy and generics
            Request listRequest = new Request(conflict.getConflictingSubsystem(),
                    conflict.getResolution(conflict.getConflictingCondition(),
                            subsystemMap.get(conflict.getConflictingSubsystem())));
            ArrayList<Conflict> innerConflicts = findConflicts(listRequest);
            for (Conflict innerConflict : innerConflicts) {
                Request innerConflictRequest = new Request(innerConflict.getConflictingSubsystem(),
                        innerConflict.getResolution(innerConflict.getConflictingCondition(),
                                subsystemMap.get(conflict.getConflictingSubsystem())));
                requestList.add(innerConflictRequest);
                logger.trace("Added request: [{}].", innerConflictRequest.getName());
            }
            requestList.add(listRequest);
            logger.trace("Added request: [{}].", listRequest.getName());
        }
        executeList.add(requestList);
    }

    /**
     * Resolves any conflicts with the Request and then moves the subsystem.
     *
     * @param request the request to be fulfilled <br\>
     */
    public void execute(Request request) {
        Assert.assertNotNull(request, "request");
        ArrayList<Conflict> conflicts = findConflicts(request);
        logger.trace("Executing request [{}]. Conflicts: [{}]", request.getName(), conflicts.size());
        for (Conflict conf : conflicts) {
            logger.trace("Conflict [{}]", conf.toString());
            logger.trace("Origin Condition: [{}]", conf.getOriginCondition().toString());
            logger.trace("Conflicting Condition: [{}]", conf.getConflictingCondition().toString());

            ColleagueSubsystem conflictingSubsystem = subsystemMap.get(conf.getConflictingSubsystem());
            conflictingSubsystem.set(conf.getResolution(subsystemTracker.findSubsystemCondition(conf.getConflictingSubsystem()), conflictingSubsystem));
        }
        Condition condition = request.getCondition();
        Class subsystem = request.getSubsystem();

        if (!conflicts.isEmpty()) {
            subsystemMap.get(subsystem).set(condition.getConditionValue());
            removeRequest(request);
            return;
        }

        logger.trace("Executing request [{}] complete", request.getName());
    }

    /**
     * Removes requests in the request lists within the {@link #executeList}
     *
     * @param request the request to be removed
     */
    private void removeRequest(Request request) {
        Assert.assertNotNull(request, "request");
        logger.trace("Removing request [{}]", request.getName());

        for (Iterator<ArrayList<Request>> executeListIterator = executeList.iterator(); executeListIterator.hasNext();) {
            ArrayList<Request> list = executeListIterator.next();
            list.removeIf(listRequest -> listRequest == request);
        }
        logger.trace("Removing request [{}] complete", request.getName());
    }


    /**
     * Uses a {@link ConflictMap} to determine whether the requested condition is conflicting with current subsystem positions
     *
     * @param request the request that is being checked
     * @return found conflicts in a list or just an empty list if there aren't any
     * <p>
     */
    private ArrayList<Conflict> findConflicts(Request request) {
        logger.trace("Finding conflicts for Request [{}]...", request.getName());
        Class subsystem = request.getSubsystem();
        ArrayList<Conflict> possibleConflicts = conflictMap.get(subsystem);
        ArrayList<Conflict> conflicts = new ArrayList<>();

        if (possibleConflicts == null) {
            return conflicts;
        }

        for (Conflict conflict : possibleConflicts) {
            boolean isConflicting = conflict.isConflicting(conflict, request, subsystemTracker.findSubsystemCondition(conflict.getConflictingSubsystem()));
            if (isConflicting) {
                logger.trace("Adding conflicting conflict: [{}].", conflict.getName());
                conflicts.add(conflict);
            }
        }

        logger.trace("Finding conflicts for Request [{}] complete.", request.getName());
        return conflicts;
    }
}
