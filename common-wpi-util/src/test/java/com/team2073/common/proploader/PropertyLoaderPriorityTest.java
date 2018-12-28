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
public class PropertyLoaderPriorityTest extends BaseWpiTest {


    // NOTES:
    // -Each of these tests simply point to a certain "scenario" directory and run the exact same test
    // -The contents of the directory is what actually dictates the test
    // -In each "scenario" directory, there should be ONLY ONE file with the correct property value (foo=foo-expected-value)
    // -The other files have invalid values (foo=INCORRECT PROPERTY FILE LOADED. Prop file: com/team2073/common/proploader/scenario-2/A-source-code-files/PriorityTest.properties)
    // -If the PropertyLoader loads values from these incorrect files, the tests will fail

    // -Within a scenario directory there are two directories
    //      -A-source-code-files
    //          -These are meant to resemble files that users of the library have in their source code (src/main/resources)
    //          -These files do not get copied anywhere, they are accessed directly from this directory during tests
    //      -B-external-files
    //          -These are meant to resemble files that are external to the source code (Ex: in the user directory on board the RIO)
    //          -These files are copied to a temp directory before the test starts and then this temp directory is used in the test to represent an external directory

    // -The files for these tests exist in the "com/team2073/common/proploader/priority-tests" directory
    // -There are template files in "com/team2073/common/proploader/priority-tests/_TEMPLATES" directory


    public static class PriorityTestProperties {
        String foo;
    }

    private static final String SCENARIO_1_SOURCE_CODE_PATH  = "com/team2073/common/proploader/priority-tests/scenario-1/A-source-code-files";
    private static final String SCENARIO_2_SOURCE_CODE_PATH  = "com/team2073/common/proploader/priority-tests/scenario-2/A-source-code-files";
    private static final String SCENARIO_3_SOURCE_CODE_PATH  = "com/team2073/common/proploader/priority-tests/scenario-3/A-source-code-files";
    private static final String SCENARIO_4_SOURCE_CODE_PATH  = "com/team2073/common/proploader/priority-tests/scenario-4/A-source-code-files";
    private static final String SCENARIO_5_SOURCE_CODE_PATH  = "com/team2073/common/proploader/priority-tests/scenario-5/A-source-code-files";
    private static final String SCENARIO_6_SOURCE_CODE_PATH  = "com/team2073/common/proploader/priority-tests/scenario-6/A-source-code-files";
    private static final String SCENARIO_7_SOURCE_CODE_PATH  = "com/team2073/common/proploader/priority-tests/scenario-7/A-source-code-files";
    private static final String SCENARIO_8_SOURCE_CODE_PATH  = "com/team2073/common/proploader/priority-tests/scenario-8/A-source-code-files";
    private static final String SCENARIO_9_SOURCE_CODE_PATH  = "com/team2073/common/proploader/priority-tests/scenario-9/A-source-code-files";
    private static final String SCENARIO_10_SOURCE_CODE_PATH = "com/team2073/common/proploader/priority-tests/scenario-10/A-source-code-files";
    private static final String SCENARIO_11_SOURCE_CODE_PATH = "com/team2073/common/proploader/priority-tests/scenario-11/A-source-code-files";
    private static final String SCENARIO_12_SOURCE_CODE_PATH = "com/team2073/common/proploader/priority-tests/scenario-12/A-source-code-files";
    private static final String SCENARIO_13_SOURCE_CODE_PATH = "com/team2073/common/proploader/priority-tests/scenario-13/A-source-code-files";

    private static final String SCENARIO_1_EXTERNAL_CONF_PATH  = "com/team2073/common/proploader/priority-tests/scenario-1/B-external-files";
    private static final String SCENARIO_2_EXTERNAL_CONF_PATH  = "com/team2073/common/proploader/priority-tests/scenario-2/B-external-files";
    private static final String SCENARIO_3_EXTERNAL_CONF_PATH  = "com/team2073/common/proploader/priority-tests/scenario-3/B-external-files";
    private static final String SCENARIO_4_EXTERNAL_CONF_PATH  = "com/team2073/common/proploader/priority-tests/scenario-4/B-external-files";
    private static final String SCENARIO_5_EXTERNAL_CONF_PATH  = "com/team2073/common/proploader/priority-tests/scenario-5/B-external-files";
    private static final String SCENARIO_6_EXTERNAL_CONF_PATH  = "com/team2073/common/proploader/priority-tests/scenario-6/B-external-files";
    private static final String SCENARIO_7_EXTERNAL_CONF_PATH  = "com/team2073/common/proploader/priority-tests/scenario-7/B-external-files";
    private static final String SCENARIO_8_EXTERNAL_CONF_PATH  = "com/team2073/common/proploader/priority-tests/scenario-8/B-external-files";
    private static final String SCENARIO_9_EXTERNAL_CONF_PATH  = "com/team2073/common/proploader/priority-tests/scenario-9/B-external-files";
    private static final String SCENARIO_10_EXTERNAL_CONF_PATH = "com/team2073/common/proploader/priority-tests/scenario-10/B-external-files";
    private static final String SCENARIO_11_EXTERNAL_CONF_PATH = "com/team2073/common/proploader/priority-tests/scenario-11/B-external-files";
    private static final String SCENARIO_12_EXTERNAL_CONF_PATH = "com/team2073/common/proploader/priority-tests/scenario-12/B-external-files";
    private static final String SCENARIO_13_EXTERNAL_CONF_PATH = "com/team2073/common/proploader/priority-tests/scenario-13/B-external-files";

    private static final File tempDir = FileUtil.getTimestampedTempDir("property-loader/test-files");
    private static final File SCENARIO_1_EXTERNAL_CONF_DIR  = new File(tempDir, "scenario-1");
    private static final File SCENARIO_2_EXTERNAL_CONF_DIR  = new File(tempDir, "scenario-2");
    private static final File SCENARIO_3_EXTERNAL_CONF_DIR  = new File(tempDir, "scenario-3");
    private static final File SCENARIO_4_EXTERNAL_CONF_DIR  = new File(tempDir, "scenario-4");
    private static final File SCENARIO_5_EXTERNAL_CONF_DIR  = new File(tempDir, "scenario-5");
    private static final File SCENARIO_6_EXTERNAL_CONF_DIR  = new File(tempDir, "scenario-6");
    private static final File SCENARIO_7_EXTERNAL_CONF_DIR  = new File(tempDir, "scenario-7");
    private static final File SCENARIO_8_EXTERNAL_CONF_DIR  = new File(tempDir, "scenario-8");
    private static final File SCENARIO_9_EXTERNAL_CONF_DIR  = new File(tempDir, "scenario-9");
    private static final File SCENARIO_10_EXTERNAL_CONF_DIR = new File(tempDir, "scenario-10");
    private static final File SCENARIO_11_EXTERNAL_CONF_DIR = new File(tempDir, "scenario-11");
    private static final File SCENARIO_12_EXTERNAL_CONF_DIR = new File(tempDir, "scenario-12");
    private static final File SCENARIO_13_EXTERNAL_CONF_DIR = new File(tempDir, "scenario-13");

    private static final String FOO_EXPECTED_VALUE = "foo-expected-value";


    private PropertyLoader loader;
    private String[] profileList;
    private String sourceCodeConfDirPath;
    private String externalCopyFromPath;
    private File externalConfDir;
    private PriorityTestProperties propContainer;
    private String fooExpectedValue;
    private String fooActualValue;

    private void runTest() throws IOException {

        JvmUtil.copyResourcesToDirectory(this, externalCopyFromPath, externalConfDir);

        robotContext.getRobotProfiles().addProfiles(profileList);
        loader = new PropertyLoader();
        loader.setSourceCodePropertiesDirPath(sourceCodeConfDirPath);
        loader.setExternalPropertiesDir(externalConfDir);
        propContainer = loader.registerPropContainer(PriorityTestProperties.class);
        loader.loadProperties();
        fooActualValue = propContainer.foo;

        assertThat(fooActualValue).isEqualTo(fooExpectedValue);
    }

    // ============================================================
    // No profiles active scenarios
    // ============================================================

    @Test
    @DisplayName("Property file priority - Scenario 1 (definition in code comments")
    void scenario1() throws IOException {

        // Scenario 1

        // Active profiles:         None
        // Source code prop file:   Exists
        // External prop file:      !Exists

        // Expected load from:      Source code prop file

        profileList = new String[0];
        sourceCodeConfDirPath = SCENARIO_1_SOURCE_CODE_PATH;
        externalCopyFromPath = SCENARIO_1_EXTERNAL_CONF_PATH;
        externalConfDir = SCENARIO_1_EXTERNAL_CONF_DIR;
        fooExpectedValue = FOO_EXPECTED_VALUE;

        runTest();
    }

    @Test
    @DisplayName("Property file priority - Scenario 2 (definition in code comments")
    void scenario2() throws IOException {

        // Scenario 2

        // Active profiles:         None
        // Source code prop file:   Exists
        // External prop file:      Exists

        // Expected load from:      External prop file

        profileList = new String[0];
        sourceCodeConfDirPath = SCENARIO_2_SOURCE_CODE_PATH;
        externalCopyFromPath = SCENARIO_2_EXTERNAL_CONF_PATH;
        externalConfDir = SCENARIO_2_EXTERNAL_CONF_DIR;
        fooExpectedValue = FOO_EXPECTED_VALUE;

        runTest();
    }

    @Test
    @DisplayName("Property file priority - Scenario 3 (definition in code comments")
    void scenario3() throws IOException {

        // Scenario 3

        // Active profiles:         None
        // Source code prop file:   !Exists
        // External prop file:      Exists

        // Expected load from:      External prop file

        profileList = new String[0];
        sourceCodeConfDirPath = SCENARIO_3_SOURCE_CODE_PATH;
        externalCopyFromPath = SCENARIO_3_EXTERNAL_CONF_PATH;
        externalConfDir = SCENARIO_3_EXTERNAL_CONF_DIR;
        fooExpectedValue = FOO_EXPECTED_VALUE;

        runTest();
    }



    // ============================================================
    // One profile active scenarios
    // ============================================================



    @Test
    @DisplayName("Property file priority - Scenario 4 (definition in code comments")
    void scenario4() throws IOException {

        // Scenario 4

        // Active profiles:                     profile-1
        // Source code prop file:               Exists
        // Source code profile-1 prop file:     !Exists
        // External prop file:                  !Exists
        // External profile-1 prop file:        !Exists

        // Expected load from:                  Source code prop file

        profileList = new String[]{"profile-1"};
        sourceCodeConfDirPath = SCENARIO_4_SOURCE_CODE_PATH;
        externalCopyFromPath = SCENARIO_4_EXTERNAL_CONF_PATH;
        externalConfDir = SCENARIO_4_EXTERNAL_CONF_DIR;
        fooExpectedValue = FOO_EXPECTED_VALUE;

        runTest();
    }

    @Test
    @DisplayName("Property file priority - Scenario 5 (definition in code comments")
    void scenario5() throws IOException {

        // Scenario 5

        // Active profiles:                     profile-1
        // Source code prop file:               Exists
        // Source code profile-1 prop file:     Exists
        // External prop file:                  Exists
        // External profile-1 prop file:        !Exists

        // Expected load from:                  Source code profile-1 prop file

        profileList = new String[]{"profile-1"};
        sourceCodeConfDirPath = SCENARIO_5_SOURCE_CODE_PATH;
        externalCopyFromPath = SCENARIO_5_EXTERNAL_CONF_PATH;
        externalConfDir = SCENARIO_5_EXTERNAL_CONF_DIR;
        fooExpectedValue = FOO_EXPECTED_VALUE;

        runTest();
    }

    @Test
    @DisplayName("Property file priority - Scenario 6 (definition in code comments")
    void scenario6() throws IOException {

        // Scenario 6

        // Active profiles:                     profile-1
        // Source code prop file:               Exists
        // Source code profile-1 prop file:     !Exists
        // External prop file:                  Exists
        // External profile-1 prop file:        !Exists

        // Expected load from:                  External prop file

        profileList = new String[]{"profile-1"};
        sourceCodeConfDirPath = SCENARIO_6_SOURCE_CODE_PATH;
        externalCopyFromPath = SCENARIO_6_EXTERNAL_CONF_PATH;
        externalConfDir = SCENARIO_6_EXTERNAL_CONF_DIR;
        fooExpectedValue = FOO_EXPECTED_VALUE;

        runTest();
    }

    @Test
    @DisplayName("Property file priority - Scenario 7 (definition in code comments")
    void scenario7() throws IOException {

        // Scenario 7

        // Active profiles:                     profile-1
        // Source code prop file:               Exists
        // Source code profile-1 prop file:     Exists
        // External prop file:                  Exists
        // External profile-1 prop file:        Exists

        // Expected load from:                  External profile-1 prop file

        profileList = new String[]{"profile-1"};
        sourceCodeConfDirPath = SCENARIO_7_SOURCE_CODE_PATH;
        externalCopyFromPath = SCENARIO_7_EXTERNAL_CONF_PATH;
        externalConfDir = SCENARIO_7_EXTERNAL_CONF_DIR;
        fooExpectedValue = FOO_EXPECTED_VALUE;

        runTest();
    }



    // ============================================================
    // Two profiles active scenarios
    // ============================================================



    @Test
    @DisplayName("Property file priority - Scenario 8 (definition in code comments")
    void scenario8() throws IOException {

        // Scenario 8

        // Active profiles:                     profile-1, profile-2
        // Source code prop file:               Exists
        // Source code profile-1 prop file:     !Exists
        // Source code profile-2 prop file:     !Exists
        // External prop file:                  !Exists
        // External profile-1 prop file:        !Exists
        // External profile-2 prop file:        !Exists

        // Expected load from:                  Source code prop file

        profileList = new String[]{"profile-1", "profile-2"};
        sourceCodeConfDirPath = SCENARIO_8_SOURCE_CODE_PATH;
        externalCopyFromPath = SCENARIO_8_EXTERNAL_CONF_PATH;
        externalConfDir = SCENARIO_8_EXTERNAL_CONF_DIR;
        fooExpectedValue = FOO_EXPECTED_VALUE;

        runTest();
    }

    @Test
    @DisplayName("Property file priority - Scenario 9 (definition in code comments")
    void scenario9() throws IOException {

        // Scenario 9

        // Active profiles:                     profile-1, profile-2
        // Source code prop file:               Exists
        // Source code profile-1 prop file:     Exists
        // Source code profile-2 prop file:     !Exists
        // External prop file:                  Exists
        // External profile-1 prop file:        !Exists
        // External profile-2 prop file:        !Exists

        // Expected load from:                  Source code profile-1 prop file

        profileList = new String[]{"profile-1", "profile-2"};
        sourceCodeConfDirPath = SCENARIO_9_SOURCE_CODE_PATH;
        externalCopyFromPath = SCENARIO_9_EXTERNAL_CONF_PATH;
        externalConfDir = SCENARIO_9_EXTERNAL_CONF_DIR;
        fooExpectedValue = FOO_EXPECTED_VALUE;

        runTest();
    }

    @Test
    @DisplayName("Property file priority - Scenario 10 (definition in code comments")
    void scenario10() throws IOException {

        // Scenario 10

        // Active profiles:                     profile-1, profile-2
        // Source code prop file:               Exists
        // Source code profile-1 prop file:     Exists
        // Source code profile-2 prop file:     Exists
        // External prop file:                  !Exists
        // External profile-1 prop file:        Exists
        // External profile-2 prop file:        !Exists

        // Expected load from:                  Source code profile-2 prop file

        profileList = new String[]{"profile-1", "profile-2"};
        sourceCodeConfDirPath = SCENARIO_10_SOURCE_CODE_PATH;
        externalCopyFromPath = SCENARIO_10_EXTERNAL_CONF_PATH;
        externalConfDir = SCENARIO_10_EXTERNAL_CONF_DIR;
        fooExpectedValue = FOO_EXPECTED_VALUE;

        runTest();
    }

    @Test
    @DisplayName("Property file priority - Scenario 11 (definition in code comments")
    void scenario11() throws IOException {

        // Scenario 11

        // Active profiles:                     profile-1, profile-2
        // Source code prop file:               Exists
        // Source code profile-1 prop file:     !Exists
        // Source code profile-2 prop file:     !Exists
        // External prop file:                  Exists
        // External profile-1 prop file:        !Exists
        // External profile-2 prop file:        !Exists

        // Expected load from:                  External prop file

        profileList = new String[]{"profile-1", "profile-2"};
        sourceCodeConfDirPath = SCENARIO_11_SOURCE_CODE_PATH;
        externalCopyFromPath = SCENARIO_11_EXTERNAL_CONF_PATH;
        externalConfDir = SCENARIO_11_EXTERNAL_CONF_DIR;
        fooExpectedValue = FOO_EXPECTED_VALUE;

        runTest();
    }

    @Test
    @DisplayName("Property file priority - Scenario 12 (definition in code comments")
    void scenari12() throws IOException {

        // Scenario 12

        // Active profiles:                     profile-1, profile-2
        // Source code prop file:               Exists
        // Source code profile-1 prop file:     Exists
        // Source code profile-2 prop file:     !Exists
        // External prop file:                  Exists
        // External profile-1 prop file:        Exists
        // External profile-2 prop file:        !Exists

        // Expected load from:                  External profile-1 prop file

        profileList = new String[]{"profile-1", "profile-2"};
        sourceCodeConfDirPath = SCENARIO_12_SOURCE_CODE_PATH;
        externalCopyFromPath = SCENARIO_12_EXTERNAL_CONF_PATH;
        externalConfDir = SCENARIO_12_EXTERNAL_CONF_DIR;
        fooExpectedValue = FOO_EXPECTED_VALUE;

        runTest();
    }

    @Test
    @DisplayName("Property file priority - Scenario 13 (definition in code comments")
    void scenario13() throws IOException {

        // Scenario 13

        // Active profiles:                     profile-1, profile-2
        // Source code prop file:               Exists
        // Source code profile-1 prop file:     Exists
        // Source code profile-2 prop file:     Exists
        // External prop file:                  Exists
        // External profile-1 prop file:        Exists
        // External profile-2 prop file:        Exists

        // Expected load from:                  External profile-2 prop file

        profileList = new String[]{"profile-1", "profile-2"};
        sourceCodeConfDirPath = SCENARIO_13_SOURCE_CODE_PATH;
        externalCopyFromPath = SCENARIO_13_EXTERNAL_CONF_PATH;
        externalConfDir = SCENARIO_13_EXTERNAL_CONF_DIR;
        fooExpectedValue = FOO_EXPECTED_VALUE;

        runTest();
    }
}
