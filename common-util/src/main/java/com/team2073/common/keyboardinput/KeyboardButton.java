package com.team2073.common.keyboardinput;

import edu.wpi.first.networktables.NetworkTableEntry;
import com.team2073.common.keyboardinput.NetworkButton;

/**
 * @author Ethan See
 */
public class KeyboardButton extends NetworkButton {

    /**
     * Pass in a character on the keyboard A-Z and used in code the same way as normal controllers
     * @param key
     */
    public KeyboardButton(String key) {
        super(Keyboard.instance.getNt(), key.toUpperCase());
        Keyboard keyboard = Keyboard.instance;
        if (!keyboard.isAllowedKey(key.toUpperCase())) {
            throw new RuntimeException("Invalid Key. A-Z only");
        }
        keyboard.getKeys().get(key).setBoolean(false);
    }
}
