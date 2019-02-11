package com.team2073.common.motionprofiling;


public class TrapezoidalVelocityProfile {

    private final double desiredPosition;
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
                                      ProfileConfiguration configuration) {
        this.startingPosition = startingPosition;
        this.relativeDesiredPosition = desiredPosition - startingPosition;
        this.maxVelocity = configuration.getMaxVelocity();
        this.maxAcceleration = configuration.getMaxAcceleration();
        this.timeStep = configuration.getInterval();
        this.desiredPosition = desiredPosition;

        timeToAccelerate = maxVelocity / maxAcceleration;
        distanceTraveledByAccelerating = .5 * timeToAccelerate * maxVelocity;
        if (startingPosition > desiredPosition) {
            distanceTraveledAtMaxVelocity = relativeDesiredPosition + (2 * distanceTraveledByAccelerating);

        } else {
            distanceTraveledAtMaxVelocity = relativeDesiredPosition - (2 * distanceTraveledByAccelerating);
        }
        timeAtMaxVelocity = Math.abs(distanceTraveledAtMaxVelocity / maxVelocity);
        totalTime = timeAtMaxVelocity + 2 * timeToAccelerate;
        currentPosition += startingPosition;
        if (distanceTraveledByAccelerating > .5 * Math.abs(relativeDesiredPosition)) {
            distanceTraveledByAccelerating = .5 * relativeDesiredPosition;
            timeToAccelerate = Math.sqrt(Math.abs(relativeDesiredPosition) / maxAcceleration);
            distanceTraveledAtMaxVelocity = 0;
            timeAtMaxVelocity = 0;
            totalTime = 2 * timeToAccelerate;
        }
        if (startingPosition > desiredPosition) {
            maxAcceleration = -maxAcceleration;
            maxVelocity = -maxVelocity;
        }
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
            if (Math.abs(currentVelocity) > Math.abs(maxVelocity))
                currentVelocity = maxVelocity;
            currentPosition += currentVelocity * timeStep + (.5) * currentAcceleration * Math.pow(timeStep, 2);
        } else if (currentTime >= timeToAccelerate && currentTime <= totalTime - timeToAccelerate) {

            currentVelocity = maxVelocity;
            currentPosition += currentVelocity * timeStep;
        } else {
            currentAcceleration = -maxAcceleration;
            currentVelocity += currentAcceleration * timeStep;
            currentPosition += currentVelocity * timeStep + (.5) * currentAcceleration * Math.pow(timeStep, 2);
//			if (currentVelocity <= 0) {
//				currentVelocity = 0;
//			}
        }

        ProfileTrajectoryPoint trajPoint = new ProfileTrajectoryPoint(currentPosition, currentVelocity, currentAcceleration, 0, timeStep, currentTime);
        currentTime += timeStep;
        if (currentTime > totalTime) {
            currentPosition = desiredPosition;
            currentVelocity = 0;
            currentAcceleration = 0;
            return new ProfileTrajectoryPoint(desiredPosition, 0, 0, 0, timeStep, currentTime);
        }
        return trajPoint;
    }

    public boolean isFinished() {
        return currentTime > totalTime || currentTime > 100;
    }

    public double getCurrentPosition() {
        return currentPosition;
    }

    public double getCurrentVelocity() {
        return currentVelocity;
    }

    public double getCurrentAcceleration() {
        return currentAcceleration;
    }
}
