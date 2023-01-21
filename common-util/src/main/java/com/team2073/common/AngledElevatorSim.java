package com.team2073.common;

import com.team2073.common.util.ConversionUtil;
import edu.wpi.first.math.geometry.*;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;


public class AngledElevatorSim {

    private double length;
    private double revsPerMeter;
    private double angleOfElevator;
    private double sideElevatorIsOn;

    private Translation2d elevatorPoseInRobot;
    private Pose2d basePose;
    private Pose3d elevatorPose;
    private double beginningElevatorHeightInMeters;
    private boolean isInverted;

    /**
     * Constructs an Angled Elevator Sim
     *
     * \
     *  \
     *   \
     *    \
     *     \
     *    θ \
     * _____
     * @param revsPerMeter Revolutions per Feet
     * @param angleOfElevator  =  θ  The Angle the elevator makes with the ground
     * @param elevatorPoseInRobot Location of the robot within the chassis
     * @param sideElevatorIsOn An angle representing where the elevator is facing i.e. 0 degrees is front, 180 is back, etc...
     */
    public AngledElevatorSim(double revsPerMeter, double angleOfElevator, Translation2d elevatorPoseInRobot, double sideElevatorIsOn, double beginningElevatorHeightInMeters, boolean isInverted) {
        this.revsPerMeter = revsPerMeter;
        this.angleOfElevator = angleOfElevator;
        this.elevatorPoseInRobot = elevatorPoseInRobot;
        this.sideElevatorIsOn = sideElevatorIsOn;
        this.beginningElevatorHeightInMeters = beginningElevatorHeightInMeters;
        this.isInverted = isInverted;
    }

    /**
     * Updates the tip of elevators Pose3D
     * @param robotPose Current pose of the robot. Usually given in m_odometry.getPoseMeters()
     * @param yaw Current yaw of the robot. Usually given in gyro.getYaw()
     * @param revs Current revolution count of the motor. Falcons do a conversion of 600/2048 on encoder distance
     *                                                    NEOs default give in Revolutions
     *                                                    Else figure it out
     */
    public void updatePosition(Pose2d robotPose, double yaw, double revs) {
        Translation2d elevatorPositionFromChasis = elevatorPoseInRobot
                .rotateBy(Rotation2d.fromDegrees(Math.IEEEremainder(yaw, 360)))
                .plus(robotPose.getTranslation());
        basePose = new Pose2d(elevatorPositionFromChasis, Rotation2d.fromDegrees(robotPose.getRotation().getDegrees() + sideElevatorIsOn));

        double length = revs/ revsPerMeter;
        double floorHyp = length * Math.cos(ConversionUtil.degreesToRadians(angleOfElevator));

        if (!isInverted) {
            elevatorPose = new Pose3d(basePose.getX() + floorHyp * Math.cos(ConversionUtil.degreesToRadians(yaw)),
                    basePose.getY() + floorHyp * Math.sin(ConversionUtil.degreesToRadians(yaw)),
                    beginningElevatorHeightInMeters + length * Math.sin(ConversionUtil.degreesToRadians(angleOfElevator)),
                    new Rotation3d(0, 0, basePose.getRotation().getRadians()));
        } else {
            elevatorPose = new Pose3d(basePose.getX() + floorHyp * Math.cos(ConversionUtil.degreesToRadians(yaw)),
                    basePose.getY() + floorHyp * Math.sin(ConversionUtil.degreesToRadians(yaw)),
                    beginningElevatorHeightInMeters - length * Math.sin(ConversionUtil.degreesToRadians(angleOfElevator)),
                    new Rotation3d(0, 0, basePose.getRotation().getRadians()));
        }
    }

    public Pose3d getElevatorPose() {return elevatorPose; }
}
