package com.team2073.common.controllers.mapping;

import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.command.Command;

/**
 * @author pbriggs
 */
public class ButtonMapping {

    private Button button;
    private Command command;

    private enum TriggerType{
        WHEN_PRESSED,
        WHILE_HELD,
        WHEN_RELEASED,
        TOGGLE_WHEN_PRESSED,
        CANCEL_WHEN_PRESSED;
    }

    public void setButton(Button button) {
        this.button = button;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public void instantiateMapping(Button button, TriggerType triggerType, Command command) {
        switch(triggerType) {
            case WHEN_PRESSED:
                button.whenPressed(command);
            case WHILE_HELD:
                button.whileHeld(command);
            case WHEN_RELEASED:
                button.whenReleased(command);
            case TOGGLE_WHEN_PRESSED:
                button.toggleWhenPressed(command);
            case CANCEL_WHEN_PRESSED:
                button.cancelWhenPressed(command);
        }
    }
}
