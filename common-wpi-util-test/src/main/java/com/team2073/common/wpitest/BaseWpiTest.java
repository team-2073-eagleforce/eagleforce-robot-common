package com.team2073.common.wpitest;

import com.team2073.common.ctx.RobotContext;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.*;

/**
 * @author Preston Briggs
 */
public class BaseWpiTest {

    protected RobotContext robotContext;

    @BeforeEach
    void baseWpiTestInit() {
        robotContext = RobotContext.resetTestInstance();
        assertThat(robotContext).isNotNull();
    }
}
