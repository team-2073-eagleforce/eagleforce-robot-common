package com.team2073.robot.common.mediator.conflict;

import java.util.ArrayList;
import java.util.Map;

public class ConflictMap {

    Map<Class, ArrayList<Conflict>> conflictMap;

    public void registerConflict(Conflict conflict, Boolean bidirectional){
        Class originSubsystem = conflict.getOriginSubsystem();
        ArrayList<Conflict> conflicts = conflictMap.get(originSubsystem);
        if(conflicts == null){
            conflicts = new ArrayList<>();
        }
        conflicts.add(conflict);
        conflictMap.remove(originSubsystem);
        conflictMap.put(originSubsystem, conflicts);

        if(bidirectional){
            registerConflict(conflict.invert(), false);
        }
    }
}
