package com.team2073.common.mediator;

import com.team2073.common.assertion.Assert;
import com.team2073.common.mediator.Tracker.Tracker;
import com.team2073.common.mediator.condition.Condition;
import com.team2073.common.mediator.conflict.Conflict;
import com.team2073.common.mediator.request.Request;
import com.team2073.common.mediator.subsys.ColleagueSubsystem;
import com.team2073.common.position.zeroer.Zeroer;
import com.team2073.common.util.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;

/**
 *
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
     * Periodically checks the request added to the executeList and either removes them or starts executing them
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

    private void removeRequest(Request request) {
        Assert.assertNotNull(request, "request");
        logger.debug("Removing request [{}]", request.getName());

        for (Iterator<ArrayList<Request>> executeListIterator = executeList.iterator(); executeListIterator.hasNext(); ) {
            ArrayList<Request> list = executeListIterator.next();
            list.removeIf(listRequest -> listRequest == request);
        }
        logger.debug("Removing request [{}] complete", request.getName());
    }


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
