package com.team2073.robot.common.mediator.Tracker

import com.team2073.robot.common.mediator.condition.StateBasedCondition
import com.team2073.robot.common.mediator.subsys.ColleagueSubsystem
import com.team2073.robot.common.mediator.subsys.SubsystemStateCondition

class StateBasedTracker {

    companion object {
        var instanceList: MutableList<StateBasedTrackee> = mutableListOf()

        fun registerTrackee(instance: StateBasedTrackee) {
            instanceList.add(instance)
        }

        fun <C : ColleagueSubsystem> findSubsystemState(instance: Class<C>): SubsystemStateCondition? {
            var state: SubsystemStateCondition? = null
            for (instance in instanceList) {
                state = instance.updateTracker()
            }
            return state
        }

        fun <C : ColleagueSubsystem> findSubsystemCondition(instance: Class<C>): StateBasedCondition {
            return StateBasedCondition(findSubsystemState(instance))
        }

    }
}