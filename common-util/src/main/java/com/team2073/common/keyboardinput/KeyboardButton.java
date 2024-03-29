package com.team2073.common.keyboardinput;

import edu.wpi.first.networktables.NetworkTableEntry;
import com.team2073.common.keyboardinput.NetworkButton;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import lombok.Getter;
import org.littletonrobotics.junction.AutoLog;
import org.littletonrobotics.junction.Logger;

import java.util.ArrayList;

/**
 * @author Ethan See
 */
@Getter
public class KeyboardButton extends NetworkButton {


    private String key;
//    private static ArrayList<String> allowedKeys = new ArrayList<>();
    /**
     * Pass in a character on the keyboard A-Z and used in code the same way as normal controllers
     * @param key
     */
    public KeyboardButton(String key) {
        super(Keyboard.instance.getNt(), key.toUpperCase());
        this.key = key;
//        allowedKeys.add(key);
        Keyboard keyboard = Keyboard.instance;
        if (!keyboard.isAllowedKey(key.toUpperCase())) {
            throw new RuntimeException("Invalid Key. A-Z only");
        }
        keyboard.getKeys().get(key).setBoolean(false);
    }


}
