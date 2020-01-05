package com.team2073.common.proploader.model;

import org.apache.commons.configuration2.PropertiesConfiguration;

import java.io.File;

/**
 * @author Preston Briggs
 */
public class PropertyFileAccessor {

    private final String profile;
    private final File propFile;
    private final PropertiesConfiguration propConfig;
    private final boolean errorCreatingFile;

    public PropertyFileAccessor(String profile, File propFile, PropertiesConfiguration propConfig, boolean errorCreatingFile) {
        this.profile = profile;
        this.propFile = propFile;
        this.propConfig = propConfig;
        this.errorCreatingFile = errorCreatingFile;
    }

    public String getProfile() {
        return profile;
    }

    public File getPropFile() {
        return propFile;
    }

    public PropertiesConfiguration getPropConfig() {
        return propConfig;
    }

    public boolean isErrorCreatingFile() {
        return errorCreatingFile;
    }

    @Override
    public String toString() {
        return profile + " | " + propFile.getAbsolutePath();
    }
}
