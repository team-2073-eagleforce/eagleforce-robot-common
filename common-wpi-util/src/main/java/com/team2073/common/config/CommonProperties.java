package com.team2073.common.config;

import com.team2073.common.datarecorder.model.DataRecordTable.TimestampFormat;

import java.time.format.DateTimeFormatter;

/**
 * @author pbriggs
 */
@PropertyContainer
public class CommonProperties {

    // TODO:
    //  -Break out into sub-inner classes (will need to refactor property parsing to handle this)

    // Misc
    private Boolean realMatch = false;

    // PeriodicRunner
    private Boolean periodicRunnerAutoRegister = true;
    private int loggingAsyncPeriod = 20;
    private int smartDashboardAsyncPeriod = 20;
    private boolean publishToSmartDashboard = true;

    // DataRecorder
    private Boolean dataRecorderEnabled = true;
    private TimestampFormat dataRecorderTimestampFormat = TimestampFormat.MILLISECONDS_SINCE_BOOT;
    private String dataRecorderCustomTimestampFormat = DateTimeFormatter.ISO_LOCAL_DATE_TIME.toString();
    private Boolean dataRecorderAutoRecord = true;
    private Boolean dataRecorderAutoFlush = true;
    private Long dataRecorderDefaultRecordInterval = 20L;
    private Long dataRecorderAutoFlushInterval = 5000L;

    public Boolean getRealMatch() {
        return realMatch;
    }

    public void setRealMatch(Boolean realMatch) {
        this.realMatch = realMatch;
    }

    public Boolean getPeriodicRunnerAutoRegister() {
        return periodicRunnerAutoRegister;
    }

    public void setPeriodicRunnerAutoRegister(Boolean periodicRunnerAutoRegister) {
        this.periodicRunnerAutoRegister = periodicRunnerAutoRegister;
    }

    public int getLoggingAsyncPeriod() {
        return loggingAsyncPeriod;
    }

    public void setLoggingAsyncPeriod(int loggingAsyncPeriod) {
        this.loggingAsyncPeriod = loggingAsyncPeriod;
    }

    public int getSmartDashboardAsyncPeriod() {
        return smartDashboardAsyncPeriod;
    }

    public void setSmartDashboardAsyncPeriod(int smartDashboardAsyncPeriod) {
        this.smartDashboardAsyncPeriod = smartDashboardAsyncPeriod;
    }

    public boolean isPublishToSmartDashboard() {
        return publishToSmartDashboard;
    }

    public void setPublishToSmartDashboard(boolean publishToSmartDashboard) {
        this.publishToSmartDashboard = publishToSmartDashboard;
    }

    public Boolean getDataRecorderEnabled() {
        return dataRecorderEnabled;
    }

    public void setDataRecorderEnabled(Boolean dataRecorderEnabled) {
        this.dataRecorderEnabled = dataRecorderEnabled;
    }

    public TimestampFormat getDataRecorderTimestampFormat() {
        return dataRecorderTimestampFormat;
    }

    public void setDataRecorderTimestampFormat(TimestampFormat dataRecorderTimestampFormat) {
        this.dataRecorderTimestampFormat = dataRecorderTimestampFormat;
    }

    public String getDataRecorderCustomTimestampFormat() {
        return dataRecorderCustomTimestampFormat;
    }

    public void setDataRecorderCustomTimestampFormat(String dataRecorderCustomTimestampFormat) {
        this.dataRecorderCustomTimestampFormat = dataRecorderCustomTimestampFormat;
    }

    public Boolean getDataRecorderAutoRecord() {
        return dataRecorderAutoRecord;
    }

    public void setDataRecorderAutoRecord(Boolean dataRecorderAutoRecord) {
        this.dataRecorderAutoRecord = dataRecorderAutoRecord;
    }

    public Boolean getDataRecorderAutoFlush() {
        return dataRecorderAutoFlush;
    }

    public void setDataRecorderAutoFlush(Boolean dataRecorderAutoFlush) {
        this.dataRecorderAutoFlush = dataRecorderAutoFlush;
    }

    public Long getDataRecorderDefaultRecordInterval() {
        return dataRecorderDefaultRecordInterval;
    }

    public void setDataRecorderDefaultRecordInterval(Long dataRecorderDefaultRecordInterval) {
        this.dataRecorderDefaultRecordInterval = dataRecorderDefaultRecordInterval;
    }

    public Long getDataRecorderAutoFlushInterval() {
        return dataRecorderAutoFlushInterval;
    }

    public void setDataRecorderAutoFlushInterval(Long dataRecorderAutoFlushInterval) {
        this.dataRecorderAutoFlushInterval = dataRecorderAutoFlushInterval;
    }
}
