package com.team2073.common.motionprofiling;

public class TrapezoidalVelocityProfile {

    private double currentPosition;
    private double currentVelocity;
    private double currentAcceleration;
    private double maxVelocity;
    private double maxAcceleration;
    private double startingPosition;
    private double timeStep;
    private double timeToAccelerate;
    private double timeAtMaxVelocity;
    private double distanceTraveledByAccelerating;
    private double totalTime;
    private double currentTime;
    private double relativeDesiredPosition;
    private double distanceTraveledAtMaxVelocity;

    public TrapezoidalVelocityProfile(double startingPosition, double desiredPosition,
                                      double maxVelocity, double maxAcceleration, double timeStep) {
        this.startingPosition = startingPosition;
        this.relativeDesiredPosition = desiredPosition - startingPosition;
        this.maxVelocity = maxVelocity;
        this.maxAcceleration = maxAcceleration;
        this.timeStep = timeStep;

        timeToAccelerate = maxVelocity / maxAcceleration;
        distanceTraveledByAccelerating = .5 * timeToAccelerate * maxVelocity;
        distanceTraveledAtMaxVelocity = relativeDesiredPosition - (2 * distanceTraveledByAccelerating);
        timeAtMaxVelocity = (relativeDesiredPosition - (2 * distanceTraveledByAccelerating)) / maxVelocity;
        totalTime = timeAtMaxVelocity + 2 * timeToAccelerate;
        currentPosition += startingPosition;
    }

    /**
     * @return the next trajectory point in the profile. Returns null if profile is done.
     */
    public ProfileTrajectoryPoint calculateNextPoint() {
//		maxVelocity = initialVelocity + acceleration * time
//		position = initialSpeed*time + (1/2)*acceleration*time^2
//		maxVelocity^2 = initialVelocity^2 + 2*acceleration*initialVelocity
        if (currentTime < timeToAccelerate) {
            currentAcceleration = maxAcceleration;
            currentVelocity += currentAcceleration * timeStep;
            if (currentVelocity > maxVelocity)
                currentVelocity = maxVelocity;
            currentPosition += currentVelocity * timeStep + (.5) * currentAcceleration * Math.pow(timeStep, 2);
        } else if (currentTime >= timeToAccelerate && currentTime <= totalTime - timeToAccelerate) {

            currentVelocity = maxVelocity;
            currentPosition += currentVelocity * timeStep;
        } else {
            currentAcceleration = -maxAcceleration;
            currentVelocity += currentAcceleration * timeStep;
            currentPosition += currentVelocity * timeStep + (.5) * currentAcceleration * Math.pow(timeStep, 2);
            if (currentVelocity <= 0) {
                currentVelocity = 0;
                return null;
            }
        }

        ProfileTrajectoryPoint trajPoint = new ProfileTrajectoryPoint(currentPosition, currentVelocity, currentAcceleration, 0 , timeStep, currentTime);
        currentTime += timeStep;
        return trajPoint;
    }

}
