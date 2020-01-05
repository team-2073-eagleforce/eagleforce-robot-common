package com.team2073.common.pathfollowing;

import com.team2073.common.motionprofiling.lib.trajectory.Path;
import com.team2073.common.motionprofiling.lib.trajectory.Trajectory.Segment;
import com.team2073.common.pathfollowing.math.RigidTransform2d;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;

import static com.team2073.common.util.MathUtil.map;

public class PurePursuitDrive {
	private final double maxLookAhead;
	private final double minimumLookAhead;
	private Path path;
	private ArrayList<Segment> segmentList;
	private Segment lastClosest = new Segment();
	private Segment lastLookAhead = new Segment();
	private double maxVel;

	public PurePursuitDrive(double minimumLookAhead, double maxLookAhead, double maxVel) {
		this.maxLookAhead = maxLookAhead;
		this.minimumLookAhead = minimumLookAhead;
		this.maxVel = maxVel;
	}


	public double curvature(RigidTransform2d robotPos, double lookaheadRadius, Point2D lookahead) {
		Point2D robotPose = new Point2D.Double(robotPos.getTranslation().x(), robotPos.getTranslation().y());
		double angleInRads = robotPos.getRotation().getRadians();
		double a = -Math.tan(angleInRads);
		double c = Math.tan(angleInRads) * robotPose.getX() - robotPose.getY();
		double x = Math.abs(a * lookahead.getX() + lookahead.getY() + c) / Math.sqrt(a * a + 1);
		return side(angleInRads, lookahead, robotPose) * ((2 * x) / Math.pow(lookaheadRadius, 2));
	}

	public double side(double angleInRads, Point2D lookahead, Point2D robotPose) {
		return Math.signum(Math.sin(angleInRads) * (lookahead.getX() - robotPose.getX())
				- Math.cos(angleInRads) * (lookahead.getY() - robotPose.getY()));
	}

	public Point2D circleCenter(Point2D robotPose, double signedCurvature, double robotAngleInRads) {
		Vector2D toCenter = new Vector2D(Math.cos(robotAngleInRads - Math.PI / 2),
				Math.sin(robotAngleInRads - Math.PI / 2)).scalarMultiply(1 / signedCurvature);
		return new Point2D.Double(robotPose.getX() + toCenter.getX(), robotPose.getY() + toCenter.getY());
	}


	public double calcLookAheadDist(double vel) {
		return map(vel, 0, maxVel, minimumLookAhead, maxLookAhead);
	}

	public Segment closesetPoint(Point2D ref) {
		Segment closest = new Segment();

		for (int i = segmentList.indexOf(lastClosest); i < path.getRobotTrajectory().getNumSegments(); i++) {
			if (i == -1) {
				closest = segmentList.get(0);
			} else {
				if (ref.distance(segmentList.get(i).x, segmentList.get(i).y) <
						ref.distance(closest.x, closest.y) || closest.dt == 0) {
					closest = segmentList.get(i);
				}
			}
		}
		if (closest.dt != 0) {
			lastClosest = closest;
			return closest;
		} else {
			return lastClosest;
		}
	}

	public void setPath(Path path) {
		reset();
		this.path = path;
		this.segmentList = new ArrayList<>(Arrays.asList(path.getRobotTrajectory().getSegments_()));
	}

	private void reset() {
		lastLookAhead = new Segment();
		lastClosest = new Segment();
	}

	public Segment lookaheadPoint(double lookAhead, Point2D robotPose) {
		Segment seg = new Segment();
		for (int i = segmentList.indexOf(lastLookAhead) + 1; i < path.getRobotTrajectory().getNumSegments(); i++) {
			Point2D point = new Point2D.Double(0, 0);
			if (i == 0) {
				point = circleLineIntersect(segmentToPoint2D(segmentList.get(i)),
						segmentToPoint2D(segmentList.get(i + 1)), robotPose, lookAhead);
			} else {

				point = circleLineIntersect(segmentToPoint2D(segmentList.get(i - 1)),
						segmentToPoint2D(segmentList.get(i)), robotPose, lookAhead);
			}
			if (!point.equals(new Point2D.Double(-2073, -2073))) {
				seg = segmentList.get(i);
				break;
			}
		}
		if (seg.dt == 0) {
			return lastLookAhead;
		} else {
			lastLookAhead = seg;
			return seg;
		}
	}

	private Point2D circleLineIntersect(Point2D lineStart, Point2D lineEnd, Point2D circleCenter, double circleRadius) {
		Vector2D d = new Vector2D(lineEnd.getX() - lineStart.getX(), lineEnd.getY() - lineStart.getY());
		Vector2D f = new Vector2D(lineStart.getX() - circleCenter.getX(), lineStart.getY() - circleCenter.getY());
		double a = d.dotProduct(d);
		double b = 2 * f.dotProduct(d);
		double c = f.dotProduct(f) - Math.pow(circleRadius, 2);
		double discriminant = b * b - 4 * a * c;
		if (discriminant >= 0) {
			discriminant = Math.sqrt(discriminant);
			double t1 = (-b - discriminant) / (2 * a);
			double t2 = (-b + discriminant) / (2 * a);
			double t = 0;
			if ((t1 >= 0 && t1 <= 1) && (t2 >= 0 && t2 <= 1)) {
				if (t1 >= t2)
					t = t1;
				else
					t = t2;

			} else if (t1 >= 0 && t1 <= 1)
				t = t1;
			else if (t2 >= 0 && t2 <= 1)
				t = t2;

			if (t != 0) {
				return new Point2D.Double(lineStart.getX() + d.scalarMultiply(t).getX(),
						lineStart.getY() + d.scalarMultiply(t).getY());
			}

		}
		return new Point2D.Double(-2073, -2073);
	}

	public static Point2D segmentToPoint2D(Segment seg) {
		return new Point2D.Double(seg.x, seg.y);
	}

	public static Point2D rigidTransformToPoint2D(RigidTransform2d transform) {
		return new Point2D.Double(transform.getTranslation().x(), transform.getTranslation().y());
	}

	public int findClosestPointIndex() {
		return segmentList.indexOf(lastClosest);
	}

}
