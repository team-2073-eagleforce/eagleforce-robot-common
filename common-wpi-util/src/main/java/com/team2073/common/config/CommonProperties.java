package com.team2073.common.config;

import com.team2073.common.datarecorder.model.DataRecordTable.TimestampFormat;
import com.team2073.common.proploader.model.PropertyContainer;

import java.time.format.DateTimeFormatter;

/**
 * @author pbriggs
 */
@PropertyContainer
public class CommonProperties {
    
    public static final Integer DEFAULT_SHORT_INTERVAL = 20;
    public static final Integer DEFAULT_LONG_INTERVAL = 5000;

    // TODO:
    //  -Break out into sub-inner classes (will need to refactor property parsing to handle this)

    // Misc
    private Boolean realMatch = false;

    // PeriodicRunner
    private Boolean periodicRunnerEnabled = true;
    private Boolean periodicRunnerAutoRegister = true;
    private Integer loggingAsyncPeriod = DEFAULT_SHORT_INTERVAL;
    private Integer smartDashboardAsyncPeriod = DEFAULT_SHORT_INTERVAL;
    private Boolean publishToSmartDashboard = true;

    // DataRecorder
    private Boolean dataRecorderEnabled = true;
    private TimestampFormat dataRecorderTimestampFormat = TimestampFormat.MILLISECONDS_SINCE_BOOT;
    private String dataRecorderCustomTimestampFormat = DateTimeFormatter.ISO_LOCAL_DATE_TIME.toString();
    private Boolean dataRecorderAutoRecord = true;
    private Boolean dataRecorderAutoFlush = true;
    private Integer dataRecorderDefaultRecordInterval = DEFAULT_SHORT_INTERVAL;
    private Integer dataRecorderAutoFlushInterval = DEFAULT_LONG_INTERVAL;

    // PropertyLoader
    private Boolean propertyLoaderEnabled = true;
    private Integer propLoaderRefreshPropsInterval = DEFAULT_LONG_INTERVAL;
    
    // RobotRunner
    private Boolean loggingLevelModuleEnabled = true;
    private Boolean diagnosticLoggingModuleEnabled = false;
    private Boolean diagnosticLoggingModuleDataRecordingEnabled = false;

    //OccasionalLoggingRunner
    private Boolean occasionalLoggingRunnerEnabled = true;

    //RobotEventPublisher
    private Boolean robotEventPublisherEnabled = true;

    //SmartDashboardAwareRunner
    private Boolean smartDashboardAwareRunnerEnabled = true;
    
    public Boolean getRealMatch() {
        return realMatch;
    }
    
    public void setRealMatch(Boolean realMatch) {
        this.realMatch = realMatch;
    }

    public Boolean getPeriodicRunnerEnabled() {
        return periodicRunnerEnabled;
    }

    public void setPeriodicRunnerEnabled(Boolean periodicRunnerEnabled) {
        this.periodicRunnerEnabled = periodicRunnerEnabled;
    }

    public Boolean getPeriodicRunnerAutoRegister() {
        return periodicRunnerAutoRegister;
    }
    
    public void setPeriodicRunnerAutoRegister(Boolean periodicRunnerAutoRegister) {
        this.periodicRunnerAutoRegister = periodicRunnerAutoRegister;
    }
    
    public Integer getLoggingAsyncPeriod() {
        return loggingAsyncPeriod;
    }
    
    public void setLoggingAsyncPeriod(Integer loggingAsyncPeriod) {
        this.loggingAsyncPeriod = loggingAsyncPeriod;
    }
    
    public Integer getSmartDashboardAsyncPeriod() {
        return smartDashboardAsyncPeriod;
    }
    
    public void setSmartDashboardAsyncPeriod(Integer smartDashboardAsyncPeriod) {
        this.smartDashboardAsyncPeriod = smartDashboardAsyncPeriod;
    }
    
    public Boolean getPublishToSmartDashboard() {
        return publishToSmartDashboard;
    }
    
    public void setPublishToSmartDashboard(Boolean publishToSmartDashboard) {
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
    
    public Integer getDataRecorderDefaultRecordInterval() {
        return dataRecorderDefaultRecordInterval;
    }
    
    public void setDataRecorderDefaultRecordInterval(Integer dataRecorderDefaultRecordInterval) {
        this.dataRecorderDefaultRecordInterval = dataRecorderDefaultRecordInterval;
    }
    
    public Integer getDataRecorderAutoFlushInterval() {
        return dataRecorderAutoFlushInterval;
    }
    
    public void setDataRecorderAutoFlushInterval(Integer dataRecorderAutoFlushInterval) {
        this.dataRecorderAutoFlushInterval = dataRecorderAutoFlushInterval;
    }

    public Boolean getPropertyLoaderEnabled() {
        return propertyLoaderEnabled;
    }

    public void setPropertyLoaderEnabled(Boolean propertyLoaderEnabled) {
        this.propertyLoaderEnabled = propertyLoaderEnabled;
    }
    
    public Integer getPropLoaderRefreshPropsInterval() {
        return propLoaderRefreshPropsInterval;
    }
    
    public void setPropLoaderRefreshPropsInterval(Integer propLoaderRefreshPropsInterval) {
        this.propLoaderRefreshPropsInterval = propLoaderRefreshPropsInterval;
    }
    
    public Boolean getLoggingLevelModuleEnabled() {
        return loggingLevelModuleEnabled;
    }
    
    public void setLoggingLevelModuleEnabled(Boolean loggingLevelModuleEnabled) {
        this.loggingLevelModuleEnabled = loggingLevelModuleEnabled;
    }
    
    public Boolean getDiagnosticLoggingModuleEnabled() {
        return diagnosticLoggingModuleEnabled;
    }
    
    public void setDiagnosticLoggingModuleEnabled(Boolean diagnosticLoggingModuleEnabled) {
        this.diagnosticLoggingModuleEnabled = diagnosticLoggingModuleEnabled;
    }
    
    public Boolean getDiagnosticLoggingModuleDataRecordingEnabled() {
        return diagnosticLoggingModuleDataRecordingEnabled;
    }
    
    public void setDiagnosticLoggingModuleDataRecordingEnabled(Boolean diagnosticLoggingModuleDataRecordingEnabled) {
        this.diagnosticLoggingModuleDataRecordingEnabled = diagnosticLoggingModuleDataRecordingEnabled;
    }

    public Boolean getOccasionalLoggingRunnerEnabled() {
        return occasionalLoggingRunnerEnabled;
    }

    public void setOccasionalLoggingRunnerEnabled(Boolean occasionalLoggingRunnerEnabled) {
        this.occasionalLoggingRunnerEnabled = occasionalLoggingRunnerEnabled;
    }

    public Boolean getRobotEventPublisherEnabled() {
        return robotEventPublisherEnabled;
    }

    public void setRobotEventPublisherEnabled(Boolean robotEventPublisherEnabled) {
        this.robotEventPublisherEnabled = robotEventPublisherEnabled;
    }

    public Boolean getSmartDashboardAwareRunnerEnabled() {
        return smartDashboardAwareRunnerEnabled;
    }

    public void setSmartDashboardAwareRunnerEnabled(Boolean smartDashboardAwareRunnerEnabled) {
        this.smartDashboardAwareRunnerEnabled = smartDashboardAwareRunnerEnabled;
    }
}
