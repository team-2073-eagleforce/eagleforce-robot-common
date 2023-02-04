package com.team2073.common.simulation.model;

import com.team2073.common.util.ConversionUtil;
import com.team2073.common.util.LoggedTunableNumber;
import edu.wpi.first.math.geometry.*;

public class AngledElevatorVisualizer {

    private static double numberOfAngledElevatorSims = 0;
    protected boolean tuningMode;
    private double revsPerMeter;
    private double angleOfElevator;
    private double sideElevatorIsOn;

    private Translation2d elevatorPoseInRobot;
    private Pose2d basePose;
    private Pose3d elevatorPose;
    private double beginningElevatorHeightInMeters;
    private boolean isInverted;

    LoggedTunableNumber x = new LoggedTunableNumber("AngledElevatorSim" + numberOfAngledElevatorSims + "/X");
    LoggedTunableNumber y = new LoggedTunableNumber("AngledElevatorSim" + numberOfAngledElevatorSims + "/Y");
    LoggedTunableNumber beginningHeight = new LoggedTunableNumber("AngledElevatorSim" + numberOfAngledElevatorSims + "/beginningHeight");
    LoggedTunableNumber revsTuning = new LoggedTunableNumber("AngledElevatorSim" + numberOfAngledElevatorSims + "/revs");


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
    public AngledElevatorVisualizer(double revsPerMeter, double angleOfElevator, Translation2d elevatorPoseInRobot, double sideElevatorIsOn, double beginningElevatorHeightInMeters, boolean isInverted, boolean tuningMode) {
        this.tuningMode = tuningMode;
        numberOfAngledElevatorSims++;
        this.revsPerMeter = revsPerMeter;
        this.angleOfElevator = angleOfElevator;
        this.elevatorPoseInRobot = elevatorPoseInRobot;
        this.sideElevatorIsOn = sideElevatorIsOn;
        this.beginningElevatorHeightInMeters = beginningElevatorHeightInMeters;
        this.isInverted = isInverted;
        setTuningMode(tuningMode);
        x.initDefault(0);
        y.initDefault(0);
        revsTuning.initDefault(0);
        beginningHeight.initDefault(0);
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
        if ((x.hasChanged() || y.hasChanged() || beginningHeight.hasChanged()) && tuningMode) {
            elevatorPoseInRobot = new Translation2d(x.get(), y.get());
            beginningElevatorHeightInMeters = beginningHeight.get();
        }

        Translation2d elevatorPositionFromChasis = elevatorPoseInRobot
                .rotateBy(Rotation2d.fromDegrees(Math.IEEEremainder(yaw, 360)))
                .plus(robotPose.getTranslation());
        basePose = new Pose2d(elevatorPositionFromChasis, Rotation2d.fromDegrees(robotPose.getRotation().getDegrees() + sideElevatorIsOn));

        double length = tuningMode ? revsTuning.get() / revsPerMeter : revs/ revsPerMeter;

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

    private void setTuningMode(boolean tuningMode) {
        x.setTuningMode(tuningMode);
        y.setTuningMode(tuningMode);
        beginningHeight.setTuningMode(tuningMode);
        revsTuning.setTuningMode(tuningMode);
    }

    public Pose3d getElevatorPose() {return  elevatorPose; }
}
