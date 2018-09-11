package com.team2073.common.datarecorder;

import java.util.Random;

/**
 * @author pbriggs
 */
public abstract class DataRecorderTestFixtures {

    public static class BasicRecordable implements Recordable {

        private String state = "INITIALIZING";
        private int iterations = 0;
        private double position = 2.34;
//        private long longVar = 47;
        private boolean active = true;

        @Override
        public void onBeforeRecord() {
            iterations++;
            position += (1 * new Random().nextDouble());
//            longVar--;
            active = new Random().nextBoolean();
            if (iterations < 2)
                state = "INITIALIZING";
            else if (iterations < 5)
                state = "WAITING";
            else if (iterations < 15)
                state = "MOVING";
            else if (iterations < 20)
                state = "WAITING";
            else if (iterations < 28)
                state = "MOVING";
            else
                state = "WAITING";
        }
    }

    public static class BasicRecordable2 implements Recordable {

        private String stringVar = "22 val";
        private int intVar = 2;
        private double doubleVar = 2.22;
        private long longVar = 222;
        private boolean booleanVar = true;

        @Override
        public void onBeforeRecord() {

        }
    }

}
