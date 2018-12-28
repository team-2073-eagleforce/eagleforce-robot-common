package com.team2073.common.config;

import com.team2073.common.util.JvmUtil;
import com.team2073.common.wpitest.BaseWpiTest;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import static com.team2073.common.config.RobotProfiles.*;
import static org.assertj.core.api.Assertions.*;

/**
 * @author Preston Briggs
 */
public class RobotProfilesTest extends BaseWpiTest {
    
    public static final String ROOT_TEST_DIR_PATH = "com/team2073/common/config";
    public static final String CLASS_ROOT_TEST_DIR_PATH = ROOT_TEST_DIR_PATH + "/RobotProfilesTest";
    public static final String SMOKE_TEST_ROBOT_PROFILES_PATH = CLASS_ROOT_TEST_DIR_PATH + "/smoke-test/robot.profiles";
    public static final String REMOVE_PROFILE_ROBOT_PROFILES_PATH = CLASS_ROOT_TEST_DIR_PATH + "/remove-profile/robot.profiles";
    public static final String COMMENTED_LINES_ROBOT_PROFILES_PATH = CLASS_ROOT_TEST_DIR_PATH + "/commented-lines/robot.profiles";
    public static final String EMPTY_LINES_ROBOT_PROFILES_PATH = CLASS_ROOT_TEST_DIR_PATH + "/empty-lines/robot.profiles";
    public static final String SPACES_OR_TABS_ROBOT_PROFILES_PATH = CLASS_ROOT_TEST_DIR_PATH + "/spaces-or-tabs/robot.profiles";
    public static final String PRIORITY_LOADING_1__ROBOT_PROFILES_PATH = CLASS_ROOT_TEST_DIR_PATH + "/priority-loading-1/robot.profiles";
    public static final String PRIORITY_LOADING_2__ROBOT_PROFILES_PATH = CLASS_ROOT_TEST_DIR_PATH + "/priority-loading-2/robot.profiles";
    
    private RobotProfiles robotProfiles;
    private File robotProfilesFile;
    
    private void cleanUp() throws IOException {
        if (robotProfilesFile.exists())
            FileUtils.forceDelete(robotProfilesFile);
    
        System.clearProperty(ADDITIONAL_PROFILES_ENVIRONMENT_VARIABLE);
    }
    
    private void copyRobotProfilesFile(String sourcePath) throws IOException {
        JvmUtil.copyResourcesToDirectory(this, sourcePath, robotProfilesFile.getParentFile());
    }
    
    @BeforeEach
    void beforeEach() throws IOException {
        robotProfiles = robotContext.getRobotProfiles();
        robotProfilesFile = new File(robotContext.getRobotDirectory().getTempDir(), "robot.profiles");
        robotProfiles.setRobotProfilesFile(robotProfilesFile);
        cleanUp();
    }
    
    @AfterEach
    void afterEach() throws IOException {
        cleanUp();
    }
    
    // Smoke tests
    // ========================================================================
    
    @Test
    @DisplayName("WHEN: Profile is set programmatically - THEN: Profile is set properly (smoke test)")
    void profileFromProgrammatically() throws IOException {
    
        robotProfiles.addProfiles("profile-1");
        
        robotProfiles.initializeAndLockProfiles();
        
        Set<String> profileList = robotProfiles.getProfileList();
        assertThat(profileList).containsExactly("profile-1");
    }
    
    @Test
    @DisplayName("WHEN: Profile is set via robot.profiles - THEN: Profile is set properly (smoke test)")
    void profileFromRobotProperties() throws IOException {
        
        // Adds profile-1
        JvmUtil.copyResourcesToDirectory(this, SMOKE_TEST_ROBOT_PROFILES_PATH, robotProfilesFile.getParentFile());
        
        robotProfiles.initializeAndLockProfiles();
        
        Set<String> profileList = robotProfiles.getProfileList();
        assertThat(profileList).containsExactly("profile-1");
    }
    
    @Test
    @DisplayName("WHEN: Profile is set via environment variable - THEN: Profile is set properly (smoke test)")
    void profileFromEnvVar() throws IOException {
    
        System.setProperty(ADDITIONAL_PROFILES_ENVIRONMENT_VARIABLE, "profile-1");
        
        robotProfiles.initializeAndLockProfiles();
        
        Set<String> profileList = robotProfiles.getProfileList();
        assertThat(profileList).containsExactly("profile-1");
    }
    
    // Remove profile
    // ========================================================================
    
    @Test
    @DisplayName("WHEN: Profile is removed programmatically - THEN: Profile is not active")
    void profileRemovedProgrammatically() throws IOException {
        
        robotProfiles.addProfiles("profile-1", "profile-2", "profile-3");
    
        // Remove the profile using negation
        robotProfiles.addProfiles("!profile-1");
        // Remove the profile using correct method
        robotProfiles.removeProfiles("profile-2");
        
        robotProfiles.initializeAndLockProfiles();
        
        Set<String> profileList = robotProfiles.getProfileList();
        assertThat(profileList).containsExactly("profile-3");
    }
    
    @Test
    @DisplayName("WHEN: Profile is removed via robot.profiles - THEN: Profile is not active")
    void profileRemovedFromRobotProfiles() throws IOException {
    
        robotProfiles.addProfiles("profile-1", "profile-2");
        
        // !profile-1
        JvmUtil.copyResourcesToDirectory(this, REMOVE_PROFILE_ROBOT_PROFILES_PATH, robotProfilesFile.getParentFile());
        
        robotProfiles.initializeAndLockProfiles();
        
        Set<String> profileList = robotProfiles.getProfileList();
        assertThat(profileList).containsExactly("profile-2");
    }
    
    @Test
    @DisplayName("WHEN: Profile is removed via environment variable - THEN: Profile is not active")
    void profileRemovedFromRobotProperties() throws IOException {
        
        robotProfiles.addProfiles("profile-1", "profile-2");
        
        System.setProperty(ADDITIONAL_PROFILES_ENVIRONMENT_VARIABLE, "!profile-1");
        robotProfiles.initializeAndLockProfiles();
        
        Set<String> profileList = robotProfiles.getProfileList();
        assertThat(profileList).containsExactly("profile-2");
    }
    
    // Set Multiple times
    // ========================================================================
    
    @Test
    @DisplayName("WHEN: Profile set multiple times - THEN: Profile only set once")
    void profileSetMultipleTimes() {
        final String profile1 = "foo";
        
        robotProfiles.addProfiles(profile1, profile1);
        robotProfiles.addProfiles(profile1);
        robotProfiles.initializeAndLockProfiles();
        
        Set<String> profileList = robotProfiles.getProfileList();
        assertThat(profileList).size().isEqualTo(1);
        assertThat(profileList).containsOnly(profile1);
    }
    
    // robot.profiles tests
    // ========================================================================

    @Test
    @DisplayName("WHEN: robot.profiles has commented line - THEN: Commented line is ignored")
    void commentedLine() throws IOException {

        JvmUtil.copyResourcesToDirectory(this, COMMENTED_LINES_ROBOT_PROFILES_PATH, robotProfilesFile.getParentFile());
        
        robotProfiles.initializeAndLockProfiles();
    
        Set<String> profileList = robotProfiles.getProfileList();
        assertThat(profileList).containsExactly("profile-1", "profile-5", "profile-6", "profile-8");
    }

    @Test
    @DisplayName("WHEN: robot.profiles has empty line - THEN: Empty line is ignored")
    void emptyLine() throws IOException {

        JvmUtil.copyResourcesToDirectory(this, EMPTY_LINES_ROBOT_PROFILES_PATH, robotProfilesFile.getParentFile());

        robotProfiles.initializeAndLockProfiles();
    
        Set<String> profileList = robotProfiles.getProfileList();
        assertThat(profileList).containsExactly("profile-1", "profile-2");
    }
    
    @Test
    @DisplayName("WHEN: robot.profiles has spaces or tabs between commas - THEN: Spaces and tabs are not part of parsed profile name")
    void spacesOrTabs() throws IOException {
        
        JvmUtil.copyResourcesToDirectory(this, SPACES_OR_TABS_ROBOT_PROFILES_PATH, robotProfilesFile.getParentFile());
        
        robotProfiles.initializeAndLockProfiles();
        
        Set<String> profileList = robotProfiles.getProfileList();
        assertThat(profileList).containsExactly("profile-1", "profile-2", "profile-3", "profile-4", "profile-5", "profile-6");
    }
    
    // Environment variable tests
    // ========================================================================
    
    @Test
    @DisplayName("WHEN: Environment variable has spaces or tabs between commas - THEN: Spaces and tabs are not part of parsed profile name")
    void spacesOrTabsEnvVar() throws IOException {
    
        System.setProperty(ADDITIONAL_PROFILES_ENVIRONMENT_VARIABLE, " profile-1 ,\tprofile-2\t, \tprofile-3\t ");
        
        robotProfiles.initializeAndLockProfiles();
        
        Set<String> profileList = robotProfiles.getProfileList();
        assertThat(profileList).containsExactly("profile-1", "profile-2", "profile-3");
    }
    
    // Priority loading
    // ========================================================================
    
    @Test
    @DisplayName("WHEN: Profile is set via robot.profiles - THEN: robot.profiles takes priority over programmatically set profiles")
    void profileFromRobotProfile() throws IOException {
    
        JvmUtil.copyResourcesToDirectory(this, PRIORITY_LOADING_1__ROBOT_PROFILES_PATH, robotProfilesFile.getParentFile());
    
        robotProfiles.addProfiles("profile-1");
        robotProfiles.addProfiles("profile-2");
    
        // Should remove profile-1 and add profile-3
        robotProfiles.initializeAndLockProfiles();
    
        Set<String> profileList = robotProfiles.getProfileList();
        assertThat(profileList).containsExactly("profile-2", "profile-3");
    }
    
    @Test
    @DisplayName("WHEN: Profile is set via environment variable - THEN: Environment variable takes priority over robot.profiles and programmatically set profiles")
    void priorityEnvVar() throws IOException {
        
        JvmUtil.copyResourcesToDirectory(this, PRIORITY_LOADING_2__ROBOT_PROFILES_PATH, robotProfilesFile.getParentFile());
    
    
        robotProfiles.addProfiles("profile-1");
        robotProfiles.addProfiles("profile-2");
        
        // robot.profiles: profile-3,profile-4
    
        System.setProperty(ADDITIONAL_PROFILES_ENVIRONMENT_VARIABLE, "!profile-1,!profile-3,profile-5");
    
        robotProfiles.initializeAndLockProfiles();
    
        Set<String> profileList = robotProfiles.getProfileList();
        assertThat(profileList).containsExactly("profile-2", "profile-4", "profile-5");
    }
}
