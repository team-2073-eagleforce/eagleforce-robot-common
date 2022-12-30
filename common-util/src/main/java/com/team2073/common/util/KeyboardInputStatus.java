package com.team2073.common.util;

import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import lombok.Setter;

import java.util.*;
import java.util.function.Predicate;

@Setter
public class KeyboardInputStatus {
    private static Map<String, SendableStatus> groups = new HashMap<>();

    private double activeStartingTime = 0.0;
    private boolean active = false;

    private String text;

    public void set(boolean active) {
        if (active && !this.active)
            activeStartingTime = Timer.getFPGATimestamp();
        this.active = active;
    }

    public KeyboardInputStatus(String text) {
        this(text, "Status");
    }

    public KeyboardInputStatus(String text, String group) {
        if (!groups.containsKey(group)) {
            groups.put(group, new SendableStatus());
            SmartDashboard.putData(group, groups.get(group));
        }

        this.text = text;
        groups.get(group).statuses.add(this);
    }

    private static class SendableStatus implements Sendable {
        public final List<KeyboardInputStatus> statuses = new ArrayList<>();

        public String[] getStrings() {
            Predicate<KeyboardInputStatus> activeFilter = (KeyboardInputStatus x) -> x.active;
            Comparator<KeyboardInputStatus> timeSorter = (KeyboardInputStatus s1, KeyboardInputStatus s2) ->
                    (int) (s2.activeStartingTime - s1.activeStartingTime);
            return statuses.stream().filter(activeFilter).sorted(timeSorter).map((KeyboardInputStatus s) -> s.text).toArray(String[]::new);
        }

        @Override
        public void initSendable(SendableBuilder builder) {
            builder.setSmartDashboardType("Status");
            builder.addStringArrayProperty("statuses", () -> getStrings(), null);
        }
    }
}
