package com.team2073.common.speedcontroller;

import com.ctre.phoenix.ErrorCode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.team2073.common.ctx.RobotContext;
import com.team2073.common.periodic.AsyncPeriodicRunnable;
import com.team2073.common.periodic.SmartDashboardAware;
import com.team2073.common.util.EnumUtil;
import com.team2073.common.util.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.team2073.common.util.ClassUtil.*;

public class EagleSRX extends TalonSRX implements AsyncPeriodicRunnable, SmartDashboardAware {


    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final RobotContext robotContext = RobotContext.getInstance();
    private String name;
    private boolean isTestingEnabled = false;
    private double safePercentage = .5;
	private Timer timer = new Timer();

    private ControlMode nextControlMode = ControlMode.PercentOutput;
    private double nextOutputValue = 0;

    private double lastPercentOutForward = 0;
    private double lastPercentOutReverse = 0;
    private double lastkPSlot0 = 0;
    private double lastkISlot0 = 0;
    private double lastkDSlot0 = 0;
    private double lastkFSlot0 = 0;

    private double lastkPSlot1 = 0;
    private double lastkISlot1 = 0;
    private double lastkDSlot1 = 0;
    private double lastkFSlot1 = 0;
    private int currentPidIdx = 0;
    private boolean enabledTestingMode = false;

    public enum DataType {
        POSITION, VELOCITY, VOLTAGE, CURRENT;
    }

    private LoadingCache<DataType, Double> talonDataCache;
    private Timer outputTimer = new Timer();

    /**
     *
     * @param deviceNumber
     * @param name
     * @param safePercentage
     *            safePercentage is the percent of the otherwise set speed, from 0
     *            to 1
     */
    public EagleSRX(int deviceNumber, String name, double safePercentage) {
        super(deviceNumber);
        this.name = name;
        this.safePercentage = safePercentage;
        talonDataCache = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.MILLISECONDS).maximumSize(100)
                .build(new CacheLoader<DataType, Double>() {
                    public Double load(DataType key) throws Exception {
                        switch (key) {
                            case POSITION:
                                return (double) getSelectedSensorPositionInternal(currentPidIdx);
                            case VOLTAGE:
                                return getMotorOutputVoltageInternal();
                            case CURRENT:
                                return getOutputCurrentInternal();
                            case VELOCITY:
                                return (double) getSelectedSensorVelocityInternal(currentPidIdx);
                            default:
                                EnumUtil.throwUnknownValueException(key);
                                // dead code
                                return null;
                        }

                    }
                });
        autoRegisterWithPeriodicRunner(simpleName(this) + "[" + deviceNumber + "]");
        robotContext.getSmartDashboardRunner().registerInstance(this);

        logger.info("[{}] Talon has been initialized on port [{}].", name, deviceNumber);
    }

    @Override
    public void updateSmartDashboard() {
        if (logger.isTraceEnabled()) {
            SmartDashboard.putNumber(name, this.getMotorOutputVoltage());
        }
    }

    @Override
    public void readSmartDashboard() {
        isTestingEnabled = SmartDashboard.setDefaultBoolean(name + " Enable Test Mode", false);
        if (isTestingEnabled && (timer.hasWaited(1000) || !enabledTestingMode)) {
            timer.start();
            enabledTestingMode = true;
            reloadPIDFValues();
        }
    }

    /**
     * use requestSelectedSensorPosition instead
     */
    private int getSelectedSensorPositionInternal(int pidIdx) {
        return super.getSelectedSensorPosition(pidIdx);
    }

    private double getMotorOutputVoltageInternal() {
        return super.getMotorOutputVoltage();
    }

    private double getOutputCurrentInternal() {
        return super.getOutputCurrent();
    }

    private int getSelectedSensorVelocityInternal(int pidIdx) {
        return super.getSelectedSensorVelocity(pidIdx);
    }

    @Override
    public int getSelectedSensorPosition(int pidIdx) {
        this.currentPidIdx = pidIdx;
        try {
            return (int) Math.round(talonDataCache.get(DataType.POSITION));
        } catch (ExecutionException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public int getSelectedSensorVelocity(int pidIdx) {
        this.currentPidIdx = pidIdx;
        try {
            return (int) Math.round(talonDataCache.get(DataType.VELOCITY));
        } catch (ExecutionException e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public double getMotorOutputVoltage() {
        try {
            return (int) Math.round(talonDataCache.get(DataType.VOLTAGE));
        } catch (ExecutionException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public double getOutputCurrent() {
        try {
            return (int) Math.round(talonDataCache.get(DataType.CURRENT));
        } catch (ExecutionException e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public ErrorCode config_kD(int slotIdx, double value, int timeoutMs) {
        if (slotIdx == 1)
            lastkDSlot1 = value;
        else
            lastkDSlot0 = value;
        if (isTestingEnabled)
            return super.config_kD(slotIdx, value * safePercentage, timeoutMs);
        else
            return super.config_kD(slotIdx, value, timeoutMs);
    }

    @Override
    public ErrorCode config_kI(int slotIdx, double value, int timeoutMs) {
        if (slotIdx == 1)
            lastkISlot1 = value;
        else
            lastkISlot0 = value;
        if (isTestingEnabled)
            return super.config_kI(slotIdx, value * safePercentage, timeoutMs);
        else
            return super.config_kI(slotIdx, value, timeoutMs);
    }

    @Override
    public ErrorCode config_kP(int slotIdx, double value, int timeoutMs) {
        if (slotIdx == 1)
            lastkPSlot1 = value;
        else
            lastkPSlot0 = value;
        if (isTestingEnabled)
            return super.config_kP(slotIdx, value * safePercentage, timeoutMs);
        else
            return super.config_kP(slotIdx, value, timeoutMs);
    }

    @Override
    public ErrorCode config_kF(int slotIdx, double value, int timeoutMs) {
        if (slotIdx == 1)
            lastkFSlot1 = value;
        else
            lastkFSlot0 = value;
        if (isTestingEnabled)
            return super.config_kF(slotIdx, value * safePercentage, timeoutMs);
        else
            return super.config_kF(slotIdx, value, timeoutMs);
    }

    @Override
    public ErrorCode configPeakOutputForward(double percentOut, int timeoutMs) {
        lastPercentOutForward = percentOut;
        if (isTestingEnabled)
            return super.configPeakOutputForward(percentOut * safePercentage, timeoutMs);
        else
            return super.configPeakOutputForward(percentOut, timeoutMs);
    }

    @Override
    public ErrorCode configPeakOutputReverse(double percentOut, int timeoutMs) {
        lastPercentOutReverse = percentOut;
        if (isTestingEnabled)
            return super.configPeakOutputReverse(percentOut * safePercentage, timeoutMs);
        else
            return super.configPeakOutputReverse(percentOut, timeoutMs);
    }

    public void reloadPIDFValues() {
        this.config_kP(1, lastkPSlot1, 0);
        this.config_kI(1, lastkISlot1, 0);
        this.config_kD(1, lastkDSlot1, 0);
        this.config_kF(1, lastkFSlot1, 0);
        this.config_kP(0, lastkPSlot0, 0);
        this.config_kI(0, lastkISlot0, 0);
        this.config_kD(0, lastkDSlot0, 0);
        this.config_kF(0, lastkFSlot0, 0);
        this.configPeakOutputForward(lastPercentOutForward, 0);
        this.configPeakOutputReverse(lastPercentOutReverse, 0);
    }

    @Override
    public void set(ControlMode mode, double outputValue) {
        this.nextControlMode = mode;
        this.nextOutputValue = outputValue;
    }

    @Override
    public void onPeriodicAsync() {
        super.set(getNextControlMode(), getNextOutputValue());
    }

    protected ControlMode getNextControlMode() {
        return nextControlMode;
    }

    protected double getNextOutputValue() {
        return nextOutputValue;
    }

}
