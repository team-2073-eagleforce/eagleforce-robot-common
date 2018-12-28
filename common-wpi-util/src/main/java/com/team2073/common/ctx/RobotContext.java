package com.team2073.common.ctx;

import com.team2073.common.config.CommonProperties;
import com.team2073.common.config.RobotDirectory;
import com.team2073.common.config.RobotProfiles;
import com.team2073.common.datarecorder.DataRecorder;
import com.team2073.common.event.RobotEventPublisher;
import com.team2073.common.objective.AbstractSubsystemCoordinator;
import com.team2073.common.periodic.OccasionalLoggingRunner;
import com.team2073.common.periodic.PeriodicRunner;
import com.team2073.common.periodic.SmartDashboardAwareRunner;
import com.team2073.common.smartdashboard.adapter.DriverStationAdapter;
import com.team2073.common.smartdashboard.adapter.DriverStationAdapterSimulationImpl;
import com.team2073.common.smartdashboard.adapter.SmartDashboardAdapter;
import com.team2073.common.smartdashboard.adapter.SmartDashboardAdapterSimulationImpl;
import com.team2073.common.util.Ex;
import com.team2073.common.util.Throw;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * Use this class to get instances normally retrieved through static methods
 * (such as {@link DriverStation}, {@link SmartDashboard}, etc.). This way when
 * running from a unit test (off board the rio), you can simply return 'dummy'
 * instances so the tests do not fail with a <b>java.lang.UnsatisfiedLinkError: no ntcore in java.library.path</b>
 *
 * @author pbriggs
 */
public class RobotContext {

    // TODO
    // -Create ScheduleAdapter
    // -Setup AbstractSubsystemCoordinator
    // -In getTestInstance() call .close() on instances to give them a chance to clean up threads/etc.
    // -Figure out the pattern to use for getting the adapters (currently there is no way to set them to test instances)
    //      -Then change DataRecordOutputHandlerSmartDashboardImpl to get the adapter from RobotContext
    // -Create additional setters that set simulation instances (setSmartDashboardToSimulation() )
    //      -We won't need these for non-wpi stuff (we don't have simulation implementations)
    // -Setup custom classloader to handle static reloading during unit tests:
    //      - http://ahlamnote.blogspot.com/2017/07/junit-test-for-singletonsstatic.html

    private Logger log = LoggerFactory.getLogger(getClass());

    private static RobotContext instance;

    // WPI instances
    private DriverStationAdapter driverStation;
    private Scheduler scheduler;
    private SmartDashboardAdapter smartDashboard;

    // Custom instances
    private LocalDateTime bootTimestamp = LocalDateTime.now();
    private PeriodicRunner periodicRunner;
    private OccasionalLoggingRunner loggingRunner;
    private DataRecorder dataRecorder;
    private RobotEventPublisher eventPublisher;
    private SmartDashboardAwareRunner smartDashboardRunner;
    private AbstractSubsystemCoordinator subsystemCoordinator;
    private CommonProperties commonProps = new CommonProperties();
    private RobotDirectory robotDir;
    private RobotProfiles robotProfiles;

    public static RobotContext getInstance() {
        if (instance == null)
            instance = new RobotContext();
        return instance;
    }

    /** Call this inbetween tests if you need to change any of the implementations. */
    public static RobotContext resetTestInstance() {
        // See TODO about custom classloader
        instance = new RobotContext();
        instance.setSmartDashboard(SmartDashboardAdapterSimulationImpl.getInstance());
        instance.setDriverStation(DriverStationAdapterSimulationImpl.getInstance());
        instance.getDataRecorder().requestShutdown();
        instance.setDataRecorder(new DataRecorder());
        return instance;
    }

    public void registerPeriodicInstances() {
        log.info("Registering Periodic instances...");
//        smartDashboardRunner.registerSelf(periodicRunner);
//        loggingRunner.registerSelf(periodicRunner);
        // TODO: Check if we are already registered. Currently, this is blocking implementations from customizing the registration
        dataRecorder.registerWithPeriodicRunner(periodicRunner);
        log.info("Registering Periodic instances complete.");
    }

    public LocalDateTime getBootTimestamp() {
        return bootTimestamp;
    }

    public CommonProperties getCommonProps() {
        if (commonProps == null)
            commonProps = new CommonProperties();

        return commonProps;
    }

    public RobotContext setCommonProps(CommonProperties commonProps) {
        if (this.commonProps != null)
            Throw.illegalState("Cannot set commonProps after they have already been set.");

        this.commonProps = commonProps;
        return this;
    }

    public PeriodicRunner getPeriodicRunner() {
        if (periodicRunner == null)
            periodicRunner = new PeriodicRunner();

        return periodicRunner;
    }

    public RobotContext setPeriodicRunner(PeriodicRunner periodicRunner) {
        this.periodicRunner = periodicRunner;
        return this;
    }

    public OccasionalLoggingRunner getLoggingRunner() {
        if (loggingRunner == null)
            loggingRunner = new OccasionalLoggingRunner();

        return loggingRunner;
    }

    public RobotContext setLoggingRunner(OccasionalLoggingRunner loggingRunner) {
        this.loggingRunner = loggingRunner;
        return this;
    }

    public DataRecorder getDataRecorder() {
        if (dataRecorder == null)
            dataRecorder = new DataRecorder();

        return dataRecorder;
    }

    public RobotContext setDataRecorder(DataRecorder dataRecorder) {
        this.dataRecorder = dataRecorder;
        return this;
    }

    public RobotEventPublisher getEventPublisher() {
        if (eventPublisher == null)
            eventPublisher = new RobotEventPublisher();

        return eventPublisher;
    }

    public RobotContext setEventPublisher(RobotEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        return this;
    }

    public SmartDashboardAwareRunner getSmartDashboardRunner() {
        if (smartDashboardRunner == null)
            smartDashboardRunner = new SmartDashboardAwareRunner();

        return smartDashboardRunner;
    }

    public RobotContext setSmartDashboardRunner(SmartDashboardAwareRunner smartDashboardRunner) {
        this.smartDashboardRunner = smartDashboardRunner;
        return this;
    }

    public DriverStationAdapter getDriverStation() {
        if (driverStation == null)
            driverStation = DriverStationAdapter.getInstance();

        return driverStation;
    }

    public RobotContext setDriverStation(DriverStationAdapter driverStation) {
        this.driverStation = driverStation;
        return this;
    }

    public Scheduler getScheduler() {
        if (scheduler == null)
            scheduler = Scheduler.getInstance();

        return scheduler;
    }

    public RobotContext setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
        return this;
    }

    public SmartDashboardAdapter getSmartDashboard() {
        if (smartDashboard == null)
            smartDashboard = SmartDashboardAdapter.getInstance();

        return smartDashboard;
    }

    public void setSmartDashboard(SmartDashboardAdapter smartDashboard) {
        this.smartDashboard = smartDashboard;
    }
    
    public RobotDirectory getRobotDirectory() {
        if (robotDir == null)
            robotDir = new RobotDirectory();
        
        return robotDir;
    }

    public RobotContext setRobotDirectory(RobotDirectory robotDir) {
        this.robotDir = robotDir;
        return this;
    }
    
    public RobotProfiles getRobotProfiles() {
        if (robotProfiles == null)
            robotProfiles = new RobotProfiles();
        
        return robotProfiles;
    }

    public RobotContext setRobotProfiles(RobotProfiles robotProfiles) {
        if (this.commonProps != null)
            throw Ex.illegalState("Cannot set robotProfiles after they have already been set.");
        
        this.robotProfiles = robotProfiles;
        return this;
    }
}
