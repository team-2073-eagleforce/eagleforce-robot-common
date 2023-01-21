package com.team2073.common.util;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import edu.wpi.first.wpilibj.CAN;
import lombok.Getter;

import java.util.ArrayList;

public class CANSparkMaxManager {
    private static ArrayList<CANSparkMax> neoArray = new ArrayList<>();
    private static ArrayList<CANSparkMax> neo550Array = new ArrayList<>();
    private static ArrayList<Integer> stallingNeoIDs = new ArrayList<>();
    private static ArrayList<Integer> stallingNeo550IDs = new ArrayList<>();
    private static final double STALL_CURRENT_NEO = 181d;//@TODO Add an actual current.
    private static final double STALL_CURRENT_NEO_550 = 12d;//@TODO Add an actual current.
    private static boolean isStalling;
    /**
     * @param sparkMaxes takes in an infinite number of
     *                   CANSparkMax objects that should
     *                   only be routed to regular Neos.
     * Checks to make sure that the id is not already in
     * use and adds it to the ArrayList of Neo Spark Maxes.
     *
     */
    public static void addNeos(CANSparkMax...sparkMaxes) {
        for (CANSparkMax sparkMax : sparkMaxes) {
            if (!neoArray.contains(sparkMax)) {
                neoArray.add(sparkMax);
            }
        }
    }

    /**
     * @param sparkMaxes takes in an infinite number of
     *                   CANSparkMax objects that should
     *                   only be routed to regular Neos.
     * Checks to make sure that the id is in use and
     * removes it to the ArrayList of Neo Spark Maxes.
     */
    public static void removeNeos(CANSparkMax...sparkMaxes){
        for (CANSparkMax sparkMax : sparkMaxes) {
            neoArray.remove(sparkMax);
        }
    }
    /**
     * @param sparkMaxes takes in an infinite number of
     *                   CANSparkMax objects that should
     *                   only be routed to Neo550s.
     * Checks to make sure that the id is not already in
     * use and adds it to the ArrayList of Neo550 Spark Maxes.
     *
     */
    public static void addNeo550s(CANSparkMax...sparkMaxes) {
        for (CANSparkMax sparkMax : sparkMaxes) {
            if (!neoArray.contains(sparkMax)) {
                neoArray.add(sparkMax);
            }
        }
    }
    /**
     * @param sparkMaxes takes in an infinite number of
     *                   CANSparkMax objects that should
     *                   only be routed to Neo550s.
     * Checks to make sure that the id is in use and
     * removes it to the ArrayList of Neo550 Spark Maxes.
     */
    public static void removeNeo550s(CANSparkMax...sparkMaxes){
        for (CANSparkMax sparkMax : sparkMaxes) {
            neoArray.remove(sparkMax);
        }
    }

    /**
     * Checks each array to make sure that they are
     * below the current limit for their respective
     * motor, if not then it will turn isStalling to
     * true.
     */
    public static void manage(){
        int count = 0;
        for(CANSparkMax sparkMax : neoArray){
            if(sparkMax.getOutputCurrent() >= STALL_CURRENT_NEO){
                isStalling = true;
                stallingNeoIDs.add(sparkMax.getDeviceId());
            }else{
                count++;
            }
        }
        for (CANSparkMax sparkMax : neo550Array) {
            if (sparkMax.getOutputCurrent() >= STALL_CURRENT_NEO_550) {
                isStalling = true;
                stallingNeo550IDs.add(sparkMax.getDeviceId());
            } else {
                count++;
            }
        }
        if(count == (neo550Array.size() + neoArray.size())){
            isStalling = false;
        }
    }
}
