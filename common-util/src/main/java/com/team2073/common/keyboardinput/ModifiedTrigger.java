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
    }

    /**
     * Starts the given command whenever the condition changes from `false` to `true`.
     *
     * @param command the command to start
     * @return this ModifiedTrigger, so calls can be chained
     */
    public ModifiedTrigger onTrue(Command command) {
        listOfCommands.add(command.getName());
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

    /**
     * Starts the given command whenever the condition changes from `true` to `false`.
     *
     * @param command the command to start
     * @return this ModifiedTrigger, so calls can be chained
     */
    public ModifiedTrigger onFalse(Command command) {
        listOfCommands.add(command.getName());
        requireNonNullParam(command, "command", "onFalling");
        m_loop.bind(
                new Runnable() {
                    private boolean m_pressedLast = m_condition.getAsBoolean();

                    @Override
                    public void run() {
                        boolean pressed = m_condition.getAsBoolean();

                        if (m_pressedLast && !pressed) {
                            command.schedule();
                        }

                        m_pressedLast = pressed;
                    }
                });
        return this;
    }

    /**
     * Starts the given command when the condition changes to `true` and cancels it when the condition
     * changes to `false`.
     *
     * <p>Doesn't re-start the command if it ends while the condition is still `true`. If the command
     * should restart, see {@link edu.wpi.first.wpilibj2.command.RepeatCommand}.
     *
     * @param command the command to start
     * @return this ModifiedTrigger, so calls can be chained
     */
    public ModifiedTrigger whileTrue(Command command) {
        listOfCommands.add(command.getName());
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
     * Starts the given command when the condition changes to `false` and cancels it when the
     * condition changes to `true`.
     *
     * <p>Doesn't re-start the command if it ends while the condition is still `false`. If the command
     * should restart, see {@link edu.wpi.first.wpilibj2.command.RepeatCommand}.
     *
     * @param command the command to start
     * @return this ModifiedTrigger, so calls can be chained
     */
    public ModifiedTrigger whileFalse(Command command) {
        listOfCommands.add(command.getName());
        requireNonNullParam(command, "command", "whileLow");
        m_loop.bind(
                new Runnable() {
                    private boolean m_pressedLast = m_condition.getAsBoolean();

                    @Override
                    public void run() {
                        boolean pressed = m_condition.getAsBoolean();

                        if (m_pressedLast && !pressed) {
                            command.schedule();
                        } else if (!m_pressedLast && pressed) {
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
    public ModifiedTrigger toggleOnTrue(Command command) {
        listOfCommands.add(command.getName());
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

    /**
     * Toggles a command when the condition changes from `true` to the low state.
     *
     * @param command the command to toggle
     * @return this ModifiedTrigger, so calls can be chained
     */
    public ModifiedTrigger toggleOnFalse(Command command) {
        listOfCommands.add(command.getName());
        requireNonNullParam(command, "command", "toggleOnFalling");
        m_loop.bind(
                new Runnable() {
                    private boolean m_pressedLast = m_condition.getAsBoolean();

                    @Override
                    public void run() {
                        boolean pressed = m_condition.getAsBoolean();

                        if (m_pressedLast && !pressed) {
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

    /**
     * Starts the given command whenever the ModifiedTrigger just becomes active.
     *
     * @param command the command to start
     * @return this ModifiedTrigger, so calls can be chained
     * @deprecated Use {@link #onTrue(Command)} instead.
     */
    @Deprecated
    public ModifiedTrigger whenActive(final Command command) {
        listOfCommands.add(command.getName());
        requireNonNullParam(command, "command", "whenActive");

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

    /**
     * Runs the given runnable whenever the ModifiedTrigger just becomes active.
     *
     * @param toRun the runnable to run
     * @param requirements the required subsystems
     * @return this ModifiedTrigger, so calls can be chained
     * @deprecated Replace with {@link #onTrue(Command)}, creating the InstantCommand manually
     */
    @Deprecated
    public ModifiedTrigger whenActive(final Runnable toRun, Subsystem... requirements) {
        return whenActive(new InstantCommand(toRun, requirements));
    }

    /**
     * Constantly starts the given command while the button is held.
     *
     * <p>{@link Command#schedule()} will be called repeatedly while the ModifiedTrigger is active, and will
     * be canceled when the ModifiedTrigger becomes inactive.
     *
     * @param command the command to start
     * @return this ModifiedTrigger, so calls can be chained
     * @deprecated Use {@link #whileTrue(Command)} with {@link
     *     edu.wpi.first.wpilibj2.command.RepeatCommand RepeatCommand}, or bind {@link
     *     Command#schedule() command::schedule} to {@link BooleanEvent#ifHigh(Runnable)} (passing no
     *     requirements).
     */
    @Deprecated
    public ModifiedTrigger whileActiveContinuous(final Command command) {
        listOfCommands.add(command.getName());
        requireNonNullParam(command, "command", "whileActiveContinuous");

        m_loop.bind(
                new Runnable() {
                    private boolean m_pressedLast = m_condition.getAsBoolean();

                    @Override
                    public void run() {
                        boolean pressed = m_condition.getAsBoolean();

                        if (pressed) {
                            command.schedule();
                        } else if (m_pressedLast) {
                            command.cancel();
                        }

                        m_pressedLast = pressed;
                    }
                });

        return this;
    }

    /**
     * Constantly runs the given runnable while the button is held.
     *
     * @param toRun the runnable to run
     * @param requirements the required subsystems
     * @return this ModifiedTrigger, so calls can be chained
     * @deprecated Use {@link #whileTrue(Command)} and construct a RunCommand manually
     */
    @Deprecated
    public ModifiedTrigger whileActiveContinuous(final Runnable toRun, Subsystem... requirements) {
        return whileActiveContinuous(new InstantCommand(toRun, requirements));
    }

    /**
     * Starts the given command when the ModifiedTrigger initially becomes active, and ends it when it becomes
     * inactive, but does not re-start it in-between.
     *
     * @param command the command to start
     * @return this ModifiedTrigger, so calls can be chained
     * @deprecated Use {@link #whileTrue(Command)} instead.
     */
    @Deprecated
    public ModifiedTrigger whileActiveOnce(final Command command) {
        listOfCommands.add(command.getName());
        requireNonNullParam(command, "command", "whileActiveOnce");

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
     * Starts the command when the ModifiedTrigger becomes inactive.
     *
     * @param command the command to start
     * @return this ModifiedTrigger, so calls can be chained
     * @deprecated Use {@link #onFalse(Command)} instead.
     */
    @Deprecated
    public ModifiedTrigger whenInactive(final Command command) {
        listOfCommands.add(command.getName());
        requireNonNullParam(command, "command", "whenInactive");

        m_loop.bind(
                new Runnable() {
                    private boolean m_pressedLast = m_condition.getAsBoolean();

                    @Override
                    public void run() {
                        boolean pressed = m_condition.getAsBoolean();

                        if (m_pressedLast && !pressed) {
                            command.schedule();
                        }

                        m_pressedLast = pressed;
                    }
                });

        return this;
    }

    /**
     * Runs the given runnable when the ModifiedTrigger becomes inactive.
     *
     * @param toRun the runnable to run
     * @param requirements the required subsystems
     * @return this ModifiedTrigger, so calls can be chained
     * @deprecated Construct the InstantCommand manually and replace with {@link #onFalse(Command)}
     */
    @Deprecated
    public ModifiedTrigger whenInactive(final Runnable toRun, Subsystem... requirements) {
        return whenInactive(new InstantCommand(toRun, requirements));
    }

    /**
     * Toggles a command when the ModifiedTrigger becomes active.
     *
     * @param command the command to toggle
     * @return this ModifiedTrigger, so calls can be chained
     * @deprecated Use {@link #toggleOnTrue(Command)} instead.
     */
    @Deprecated
    public ModifiedTrigger toggleWhenActive(final Command command) {
        listOfCommands.add(command.getName());
        requireNonNullParam(command, "command", "toggleWhenActive");

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

    /**
     * Cancels a command when the ModifiedTrigger becomes active.
     *
     * @param command the command to cancel
     * @return this ModifiedTrigger, so calls can be chained
     * @deprecated Instead, pass this as an end condition to {@link Command#until(BooleanSupplier)}.
     */
    @Deprecated
    public ModifiedTrigger cancelWhenActive(final Command command) {
        listOfCommands.add(command.getName());
        requireNonNullParam(command, "command", "cancelWhenActive");

        m_loop.bind(
                new Runnable() {
                    private boolean m_pressedLast = m_condition.getAsBoolean();

                    @Override
                    public void run() {
                        boolean pressed = m_condition.getAsBoolean();

                        if (!m_pressedLast && pressed) {
                            command.cancel();
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
