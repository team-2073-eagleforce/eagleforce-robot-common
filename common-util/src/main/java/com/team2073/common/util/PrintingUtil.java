package com.team2073.common.util;
import java.util.ArrayList;

public class PrintingUtil {
    private static boolean isOn = false;
    private static int loopCounts = 0;
    private static int j;
    private static ArrayList<String> messages = new ArrayList<>();

    public static void runPrint() {
        if(isOn) {
            if(j > loopCounts) {
                for(int i = 0; i <= messages.size(); i++) {
                    System.out.println(messages.get(i));
                }
                j = 0;
            }
            j++;
        }
    }

    public static void setCountInterval(int interval) {
        loopCounts = interval;
    }
    public static void addMessage(String... message) {
        for(int i = 0; i < message.length; i++) {
            messages.add(message[i]);
        }
    }
    public static void removeMessage(String... message) {
        for(int i = 0; i < message.length; i++) {
            messages.remove(message[i]);
        }
    }
    public static void enablePrinting(boolean state) {
        isOn = state;
    }
}