package com.team2073.common.util;

import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.revrobotics.CANSparkMax;

import java.util.ArrayList;

public class StrongCurrentDetector {
    private static ArrayList<CANSparkMax> neoArray = new ArrayList<>();
    private static ArrayList<CANSparkMax> neo550Array = new ArrayList<>();
    private static ArrayList<TalonFX> falconArray = new ArrayList<>();
    private static ArrayList<Integer> stallingNeoIDs = new ArrayList<>();
    private static ArrayList<Integer> stallingNeo550IDs = new ArrayList<>();
    private static ArrayList<Integer> stallingFalconIDs = new ArrayList<>();
    private static final double STALL_CURRENT_NEO = 181d;
    private static final double STALL_CURRENT_NEO_550 = 12d;
    private static final double STALL_CURRENT_FALCON = 257d;
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
            if (!neo550Array.contains(sparkMax)) {
                neo550Array.add(sparkMax);
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
            neo550Array.remove(sparkMax);
        }
    }
    /**
     * @param talons takes in an infinite number of
     *               TalonFX objects that should
     *               only be routed Falcons
     * Checks to make sure that the id is not already in use and
     * adds it to the ArrayList of Falcons.
     */
    public static void addFalcons(TalonFX...talons) {
        for (TalonFX talon : talons) {
            if (!falconArray.contains(talon)) {
                falconArray.add(talon);
            }
        }
    }
    /**
     * @param talons takes in an infinite number of
     *               TalonFX objects that should
     *               only be routed Falcons
     * Checks to make sure that the id is in use and
     * removes it to the ArrayList of Falcons.
     */
    public static void removeFalcons(TalonFX...talons){
        for (TalonFX talon : talons) {
            falconArray.remove(talon);
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
        for(CANSparkMax sparkMax : neoArray) {
            if (sparkMax.getOutputCurrent() >= STALL_CURRENT_NEO && !stallingNeoIDs.contains(sparkMax.getDeviceId())) {
                isStalling = true;
                stallingNeoIDs.add(sparkMax.getDeviceId());
            } else if(sparkMax.getOutputCurrent() < STALL_CURRENT_NEO && stallingNeoIDs.contains(sparkMax.getDeviceId())){
                stallingNeoIDs.remove((Integer) sparkMax.getDeviceId());
                count++;
            }else{
                count++;
            }
        }
        for (CANSparkMax sparkMax : neo550Array) {
            if (sparkMax.getOutputCurrent() >= STALL_CURRENT_NEO_550 && !stallingNeo550IDs.contains(sparkMax.getDeviceId())) {
                isStalling = true;
                stallingNeo550IDs.add(sparkMax.getDeviceId());
            } else if(sparkMax.getOutputCurrent() < STALL_CURRENT_NEO_550 && stallingNeo550IDs.contains(sparkMax.getDeviceId())){
                stallingNeo550IDs.remove((Integer) sparkMax.getDeviceId());
                count++;
            } else {
                count++;
            }
        }
        for (TalonFX talon : falconArray){
            if(talon.getSupplyCurrent() >= STALL_CURRENT_FALCON && !stallingFalconIDs.contains(talon.getDeviceID())){
                isStalling = true;
                stallingFalconIDs.add(talon.getDeviceID());
            } else if (talon.getSupplyCurrent() < STALL_CURRENT_FALCON && stallingFalconIDs.contains(talon.getDeviceID())) {
                stallingFalconIDs.remove((Integer) talon.getDeviceID());
                count++;
            }else{
                count++;
            }
        }
        if(count == (neo550Array.size() + neoArray.size() + falconArray.size())){
            isStalling = false;
        }
    }

    /**
     * Returns the boolean isStalling that turns true
     * if there is at least 1 motor stalling.
     */
    public static boolean getIsStalling(){
        return isStalling;
    }

    /**
     * Returns an ArrayList with the IDs of stalling
     * Neos.
     */
    public static ArrayList<Integer> getStallingNeoIDs(){
        return stallingNeoIDs;
    }

    /**
     * Returns an ArrayList with the IDs of stalling
     * Neo550s.
     */
    public static ArrayList<Integer> getStallingNeo550IDs(){
        return stallingNeo550IDs;
    }

    /**
     * Returns an ArrayList with the IDs of stalling
     * Falcons.
     */
    public static ArrayList<Integer> getStallingFalconIDs(){
        return stallingFalconIDs;
    }
}
