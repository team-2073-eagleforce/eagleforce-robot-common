package com.team2073.common.datarecorder.datalogtool;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.util.datalog.*;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import java.util.ArrayList;

/**
 * A Handler to make DataLogging with DataLogTool from WPI more uniform
 *
 * Can't extend from WPI DataLogManager so copied and pasted their code to allow extending
 * @author Ethan See
 */
public class DataLogToolHandler extends DataLogManager{

    private static double timeInterval; //Given in seconds
    private static double initTime;
    private static DataLog dataLog = getLog();

    private static DoubleLogEntry timeStamp = new DoubleLogEntry(dataLog, "Timestamp");

    private static ArrayList<DoubleLogEntry> doubleEntries = new ArrayList<>();
    private static ArrayList<Double> doubleVariables = new ArrayList<>();
    private static ArrayList<BooleanLogEntry> booleanEntries = new ArrayList<>();
    private static ArrayList<Boolean> booleanVariables = new ArrayList<>();
    private static ArrayList<StringLogEntry> stringEntries = new ArrayList<>();
    private static ArrayList<String> stringVariables = new ArrayList<>();

    public static void start(double tI) {
        DataLogManager.start();
        initTime = Timer.getFPGATimestamp();
        timeInterval = tI;
    }

    /**
     * Default time interval of 0.5s
     */
    public static void start() {
        DataLogManager.start();
        initTime = Timer.getFPGATimestamp();
        timeInterval = 0.5;
    }

    public static void addEntry(double doubleEntry, String name) {
        DoubleLogEntry doubleLogEntry = new DoubleLogEntry(dataLog, name);
        doubleEntries.add(doubleLogEntry);
        doubleVariables.add(doubleEntry);
    }

    public static void addEntry(boolean booleanEntry, String name) {
        booleanEntries.add(new BooleanLogEntry(dataLog, name));
        booleanVariables.add(booleanEntry);
    }

    public static void addEntry(String stringEntry, String name) {
        stringEntries.add(new StringLogEntry(dataLog, name));
        stringVariables.add(stringEntry);
    }

    public static void logTimedEntries() {
        if (Timer.getFPGATimestamp() >= initTime + timeInterval) {
            timeStamp.append(Timer.getFPGATimestamp());
            for (int i = 0; i < doubleEntries.size(); i++)
                doubleEntries.get(i).append(doubleVariables.get(i));

            for (int j = 0; j < booleanEntries.size(); j++)
                booleanEntries.get(j).append(booleanVariables.get(j));

            for (int i = 0; i < stringEntries.size(); i++)
                stringEntries.get(i).append(stringVariables.get(i));

            initTime = Timer.getFPGATimestamp();
        }
    }

}
