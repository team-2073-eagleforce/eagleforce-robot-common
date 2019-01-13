package com.team2073.common.proploader;

import com.team2073.common.wpitest.BaseWpiTest;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.*;

/**
 * @author Preston Briggs
 */
public class PropertyLoaderConfigurationTest extends BaseWpiTest {
    
    @Test
    @DisplayName("WHEN: Setting external conf dir with a prefix of '~' - THEN: '~' is replaced with user dir")
    void tilda() {
        
        String expectedPath = new File(FileUtils.getUserDirectory(), "foo").getAbsolutePath();
        
        PropertyLoader loader = new PropertyLoader();
        loader.setExternalPropertiesDirPath("~/foo");
        
        String actualPath = loader.getExternalPropDir().getAbsolutePath();
        assertThat(actualPath).isEqualTo(expectedPath);
    }
    
    @Test
    @DisplayName("WHEN: Setting external conf dir with a prefix of '/' - THEN: Path is resolved as absolute path")
    void absolute1() {
        
        String expectedPath = new File("/foo").getAbsolutePath();
        
        PropertyLoader loader = new PropertyLoader();
        loader.setExternalPropertiesDirPath("/foo");
        
        String actualPath = loader.getExternalPropDir().getAbsolutePath();
        assertThat(actualPath).isEqualTo(expectedPath);
    }
    
    @Test
    @DisplayName("WHEN: Setting external conf dir with a prefix of '\\' - THEN: Path is resolved as absolute path")
    void absolute2() {
        
        String expectedPath = new File("/foo").getAbsolutePath();
        
        PropertyLoader loader = new PropertyLoader();
        loader.setExternalPropertiesDirPath("\\foo");
        
        String actualPath = loader.getExternalPropDir().getAbsolutePath();
        assertThat(actualPath).isEqualTo(expectedPath);
    }
    
    @Test
    @DisplayName("WHEN: Setting external conf dir without a prefix of '/' or '\\' - THEN: Path is resolved relative to user dir")
    void relative() {
        
        String expectedPath = new File(FileUtils.getUserDirectory(), "foo").getAbsolutePath();
        
        PropertyLoader loader = new PropertyLoader();
        loader.setExternalPropertiesDirPath("foo");
        
        String actualPath = loader.getExternalPropDir().getAbsolutePath();
        assertThat(actualPath).isEqualTo(expectedPath);
    }
}
