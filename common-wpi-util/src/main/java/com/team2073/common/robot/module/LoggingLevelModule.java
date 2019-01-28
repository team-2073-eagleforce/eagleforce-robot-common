package com.team2073.common.robot.module;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import com.team2073.common.periodic.AsyncPeriodicRunnable;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

/**
 * @author Preston Briggs
 */
public class LoggingLevelModule implements AsyncPeriodicRunnable {
    
    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    private static final String LOGGING_LEVEL_PREFIX = "log.";
    private final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
    private final SendableChooser<Level> loggingLevelChooser = new SendableChooser<>();
    private final Preferences preferences = Preferences.getInstance();
    private final String defaultPackage = getClass().getPackage().getName();
    private final String defaultPackagePrefix = defaultPackage + ".";
    
    public LoggingLevelModule(String... loggerNames) {
        removeOldLoggingLevels();
        initLoggingLevels(loggerNames);
    }
    
    @Deprecated
    // TODO: Why is this deprecated?
    private void removeOldLoggingLevels() {
        String oldLoggingLevelPrefix = "logging.level.";
        preferences.getKeys().stream().filter(key -> key.startsWith(oldLoggingLevelPrefix)).forEach(oldKey -> {
            String loggerName = oldKey.substring(oldLoggingLevelPrefix.length());
            if (loggerName.startsWith(defaultPackagePrefix)) {
                loggerName = loggerName.substring(defaultPackagePrefix.length());
            }
            String newKey = LOGGING_LEVEL_PREFIX + loggerName;
            if (!preferences.containsKey(newKey)) {
                String value = preferences.getString(oldKey, "");
                preferences.putString(newKey, value);
            }
            preferences.remove(oldKey);
        });
    }
    
    private void initLoggingLevels(String... loggerNames) {
        // Add chooser for default logging level
        loggingLevelChooser.addObject("All", Level.ALL);
        loggingLevelChooser.addObject("Trace", Level.TRACE);
        loggingLevelChooser.addObject("Debug", Level.DEBUG);
        loggingLevelChooser.addDefault("Info", Level.INFO);
        loggingLevelChooser.addObject("Warn", Level.WARN);
        loggingLevelChooser.addObject("Error", Level.ERROR);
        loggingLevelChooser.addObject("Off", Level.OFF);
        SmartDashboard.putData("Logging Level", loggingLevelChooser);
        
        // Add convenient logger names to preferences
        for (String loggerName : loggerNames) {
            String key = LOGGING_LEVEL_PREFIX + loggerName;
            if (!preferences.containsKey(key)) {
                preferences.putString(key, "");
            }
        }
        
        // temp: delete
        preferences.putString("Robot", "trace");
    }
    
    @Override
    public void onPeriodicAsync() {
        updateLoggerLevels();
    }
    
    private void updateLoggerLevels() {
        // Set root (default) logger level based on selection
        ch.qos.logback.classic.Logger rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
        rootLogger.setLevel(loggingLevelChooser.getSelected());
        
        // Reset all logger levels except root
        loggerContext.getLoggerList().stream().filter(logger -> !logger.getName().equals(Logger.ROOT_LOGGER_NAME))
                .forEach(logger -> logger.setLevel(null));
        
        // Set logger levels based on preferences
        preferences.getKeys().stream().filter(key -> key.startsWith(LOGGING_LEVEL_PREFIX)).forEach(key -> {
            // Convert preference value to logger level
            String levelString = preferences.getString(key, null);
            Level level = Level.toLevel(levelString, null);
            
            // Convert preference key to logger name
            String loggerName = key.substring(LOGGING_LEVEL_PREFIX.length());
            
            // Check if loggerName is a fully qualified class name or package name.
            boolean isValidLoggerName = false;
            try {
                boolean initializeClass = false;
                Class.forName(loggerName, initializeClass, getClass().getClassLoader());
                isValidLoggerName = true;
            } catch (ClassNotFoundException e) {
                String resourceName = loggerName.replace('.', '/');
                URL resourceUrl = getClass().getClassLoader().getResource(resourceName);
                isValidLoggerName = resourceUrl != null;
            }
            if (!isValidLoggerName) {
                if (loggerName.isEmpty()) {
//					Handles empty logger name semicolon
                    loggerName = defaultPackage;
                } else {
                    loggerName = defaultPackagePrefix + loggerName;
                }
                
            }
            
            // System.out.println("Updating logger: " + loggerName);
            // Set logger level
            ch.qos.logback.classic.Logger logger = loggerContext.getLogger(loggerName);
            logger.setLevel(level);
        });
    }
}
