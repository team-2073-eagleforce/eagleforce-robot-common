package com.team2073.common.config;

import com.team2073.common.assertion.Assert;
import com.team2073.common.ctx.RobotContext;
import com.team2073.common.util.Ex;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.team2073.common.util.ClassUtil.*;

/**
 * @author Preston Briggs
 */
public class RobotProfiles {
    
    public static final String ADDITIONAL_PROFILES_ENVIRONMENT_VARIABLE = "ROBOT_CONTEXT_ADDITIONAL_PROFILES";
    private static final String ENVIRONMENT_VARIABLE_SOURCE = "ROBOT_CONTEXT_ADDITIONAL_PROFILES environment variable";
    private static final String ROBOT_PROFILES_FILE_NAME = "robot.profiles";
    private static final String ROBOT_PROFILES_FILE_SOURCE = "robot.profiles file";
    
    private Logger log = LoggerFactory.getLogger(getClass());
    
    private final Set<String> profileList = new LinkedHashSet<>();
    private File confDir = RobotContext.getInstance().getRobotDirectory().getConfDir();
    private File robotProfilesFile = new File(confDir, ROBOT_PROFILES_FILE_NAME);
    private boolean profilesLocked = false;
    
    public File getRobotProfilesFile() {
        return robotProfilesFile;
    }
    
    public RobotProfiles setRobotProfilesFile(File robotProfilesFile) {
        Assert.assertNotNull(robotProfilesFile, "robotProfilesFile");
        this.robotProfilesFile = robotProfilesFile;
        return this;
    }
    
    public RobotProfiles clearProfiles() {
        failIfInitializationComplete("clearProfiles");
        log.debug("Removing all profiles. Current profiles: [{}].", getProfileListToStringWithoutLocking());
        profileList.clear();
        return this;
    }
    
    public RobotProfiles overwriteProfiles(String... profiles) {
        return overwriteProfiles(Arrays.asList(profiles));
    }
    
    public RobotProfiles overwriteProfiles(Collection<String> profileList) {
        failIfInitializationComplete("overwriteProfiles");
        clearProfiles();
        addProfilesInternal(profileList, "overwriteProfiles(...)");
        return this;
    }
    
    public RobotProfiles addProfiles(String... profiles) {
        return addProfiles(Arrays.asList(profiles));
    }
    
    public RobotProfiles addProfiles(Collection<String> profileList) {
        return addProfilesInternal(profileList, "addProfiles(...)");
    }
    
    private RobotProfiles addProfilesInternal(Collection<String> profileList, String profileSource) {
        failIfInitializationComplete("addProfiles");
        parseProfiles(profileList, "addProfiles(...)");
        return this;
    }
    
    public RobotProfiles removeProfiles(String... profiles) {
        return removeProfiles(Arrays.asList(profiles));
    }
    
    public RobotProfiles removeProfiles(Collection<String> profileList) {
        return removeProfilesInternal(profileList, "removeProfiles(...)");
    }
    
    public RobotProfiles removeProfilesInternal(Collection<String> profileList, String profileSource) {
        List<String> profilesToRemove = profileList.stream().map(profile -> "!" + profile).collect(Collectors.toList());
        parseProfiles(profilesToRemove, profileSource);
        return this;
    }
    
    public Set<String> getProfileList() {
        initializeAndLockProfiles();
        return Collections.unmodifiableSet(profileList);
    }
    
    private String getProfileListToStringWithoutLocking() {
        return profileList.stream().collect(Collectors.joining(","));
    }
    
    public String getProfileListToString() {
        return getProfileList().stream().collect(Collectors.joining(","));
    }
    
    public void initializeAndLockProfiles() {
        if (!profilesLocked) {
    
            // Profile priority (the last one wins):
            // 1) Programmatically ( overwriteProfiles() and addProfiles() )
            //      These are already set at this point
            // 2) ~/robot/conf/robot.profiles file
            // 3) Environment variable ROBOT_CONTEXT_ADDITIONAL_PROFILES
    
            initializeProfilesFromFile();
            initializeProfilesFromEnvVar();
    
            profilesLocked = true;
            log.info("Initialized profiles and locked from further modification. Active profiles [{}].", getProfileListToString());
        }
    }
    
    private void initializeProfilesFromFile() {
        if (!robotProfilesFile.exists()) {
            
            if (log.isDebugEnabled())
                log.debug("No robot profiles file found at [{}]. This file can be used to set profiles dynamically per robot. See documentation for details.", robotProfilesFile.getAbsolutePath());
            else
                log.info("No robot profiles file found at [{}].", robotProfilesFile.getAbsolutePath());
            
        } else {
            try {
                List<String> profiles = new ArrayList<>();
                List<String> lines = Files.readAllLines(robotProfilesFile.toPath());
    
                // Showing an example of what parsing might look like at each step
                for (String line : lines) { // line: " profile-5, profile-6 # profile-7"
    
                    line = line.trim(); // line: "profile-5, profile-6 # profile-7"
                    
                    if (StringUtils.isBlank(line))
                        continue;
    
                    if (line.startsWith("#"))
                        continue;
                    
                    // Ignore anything after the comment
                    line = line.split("#")[0].trim(); // line: "profile-5, profile-6"
                    
                    if (StringUtils.isBlank(line))
                        continue;
                    
                    String[] split = line.split(","); // split: ["profile-5"," profile-6"]
                    
                    profiles.addAll(Arrays.asList(split));
                }
                
                parseProfiles(profiles, ROBOT_PROFILES_FILE_SOURCE);
                
            } catch (IOException e) {
                log.warn("Exception parsing robot profiles files [{}]. Some profiles may not have been activated. " +
                        "Current profiles: [{}].", robotProfilesFile.getAbsolutePath(), getProfileListToString());
            }
        }
    }
    
    private void initializeProfilesFromEnvVar() {
        String additionalProfiles = System.getProperty(ADDITIONAL_PROFILES_ENVIRONMENT_VARIABLE);
        if (additionalProfiles != null) {
            String[] split = additionalProfiles.split(",");
            parseProfiles(Arrays.asList(split), ENVIRONMENT_VARIABLE_SOURCE);
        }
    }
    
    private void parseProfiles(Collection<String> profileList, String profileSource) {
    
        for (String profile : profileList) {
        
            if (!StringUtils.isBlank(profile)) {
                profile = profile.trim();
            
                if (profile.startsWith("!")) {
                    
                    // Remove all leading !'s
                    while (profile.startsWith("!"))
                        profile = profile.substring(1);
                    
                    log.debug("Removing profile [{}]. Source [{}].", profile, profileSource);
                    this.profileList.remove(profile);
                    
                } else {
    
                    log.debug("Adding profile [{}]. Source [{}].", profile, profileSource);
                    this.profileList.add(profile);
                }
            }
        }
    
    }
    
    private void failIfInitializationComplete(String attemptedAction) {
        if (profilesLocked)
            throw Ex.illegalState("Cannot perform action [{}]. Profiles have already been accessed at least once.",
                    attemptedAction, simpleName(this));
    }
}
