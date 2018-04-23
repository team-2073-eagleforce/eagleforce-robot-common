package com.team2073.common.util;

import java.awt.geom.Point2D;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.sun.javafx.geom.Vec2d;

public class PurePursuitDrive {
	private DeadReckoningTracker robotPose = new DeadReckoningTracker(null, null, null, 10);
	private List<Vec2d> desiredVectors = new ArrayList<>();
	private List<Point2D> desiredPoints = new ArrayList<>();
	private final double maxVelocity;
	private final double pathRadius;
	private final double lookAheadAtMaxVelocity;
	private final double lookAheadIntervalOnPath;
	
	public PurePursuitDrive(double maxVelocity, double pathRadius, double lookAheadAtMaxVelocity, double lookAheadIntervalOnPath) {
		this.maxVelocity = maxVelocity;
		this.pathRadius = pathRadius;
		this.lookAheadAtMaxVelocity = lookAheadAtMaxVelocity;
		this.lookAheadIntervalOnPath = lookAheadIntervalOnPath;
	}

	private Vec2d steeringVector(Vec2d desiredVector) {
		return new Vec2d((desiredVector.x - robotPose.getCurrentRobotVector().x),
				(desiredVector.y - robotPose.getCurrentRobotVector().y));
	}

	private Point2D lookAheadPoint() {
		Point2D lookAhead = new Point2D.Double();
		double distanceToLookAhead = map(robotPose.getCurrentRobotVector().x, 0, maxVelocity, 0,
				lookAheadAtMaxVelocity);
		double distanceOnX = Math.cos(robotPose.getCurrentRobotVector().y * (Math.PI / 180)) * distanceToLookAhead;
		double distanceOnY = Math.sin(robotPose.getCurrentRobotVector().y * (Math.PI / 180)) * distanceToLookAhead;
		lookAhead.setLocation(robotPose.getCurrentLocation().getX() + distanceOnX,
				robotPose.getCurrentLocation().getY() + distanceOnY);
		return lookAhead;
	}

	private double seekSteeringVector(Vec2d steeringVector) {
		System.out.println(steeringVector);
		return 0;
	}

	double map(double x, double in_min, double in_max, double out_min, double out_max) {
		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}
	
	public void trackPath() {
		Point2D lookAheadPoint = lookAheadPoint();
		if(isLookAheadPointOnThePath(lookAheadPoint)) {
//			doNothing
		}else {
			seekSteeringVector(steeringVector(pointToVector(futurePointOnPath(findClosestPointOnPath(lookAheadPoint, desiredPoints)))));
		}
		
	}
	
	private Vec2d pointToVector(Point2D point) {
		int i = 0;
		for (Point2D goal : desiredPoints) {
			if(point.equals(goal)) {
				return desiredVectors.get(i);
			}
			i++;
		}
		return null;
	}

	private boolean isLookAheadPointOnThePath(Point2D lookAheadPoint) {
		for (Point2D goal : desiredPoints) {
			if ((lookAheadPoint.getX() < goal.getX() + pathRadius && lookAheadPoint.getX() > goal.getX() - pathRadius)
					&& (lookAheadPoint.getY() < goal.getY() + pathRadius
							&& lookAheadPoint.getY() > goal.getY() - pathRadius)) {
				
				return true;
			}
		}
		return false;
	}
	
	private Point2D findClosestPointOnPath(Point2D lookAheadPoint, List<Point2D> desiredPoints) {
		double worldRecordDistance = Integer.MAX_VALUE;
		Point2D currentClosest = new Point2D.Double();
		for (Point2D desired : desiredPoints) {
			if(worldRecordDistance > lookAheadPoint.distance(desired)) {
				worldRecordDistance = lookAheadPoint.distance(desired);
				currentClosest = desired;
			}
		}
		
		return currentClosest;
	}
	
	private Point2D futurePointOnPath(Point2D closestPointOnPath) {
		double distanceToLookAhead = map(robotPose.getCurrentRobotVector().x, 0, maxVelocity, 0,
				lookAheadAtMaxVelocity);
		int futurePointOnPath = (int) Math.round(map(distanceToLookAhead, 0, lookAheadAtMaxVelocity, 0, lookAheadIntervalOnPath));
		return desiredPoints.get(futurePointOnPath);
	}

}
