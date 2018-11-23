package com.team2073.common.subsys;

import com.team2073.common.controlloop.PidfControlLoop;
import com.team2073.common.ctx.RobotContext;
import com.team2073.common.datarecorder.DataRecorder;
import com.team2073.common.periodic.PeriodicRunner;
import com.team2073.common.simulation.SimulationConstants;
import com.team2073.common.simulation.model.ArmMechanism;
import com.team2073.common.simulation.model.LinearMotionMechanism;
import com.team2073.common.simulation.runner.SimulationEnvironmentRunner;
import com.team2073.common.simulation.speedcontroller.SimulationEagleSRX;
import com.team2073.common.simulation.speedcontroller.SimulationPidfEagleSRX;
import com.team2073.common.subsys.PositionalMechanismController.HoldType;
import com.team2073.common.subsys.PositionalMechanismControllerIntegrationTestFixtures.ElevatorGoal;
import com.team2073.common.subsys.PositionalMechanismControllerIntegrationTestFixtures.ElevatorGoalSupplier;
import com.team2073.common.subsys.PositionalMechanismControllerIntegrationTestFixtures.ElevatorPositionConverter;
import com.team2073.common.util.ThreadUtil;
import com.team2073.common.wpitest.BaseWpiTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author pbriggs
 */
class PositionalMechanismControllerIntegrationTest extends BaseWpiTest {

    private Logger log = LoggerFactory.getLogger(getClass());

    @BeforeEach
    void baseWpiTestInit() {
        robotContext = RobotContext.resetTestInstance();
        assertThat(robotContext).isNotNull();
    }

    @Test
    public void simpleTestLinearMechanism() {
        RobotContext robotContext = RobotContext.getInstance();
        PeriodicRunner periodicRunner = robotContext.getPeriodicRunner();
        DataRecorder dataRecorder = robotContext.getDataRecorder().registerCsvOutputHandler();
        dataRecorder.registerWithPeriodicRunner(periodicRunner);

        LinearMotionMechanism lmm = new LinearMotionMechanism(25., SimulationConstants.MotorType.PRO, 2, 2, .855);
        PidfControlLoop pid = new PidfControlLoop(.023, 0, .02, 0,  1);
        SimulationEagleSRX srx = new SimulationPidfEagleSRX("ExampleTalon", lmm, 1350, pid);
        PositionalMechanismController<ElevatorGoal> mechanismController = new PositionalMechanismController<ElevatorGoal>("Simulation Elevator", new ElevatorPositionConverter(), HoldType.PID , srx);
        ElevatorGoalSupplier goalSupplier = new ElevatorGoalSupplier(mechanismController);

        SimulationEnvironmentRunner.create()
                .withCycleComponent(lmm)
                .withPeriodicComponent(() -> periodicRunner.invokePeriodicInstances())
                .withPeriodicComponent(mechanismController)
                .withPeriodicComponent(goalSupplier)
                .withIterationCount(300)
                .run(e -> {
                    ThreadUtil.sleep(1000);
                    dataRecorder.disable();
//                    assertThat(lmm.position()).isCloseTo(goalPosition, offset(2.0));
                });
    }

    @Test
    public void simpleTestArmMechanism() {
        RobotContext robotContext = RobotContext.getInstance();
        PeriodicRunner periodicRunner = robotContext.getPeriodicRunner();
        DataRecorder dataRecorder = robotContext.getDataRecorder().registerCsvOutputHandler();
        dataRecorder.registerWithPeriodicRunner(periodicRunner);

        ArmMechanism lmm = new ArmMechanism(100d, SimulationConstants.MotorType.BAG, 1, 2, 12);
        PidfControlLoop pid = new PidfControlLoop(.023, 0, .02, 0, 1);
        SimulationEagleSRX srx = new SimulationPidfEagleSRX("ExampleTalon", lmm, 1350, pid);
        PositionalMechanismController<ElevatorGoal> mechanismController = new PositionalMechanismController<ElevatorGoal>("Simulation Elevator", new ElevatorPositionConverter(), HoldType.PID, srx);
        ElevatorGoalSupplier goalSupplier = new ElevatorGoalSupplier(mechanismController);

        SimulationEnvironmentRunner.create()
                .withCycleComponent(lmm)
                .withPeriodicComponent(() -> periodicRunner.invokePeriodicInstances())
                .withPeriodicComponent(mechanismController)
                .withPeriodicComponent(goalSupplier)
                .withIterationCount(300)
                .run(e -> {
                    ThreadUtil.sleep(1000);
                    dataRecorder.disable();
//                    assertThat(lmm.position()).isCloseTo(goalPosition, offset(2.0));
                });
    }

}