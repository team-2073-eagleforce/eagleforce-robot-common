package com.team2073.common.mediator;

import com.team2073.common.mediator.Tracker.Tracker;
import com.team2073.common.mediator.condition.Condition;
import com.team2073.common.mediator.conflict.Conflict;
import com.team2073.common.mediator.request.Request;
import com.team2073.common.mediator.subsys.ColleagueSubsystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class Mediator{
    private Map<Class, ColleagueSubsystem> subsystemMap;
    private Map<Class, ArrayList<Conflict>> conflictMap;
    private Tracker subsystemTracker;

    ArrayList<ArrayList<Request>> executeList = new ArrayList<>();

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @PostConstruct
    public void init(Map<Class, ColleagueSubsystem> subsystemMap, Map<Class, ArrayList<Conflict>> conflictMap, Tracker subsystemTracker) {
        this.subsystemMap = subsystemMap;
        this.conflictMap = conflictMap;
        this.subsystemTracker = subsystemTracker;
        logger.info("init mediator");
    }

    public void periodic() {
        executeList.removeIf((ArrayList<Request> list) -> list.isEmpty());
        if (!executeList.isEmpty()) {
            for (ArrayList<Request> requestList : executeList) {
                for (int i = requestList.size() - 1; requestList.size() > 0; i--) {
                    execute(requestList.get(requestList.size() - 1));
                }
            }
        }
    }

    public void add(Request request) {
        ArrayList<Conflict> conflicts = findConflicts(request);
        if (conflicts == null || conflicts.isEmpty()) {
            execute(request);
        } else {
            ArrayList<Request> requestList = new ArrayList<>();
            for (Conflict conflict : conflicts) {
                requestList.add(request);
                Request listRequest = new Request(conflict.getConflictingSubsystem(),
                        conflict.getResolution(conflict.getConflictingCondition(),
                                subsystemMap.get(conflict.getConflictingSubsystem())));
                ArrayList<Conflict> innerConflicts = findConflicts(listRequest);
                if (innerConflicts != null) {
                    for (Conflict innerConflict : innerConflicts) {
                        requestList.add(new Request(innerConflict.getConflictingSubsystem(),
                                innerConflict.getResolution(innerConflict.getConflictingCondition(),
                                        subsystemMap.get(conflict.getConflictingSubsystem()))));
                    }
                }
                requestList.add(listRequest);
                logger.debug("added request");
            }
            executeList.add(requestList);
        }
    }

    public void execute(Request request) {
        logger.debug("executing");
        ArrayList<Conflict> conflicts = findConflicts(request);
        logger.debug("There are [" + conflicts.size() + "] conflicts");
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
    }

    private void removeRequest(Request request) {
        for (Iterator<ArrayList<Request>> executeListIterator = executeList.iterator(); executeListIterator.hasNext(); ) {
            if (!executeList.isEmpty()) {
                ArrayList<Request> list = executeListIterator.next();
                list.removeIf((Request listRequest) -> listRequest == request);
            }
        }
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
