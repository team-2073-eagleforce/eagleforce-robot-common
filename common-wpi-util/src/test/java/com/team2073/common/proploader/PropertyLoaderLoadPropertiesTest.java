package com.team2073.common.proploader;

import com.team2073.common.proploader.PropertyLoaderLoadPropertiesTest.EnumProperties.Foo;
import com.team2073.common.util.FileUtil;
import com.team2073.common.util.JvmUtil;
import com.team2073.common.wpitest.BaseWpiTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static com.team2073.common.proploader.PropertyLoaderLoadPropertiesTest.EnumProperties.Foo.*;
import static org.assertj.core.api.Assertions.*;

/**
 * @author Preston Briggs
 */
public class PropertyLoaderLoadPropertiesTest extends BaseWpiTest {

    public static class InvalidDataTypeProperties {
        Integer foo;
        String bar;
    }
    
    @Test
    @DisplayName("WHEN: Property file has invalid data type for the associated field - THEN: Should ignore and no exception is thrown")
    void invalidDataType() throws IOException {
        String externalCopyFromPath = "com/team2073/common/proploader/PropertyLoaderLoadPropertiesTest/invalid-data-type";
        File externalConfDir = FileUtil.getTimestampedTempDir("property-loader/test-files");
        
        JvmUtil.copyResourcesToDirectory(this, externalCopyFromPath, externalConfDir);
        
        PropertyLoader loader = new PropertyLoader();
        loader.setExternalPropertiesDir(externalConfDir);
        InvalidDataTypeProperties propContainer = loader.registerPropContainer(InvalidDataTypeProperties.class);
        loader.loadProperties();
        loader.loadProperties();
        Integer fooActualValue = propContainer.foo;
        String barActualValue = propContainer.bar;
    
        assertThat(fooActualValue).isNull();
        assertThat(barActualValue).isEqualTo("baz");
    }
    
    public static class EnumProperties {
        public enum Foo {BAR, BAZ}
        Foo foo;
    }
    
    @Test
    @DisplayName("WHEN: Property container has enum field with valid value - THEN: Should load properly")
    void enumValidValue() throws IOException {
        String externalCopyFromPath = "com/team2073/common/proploader/PropertyLoaderLoadPropertiesTest/enum-valid-value";
        File externalConfDir = FileUtil.getTimestampedTempDir("property-loader/test-files");
    
        JvmUtil.copyResourcesToDirectory(this, externalCopyFromPath, externalConfDir);
    
        PropertyLoader loader = new PropertyLoader();
        loader.setExternalPropertiesDir(externalConfDir);
        EnumProperties propContainer = loader.registerPropContainer(EnumProperties.class);
        loader.loadProperties();
        loader.loadProperties();
        Foo fooActualValue = propContainer.foo;
    
        assertThat(fooActualValue).isEqualTo(BAR);
    }
}
