package com.team2073.common.wpitest;

import com.team2073.common.ctx.RobotContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.*;

/**
 * @author Preston Briggs
 */
public class BaseWpiTest {

    protected RobotContext robotContext;

    @BeforeEach
    void baseWpiTestInit() {
        robotContext = RobotContext.initSimulationInstance();
        assertThat(robotContext).isNotNull();
    }
    
    @AfterEach
    void baseWpiTestCleanUp() {
        RobotContext.shutdownSimulationInstance();
        robotContext = null;
    }
}
