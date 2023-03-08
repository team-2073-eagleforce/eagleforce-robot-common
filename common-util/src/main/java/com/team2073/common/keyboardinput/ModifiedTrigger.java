// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package com.team2073.common.keyboardinput;

import static edu.wpi.first.util.ErrorMessages.requireNonNullParam;

import edu.wpi.first.math.filter.Debouncer;
import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.event.BooleanEvent;
import edu.wpi.first.wpilibj.event.EventLoop;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.Subsystem;
import lombok.Getter;

import java.util.ArrayList;
import java.util.function.BooleanSupplier;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import org.graalvm.compiler.loop.MathUtil;

/**
 * A Modified version of {@link Trigger} to add keyboard inputs to a Smartdashboard array to be
 * read from KeyboardInput program
 *
 * May need to be updated every year (Copy and Paste the newest version from WPI and re-add the code
 *
 */
@Getter
public class ModifiedTrigger implements BooleanSupplier {

    private static ArrayList<String> listOfCommands = new ArrayList<>();

    private static ArrayList<String> typeOfCommand = new ArrayList<>();

    private static ArrayList<String> allowedKeys = new ArrayList<>();

    private final BooleanSupplier m_condition;
    private final EventLoop m_loop;

    /**
     * Creates a new ModifiedTrigger based on the given condition.
     *
     * @param loop The loop instance that polls this ModifiedTrigger.
     * @param condition the condition represented by this ModifiedTrigger
     */
    public ModifiedTrigger(EventLoop loop, BooleanSupplier condition) {
        m_loop = requireNonNullParam(loop, "loop", "ModifiedTrigger");
        m_condition = requireNonNullParam(condition, "condition", "ModifiedTrigger");
    }

    /**
     * Creates a new ModifiedTrigger based on the given condition.
     *
     * <p>Polled by the default scheduler button loop.
     *
     * @param condition the condition represented by this ModifiedTrigger
     */
    public ModifiedTrigger(BooleanSupplier condition) {
        this(CommandScheduler.getInstance().getDefaultButtonLoop(), condition);
    }

    /** Creates a new ModifiedTrigger that is always `false`. */
    @Deprecated
    public ModifiedTrigger() {
        this(() -> false);
    }

    public static void addCommandsToSmartDashboard() {
        String[] c = listOfCommands.toArray(new String[listOfCommands.size()]);
        SmartDashboard.putData("Commands", builder ->
                builder.addStringArrayProperty("ListOfCommands", () -> c, null));

        String[] typeOfCom = typeOfCommand.toArray(new String[typeOfCommand.size()]);
        SmartDashboard.putData("Commands", builder ->
                builder.addStringArrayProperty("TypeOfCommands", () -> typeOfCom, null));

//        for (int i = 0; i < allowedKeys.size(); i++) {
//            if (allowedKeys.get(i)== null)
//        }
        String[] a = allowedKeys.toArray(new String[allowedKeys.size()]);
        SmartDashboard.putData("AllowedKeys", builder ->
                builder.addStringArrayProperty("Keys", () -> a, null));
    }

    /**
     * Starts the given command whenever the condition changes from `false` to `true`.
     *
     * @param command the command to start
     * @return this ModifiedTrigger, so calls can be chained
     */
    public ModifiedTrigger onTrue(Command command, String key) {
        listOfCommands.add(command.getName());
        typeOfCommand.add("onTrue");
        allowedKeys.add(key);
        requireNonNullParam(command, "command", "onRising");
        m_loop.bind(
                new Runnable() {
                    private boolean m_pressedLast = m_condition.getAsBoolean();

                    @Override
                    public void run() {
                        boolean pressed = m_condition.getAsBoolean();

                        if (!m_pressedLast && pressed) {
                            command.schedule();
                        }

                        m_pressedLast = pressed;
                    }
                });
        return this;
    }


    public ModifiedTrigger whileTrue(Command command, String key) {
        listOfCommands.add(command.getName());
        typeOfCommand.add("whileTrue");
        allowedKeys.add(key);
        requireNonNullParam(command, "command", "whileHigh");
        m_loop.bind(
                new Runnable() {
                    private boolean m_pressedLast = m_condition.getAsBoolean();

                    @Override
                    public void run() {
                        boolean pressed = m_condition.getAsBoolean();

                        if (!m_pressedLast && pressed) {
                            command.schedule();
                        } else if (m_pressedLast && !pressed) {
                            command.cancel();
                        }

                        m_pressedLast = pressed;
                    }
                });
        return this;
    }

    /**
     * Toggles a command when the condition changes from `false` to `true`.
     *
     * @param command the command to toggle
     * @return this ModifiedTrigger, so calls can be chained
     */
    public ModifiedTrigger toggleOnTrue(Command command, String key) {
        listOfCommands.add(command.getName());
        typeOfCommand.add("toggleOnTrue");
        allowedKeys.add(key);
        requireNonNullParam(command, "command", "toggleOnRising");
        m_loop.bind(
                new Runnable() {
                    private boolean m_pressedLast = m_condition.getAsBoolean();

                    @Override
                    public void run() {
                        boolean pressed = m_condition.getAsBoolean();

                        if (!m_pressedLast && pressed) {
                            if (command.isScheduled()) {
                                command.cancel();
                            } else {
                                command.schedule();
                            }
                        }

                        m_pressedLast = pressed;
                    }
                });
        return this;
    }

    @Override
    public boolean getAsBoolean() {
        return m_condition.getAsBoolean();
    }

    /**
     * Composes two ModifiedTriggers with logical OR.
     *
     * @param ModifiedTrigger the condition to compose with
     * @return A ModifiedTrigger which is active when either component ModifiedTrigger is active.
     */
    public ModifiedTrigger and(BooleanSupplier ModifiedTrigger) {
        return new ModifiedTrigger(() -> m_condition.getAsBoolean() && ModifiedTrigger.getAsBoolean());
    }

    /**
     * Composes two ModifiedTriggers with logical OR.
     *
     * @param ModifiedTrigger the condition to compose with
     * @return A ModifiedTrigger which is active when either component ModifiedTrigger is active.
     */
    public ModifiedTrigger or(BooleanSupplier ModifiedTrigger) {
        return new ModifiedTrigger(() -> m_condition.getAsBoolean() || ModifiedTrigger.getAsBoolean());
    }

    /**
     * Creates a new ModifiedTrigger that is active when this ModifiedTrigger is inactive, i.e. that acts as the
     * negation of this ModifiedTrigger.
     *
     * @return the negated ModifiedTrigger
     */
    public ModifiedTrigger negate() {
        return new ModifiedTrigger(() -> !m_condition.getAsBoolean());
    }

    /**
     * Creates a new debounced ModifiedTrigger from this ModifiedTrigger - it will become active when this ModifiedTrigger has
     * been active for longer than the specified period.
     *
     * @param seconds The debounce period.
     * @return The debounced ModifiedTrigger (rising edges debounced only)
     */
    public ModifiedTrigger debounce(double seconds) {
        return debounce(seconds, Debouncer.DebounceType.kRising);
    }

    /**
     * Creates a new debounced ModifiedTrigger from this ModifiedTrigger - it will become active when this ModifiedTrigger has
     * been active for longer than the specified period.
     *
     * @param seconds The debounce period.
     * @param type The debounce type.
     * @return The debounced ModifiedTrigger.
     */
    public ModifiedTrigger debounce(double seconds, Debouncer.DebounceType type) {
        return new ModifiedTrigger(
                new BooleanSupplier() {
                    final Debouncer m_debouncer = new Debouncer(seconds, type);

                    @Override
                    public boolean getAsBoolean() {
                        return m_debouncer.calculate(m_condition.getAsBoolean());
                    }
                });
    }

}
