package com.team2073.common.pathfollowing;

import com.team2073.common.motionprofiling.lib.trajectory.Path;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class PurePursuitDrive {
	private double minCurvatureForMaxLookahead = 10d;
	private List<Vector2D> desiredVectors = new ArrayList<>();
	private List<Point2D> desiredPoints = new ArrayList<>();
	private final double maxVelocity;
	private final double maxLookAhead;
	private final double minimumLookAhead;
	private final double trackWidth;
	private Path path;

	public PurePursuitDrive(Path path, double maxVelocity, double minimumLookAhead, double maxLookAhead, double trackWidth) {
		this.maxVelocity = maxVelocity;
		this.minimumLookAhead = minimumLookAhead;
		this.maxLookAhead = maxLookAhead;
		this.trackWidth = trackWidth;
		this.path = path;
	}

	private double curvature(double x, double lookahead) {
		return (2 * x) / Math.pow(lookahead, 2);
	}

	private double calcLookAheadDist(double pathCurvature) {
		if (pathCurvature < 2d / trackWidth) {
			pathCurvature = 2d / trackWidth;
		}
		if (pathCurvature > minCurvatureForMaxLookahead) {
			pathCurvature = minCurvatureForMaxLookahead;
		}
		return map(pathCurvature, 2d / trackWidth, minCurvatureForMaxLookahead, minimumLookAhead, maxLookAhead);
	}

	private Point2D lookAheadPoint() {
		Point2D lookAhead = new Point2D.Double();
		return lookAhead;
	}

	private double seekSteeringVector(Vector2D steeringVector) {
		System.out.println(steeringVector);
		return 0;
	}

	double map(double x, double in_min, double in_max, double out_min, double out_max) {
		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}

	private Vector2D pointToVector(Point2D point) {
		int i = 0;
		for (Point2D goal : desiredPoints) {
			if (point.equals(goal)) {
				return desiredVectors.get(i);
			}
			i++;
		}
		return null;
	}

	private Point2D findClosestPointOnPath(Point2D lookAheadPoint, List<Point2D> desiredPoints) {
		double worldRecordDistance = Integer.MAX_VALUE;
		Point2D currentClosest = new Point2D.Double();
		for (Point2D desired : desiredPoints) {
			if (worldRecordDistance > lookAheadPoint.distance(desired)) {
				worldRecordDistance = lookAheadPoint.distance(desired);
				currentClosest = desired;
			}
		}

		return currentClosest;
	}

	private Point2D futurePointOnPath(Point2D closestPointOnPath) {
		double distanceToLookAhead = map(0, 0, maxVelocity, 0,
				maxLookAhead);
		return null;
	}

}
