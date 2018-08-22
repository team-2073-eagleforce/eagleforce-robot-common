package com.team2073.common.simulation.env;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author pbriggs
 */
public class SimulationRobot {

    private Logger log = LoggerFactory.getLogger(getClass());

    public void robotInit() {
        log.debug("SimulationRobot - robotInit");
    }

    public void disabledInit() {
        log.debug("SimulationRobot - disabledInit");
    }

    public void autonomousInit() {
        log.debug("SimulationRobot - autonInit");
    }

    public void teleopInit() {
        log.debug("SimulationRobot - teleopInit");
    }

    public void testInit() {
        log.debug("SimulationRobot - testInit");
    }

    public void robotPeriodic() {
        log.trace("SimulationRobot - robotPeriodic");
    }

    public void disabledPeriodic() {
        log.trace("SimulationRobot - disabledPeriodic");
    }

    public void autonomousPeriodic() {
        log.trace("SimulationRobot - autonPeriodic");
    }

    public void teleopPeriodic() {
        log.trace("SimulationRobot - teleopPeriodic");
    }

    public void testPeriodic() {
        log.trace("SimulationRobot - testPeriodic");
    }
}
