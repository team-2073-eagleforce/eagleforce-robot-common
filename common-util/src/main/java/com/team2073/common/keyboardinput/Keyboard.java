package com.team2073.common.keyboardinput;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;

/**
 * @author Ethan See
 */
@Getter
@Setter
public class Keyboard  {

    public static final Keyboard instance = new Keyboard();

    private HashMap<String, NetworkTableEntry> keys = new HashMap<>();

    public String[] availableKeys = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N" , "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    NetworkTableInstance ntinstance = NetworkTableInstance.getDefault();
    NetworkTable nt;

    private Keyboard() {
        nt = ntinstance.getTable("Keyboard");
        setup();
    }

    public void setup() {
        for (String key : availableKeys) {
            keys.put(key, nt.getEntry(key));
        }
    }
    public boolean isAllowedKey(String k) {
        k = k.toUpperCase();
        for (String key: availableKeys) {
            if (key.equals(k))
                return true;
        }
        return false;
    }
}
