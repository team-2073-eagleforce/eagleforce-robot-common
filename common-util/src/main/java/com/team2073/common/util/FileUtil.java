package com.team2073.common.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author Preston Briggs
 */
public abstract class FileUtil {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss.SSS");

    public static String formatTimestamp(LocalDateTime timestamp) {
        return timestamp.format(formatter);
    }

    public static File getTimestampedTempDir() {
        return getTimestampedTempDir(null);
    }

    public static File getTimestampedTempDir(String dirName) {

        File targetDir = FileUtils.getTempDirectory();

        return getTimestampedDir(targetDir, dirName);
    }

    public static File getTimestampedUserDir() {
        return getTimestampedUserDir(null);
    }

    public static File getTimestampedUserDir(String dirName) {

        File targetDir = FileUtils.getUserDirectory();

        return getTimestampedDir(targetDir, dirName);
    }

    public static File getTimestampedDir(File targetDir) {
        return getTimestampedDir(targetDir, null);
    }

    public static File getTimestampedDir(File targetDir, String dirName) {

        if (dirName != null)
            targetDir = new File(targetDir, dirName);

        String timestamp = formatTimestamp(LocalDateTime.now());
        targetDir = new File(targetDir, timestamp);

        return targetDir;
    }
    
    public static String normalize(String path) {
        if (path.startsWith("~")) {
            File userDir = FileUtils.getUserDirectory();
            String theRest = path.substring(1);
            path = new File(userDir, theRest).getAbsolutePath();
        }
        path = FilenameUtils.normalize(path);
        return path;
    }

}
