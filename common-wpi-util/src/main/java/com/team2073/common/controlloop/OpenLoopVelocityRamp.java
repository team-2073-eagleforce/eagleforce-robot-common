package com.team2073.common.controlloop;

import com.team2073.common.util.ConversionUtil;

public class OpenLoopVelocityRamp {

   private double maxOutput;
   private double secondsToMaxOutput;
   private double outputPerSecond;
   private double startTime;
   private boolean started;

   public OpenLoopVelocityRamp(double maxOutput, double secondsToMaxOutput){
        this.maxOutput = maxOutput;
        this.secondsToMaxOutput = secondsToMaxOutput;

        outputPerSecond = maxOutput/secondsToMaxOutput;
    }

    public double output(){

       if(!started){
            startTime = ConversionUtil.msToSeconds((int) System.currentTimeMillis());
            started = true;
        }

        return outputPerSecond*(ConversionUtil.msToSeconds((int) System.currentTimeMillis())- startTime);
    }

    public void reset(){
       started = false;
    }
}
