package com.team2073.common.proploader;

import com.team2073.common.util.FileUtil;
import com.team2073.common.util.JvmUtil;
import com.team2073.common.wpitest.BaseWpiTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.*;

/**
 * @author Preston Briggs
 */
public class PropertyLoaderLoadPropertiesTest extends BaseWpiTest {

    public static class InvalidDataTypeProperties {
        Integer foo;
    }

    @Test
    @DisplayName("WHEN: Property file has invalid data type for the associated field - THEN: Should ignore and no exception is thrown")
    void invalidDataType() throws IOException {
        String externalCopyFromPath = "com/team2073/common/proploader/load-properties-tests";
        File externalConfDir = FileUtil.getTimestampedTempDir("property-loader/test-files");

        JvmUtil.copyResourcesToDirectory(this, externalCopyFromPath, externalConfDir);

        PropertyLoader loader = new PropertyLoader();
        loader.setExternalConfDir(externalConfDir);
        InvalidDataTypeProperties propContainer = loader.registerPropContainer(InvalidDataTypeProperties.class);
        loader.loadProperties();
        loader.loadProperties();
        Integer fooActualValue = propContainer.foo;

        assertThat(fooActualValue).isNull();
    }
}
