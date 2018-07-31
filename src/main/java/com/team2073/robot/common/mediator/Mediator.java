package com.team2073.robot.common.mediator;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.team2073.robot.common.mediator.condition.Condition;
import com.team2073.robot.common.mediator.condition.PositionBasedCondition;
import com.team2073.robot.common.mediator.condition.StateBasedCondition;
import com.team2073.robot.common.mediator.conflict.Conflict;
import com.team2073.robot.common.mediator.request.Request;
import com.team2073.robot.common.mediator.subsys.ColleagueSubsystem;
import com.team2073.robot.common.mediator.subsys.PositionBasedSubsystem;
import com.team2073.robot.common.mediator.subsys.StateBasedSubsystem;
import frc.team9073.robot.management.mediator.MasterConflictMap;
import frc.team9073.robot.management.mediator.MasterSubsystemMap;
import frc.team9073.robot.subsystem.ElbowJointSubsystem;
import frc.team9073.robot.subsystem.WristSubsystem;

import javax.annotation.PostConstruct;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Map;

@Singleton
public class Mediator<T extends Condition, C extends ColleagueSubsystem> {

	@Inject
    private MasterConflictMap masterConflictMap;
	@Inject
	private MasterSubsystemMap masterSubsystemMap;
	@Inject
    WristSubsystem wristSubsystem;
    @Inject
    ElbowJointSubsystem elbowJointSubsystem;

	private Map<Class, ColleagueSubsystem> subsystemMap;
	private Map<Class, ArrayList<Conflict>> conflictMap;


    @PostConstruct
    public void init() {
        subsystemMap = masterSubsystemMap.getSubsystemMap();
        conflictMap = masterConflictMap.getConflictMap();
        System.out.println("init mediator");
    }

    public void execute(Request<T, C> request) {
		System.out.println("mediator executing");
		ArrayList<Conflict<T, T, C>> conflicts = findConflicts(request);
		Class<C> subsystem = request.getSubsystem();
		Condition condition = request.getCondition();
		if (conflicts.isEmpty()) {
			subsystemMap.get(subsystem).set(condition.getConditionValue());
		} else {
			resolveConflicts(conflicts, request);
		}
		elbowJointSubsystem.getElbowJointPosition();
		wristSubsystem.getWristPosition();
	}

	private ArrayList<Conflict<T, T, C>> findConflicts(Request<T, C> request) {
		System.out.println("finding conflicts");
		Class<C> subsystem = request.getSubsystem();
		ArrayList<Conflict> possibleConflicts = conflictMap.get(subsystem);
		ArrayList<Conflict<T, T, C>> conflicts = new ArrayList<>();

		if (possibleConflicts == null) {
            System.out.println("no conflicts");
            return conflicts;
		}

		for (Conflict<T, T, C> conflict : possibleConflicts) {
			if(conflict.isConflicting(conflict, request)){
				conflicts.add(conflict);
			}
		}

        return conflicts;
	}

        private void resolveConflicts (ArrayList < Conflict < T, T, C >> conflicts, Request < T, C > request){
			System.out.println("resolvingConflicts");
            for (Conflict conflict: conflicts) {
                        subsystemMap.get(conflict.getConflictingSubsystem()).set(conflict.getResolution());
            }
//            if conflicts have been resolved and it is now safe use the requested value
			execute(request);
        }
}
