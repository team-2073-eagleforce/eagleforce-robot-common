package com.team2073.common.config;

import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * @author Preston Briggs
 */
public class RobotDirectory {

    private File rootDir = new File(FileUtils.getUserDirectory(), "robot");
    private File confDir = new File(rootDir, "conf");
    private File logDir = new File(rootDir, "log");
    private File inputDir = new File(rootDir, "in");
    private File outputDir = new File(rootDir, "out");
    private File tempDir = new File(FileUtils.getTempDirectory(), "robot-temp-files");

    public File getRootDir() {
        return rootDir;
    }

    public File getConfDir() {
        return confDir;
    }

    public File getLogDir() {
        return logDir;
    }

    public File getInputDir() {
        return inputDir;
    }

    public File getOutputDir() {
        return outputDir;
    }

    public File getTempDir() {
        return tempDir;
    }
}
