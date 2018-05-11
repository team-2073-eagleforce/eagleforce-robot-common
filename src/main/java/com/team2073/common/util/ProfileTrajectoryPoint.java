package com.team2073.common.util;

public class ProfileTrajectoryPoint {
    private double position;
    private double velocity;
    private double acceleration;
    private double timeStep;
    private double currentTime;

    public double getPosition() {
        return position;
    }

    public void setPosition(double position) {
        this.position = position;
    }

    public double getVelocity() {
        return velocity;
    }

    public void setVelocity(double velocity) {
        this.velocity = velocity;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(double acceleration) {
        this.acceleration = acceleration;
    }

    public double getTimeStep() {
        return timeStep;
    }

    public void setTimeStep(double timeStep) {
        this.timeStep = timeStep;
    }

    public void setCurrentTime(double currentTime) {
        this.currentTime = currentTime;
    }

}
