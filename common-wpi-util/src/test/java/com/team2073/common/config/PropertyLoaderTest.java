package com.team2073.common.config;

//import com.google.inject.Guice;
//import com.google.inject.Injector;
import com.team2073.common.config.testsamples.ApplicationProperties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author pbriggs
 */
class PropertyLoaderTest {


    private static PropertyLoader propLoader;

    @BeforeEach
    public static void propLoader(){
        propLoader = new PropertyLoader();
    }

    @Test
    public void localFileTest() {
        ApplicationProperties appProp = new ApplicationProperties();

        propLoader.loadProperties(appProp);

        assertEquals(32.5, (double) appProp.getStartingPosition());
    }


}