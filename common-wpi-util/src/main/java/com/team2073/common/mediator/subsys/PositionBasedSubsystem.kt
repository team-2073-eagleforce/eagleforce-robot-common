package com.team2073.common.mediator.subsys

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D

interface PositionBasedSubsystem : ColleagueSubsystem<Double> {

    fun getSafetyRange(): Double

    fun positionToPoint(position: Double): Vector2D

    fun pointToPosition(point: Vector2D): Double
}