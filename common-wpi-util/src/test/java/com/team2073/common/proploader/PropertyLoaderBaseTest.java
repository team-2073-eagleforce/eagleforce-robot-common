package com.team2073.common.proploader;

import com.team2073.common.wpitest.BaseWpiTest;
import org.junit.jupiter.api.BeforeEach;

/**
 * @author Preston Briggs
 */
public class PropertyLoaderBaseTest extends BaseWpiTest {
    
    protected PropertyLoader loader;
    
    @BeforeEach
    void propertyLoaderBaseTestInit() {
        loader = robotContext.getPropertyLoader();
    }
}
