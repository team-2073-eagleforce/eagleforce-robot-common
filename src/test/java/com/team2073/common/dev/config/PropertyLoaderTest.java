package com.team2073.common.dev.config;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author pbriggs
 */
class PropertyLoaderTest {

    @Test
    public void renameMe() {
        PropertyLoader propLoader = new PropertyLoader();
        Injector injector = Guice.createInjector(new PropertyLoader());
        ApplicationProperties instance = injector.getInstance(ApplicationProperties.class);
        RioProperties rioProps = injector.getInstance(RioProperties.class);

        assertEquals(32.5, (double) instance.getStartingPosition());
        assertEquals(2073, (int) rioProps.getTeamNumber());
    }
}