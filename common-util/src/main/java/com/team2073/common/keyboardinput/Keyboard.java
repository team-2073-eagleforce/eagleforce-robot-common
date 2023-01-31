package com.team2073.common.keyboardinput;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @author Ethan See
 */
@Getter
@Setter
public class Keyboard  {

    public static final Keyboard instance = new Keyboard();

    private HashMap<String, NetworkTableEntry> keys = new HashMap<>();

    public String[] availableKeys = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N" , "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", ",", ".","0","1","2","3","4","5","6","7","8","9"};

    private ArrayList<String> allKeys = new ArrayList<>();
    NetworkTableInstance ntinstance = NetworkTableInstance.getDefault();
    NetworkTable nt;

    private Keyboard() {
        nt = ntinstance.getTable("Keyboard");
        setup();
    }

    public void setup() {
        allKeys.addAll(Arrays.asList(availableKeys));

        for (int i = 0; i <=9; i++) {
            allKeys.add(Integer.toString(i));
        }
        for(int i  = 0; i< allKeys.size(); i++) {
            nt.getEntry(allKeys.get(i)).delete();
        }
        for(int i  = 0; i< allKeys.size(); i++) {
            keys.put(allKeys.get(i), nt.getEntry(allKeys.get(i)));
        }
    }
    public boolean isAllowedKey(String k) {
        k = k.toUpperCase();
        for(int i  = 0; i< allKeys.size(); i++) {
            if (allKeys.get(i).equals(k))
                return true;
        }
        return false;
    }

    public HashMap<String, NetworkTableEntry> getKeys() {
        return keys;
    }
}
