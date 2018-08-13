package com.team2073.common.mediator.Tracker

import com.team2073.common.mediator.condition.PositionBasedCondition
import com.team2073.common.mediator.subsys.ColleagueSubsystem

class PositionBasedTracker {

    companion object {
        var instanceList: MutableList<PositionBasedTrackee> = mutableListOf()

        fun registerTrackee(instance: PositionBasedTrackee) {
            instanceList.add(instance)
        }

        fun <C : ColleagueSubsystem> findSubsystemPosition(subsystem: Class<C>): Double {
            var position: Double = 0.0
            for (instance in instanceList) {
                position = instance.updateTracker()
            }
            return position
        }

        fun <C : ColleagueSubsystem> findSubsystemCondition(instance: Class<C>): PositionBasedCondition {
            return PositionBasedCondition(0.0, 0.0, findSubsystemPosition(instance))
        }


    }
}