package com.team2073.common.proploader;

import com.google.common.annotations.VisibleForTesting;
import com.team2073.common.assertion.Assert;
import com.team2073.common.ctx.RobotContext;
import com.team2073.common.periodic.AsyncPeriodicRunnable;
import com.team2073.common.periodic.PeriodicRunner;
import com.team2073.common.proploader.model.PropertyContainer;
import com.team2073.common.proploader.model.PropertyContainerWrapper;
import com.team2073.common.proploader.model.PropertyFileAccessor;
import com.team2073.common.proploader.model.PropertyMapping;
import com.team2073.common.robot.RobotRunner;
import com.team2073.common.util.Ex;
import com.team2073.common.util.FileUtil;
import org.apache.commons.io.FileUtils;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.team2073.common.proploader.PropertyLoader.PropertyLoaderState.*;
import static com.team2073.common.proploader.PropertyLoader.RunMode.*;
import static com.team2073.common.util.ClassUtil.*;

/**
 * <h1>See documentation for full details</h1>
 *
 * <p>Copied directly from documentation:</p>
 * <p>
 * The Property Loader module is used to parse properties from .properties files into Java objects. This allows you to
 * quickly make changes in a properties file that your code can then use to behave differently. For instance you could
 * extract your PID values to a .properties file and then use these property values in the code to set PID values. This
 * would allow you to have different PID values for different robots running the same code (maybe your practice bot
 * needs different PID values than your comp bot due to differences in mechanical resistance, etc.). Another example
 * would be turning certain logging or performance monitoring off when in a competition to free up resources/CPU.
 * </p>
 *
 * @author Jason Stanley
 * @author Preston Briggs
 */
public class PropertyLoader implements AsyncPeriodicRunnable {
    
    /**
     * The default path to look for .properties files in. This is relative to the classpath, which in a standard gradle
     * project is the "src/main/resources" directory.
     *
     * @see #setSourceCodePropertiesDirPath(String)
     */
    public static final String DEFAULT_CONF_DIR_NAME = "conf";
    
    enum PropertyLoaderState {
        NO_PROPERTY_CONTAINERS_REGISTERED, INITIALIZING, RUNNING, FAILED;
    }
    
    enum RunMode {
        OFF, MANUAL, PERIODIC_RUNNER;
    }

    private final Logger log = LoggerFactory.getLogger(getClass());
    
    private final RobotContext robotContext = RobotContext.getInstance();

    // State
    private PropertyLoaderState state = NO_PROPERTY_CONTAINERS_REGISTERED;
    private RunMode runMode = OFF;
    private boolean loggedSwitchingToManualMode;

    // Customizable config
    private String sourceCodePropDirPath = DEFAULT_CONF_DIR_NAME;
    private File externalPropDir = robotContext.getRobotDirectory().getConfDir();
    
    // Other
    private final List<File> propDirList = new ArrayList<>();
    private final Map<Class<?>, PropertyContainerWrapper> containerWrapperMap = new HashMap<>();
    
    public PropertyLoader() {
        Integer interval = robotContext.getCommonProps().getPropLoaderRefreshPropsInterval();
        robotContext.getPeriodicRunner().autoRegisterAsync(this, interval);
    }
    
    /**
     * An alternative to {@link #autoRegisterAllPropContainers(String)}. Pass in a class that exists in the package
     * (or parent-package) the {@literal @}{@link PropertyContainer} annotated classes reside in.
     *
     * @param rootPackageClass The class to resolve the packge from
     * @return this for chaining
     *
     * @see #autoRegisterAllPropContainers(String)
     */
    public List<? extends Object> autoRegisterAllPropContainers(Class<?> rootPackageClass) {
        Assert.assertNotNull(rootPackageClass, "rootPackageClass");
        return autoRegisterAllPropContainers(rootPackageClass.getPackage().getName());
    }
    
    /**
     * Automatically register all classes annotated with {@literal @}{@link PropertyContainer} in the given package.
     * This is an alternative to registering all PropertyContainer classes manually using
     * {@link #registerPropContainer(Class)}.
     *
     * @param rootPackage The package to load {@literal @}{@link PropertyContainer}s from
     * @return this for chaining
     */
    public List<? extends Object> autoRegisterAllPropContainers(String rootPackage) {
        Assert.assertNotNull(rootPackage, "rootPackage");
        List<Object> registered = new ArrayList<>();
        Reflections reflection = new Reflections(rootPackage);
        Set<Class<?>> subTypesOf = reflection.getTypesAnnotatedWith(PropertyContainer.class);
        for (Class<?> clazz : subTypesOf) {
            registered.add(registerPropContainer(clazz));
        }
        return registered;
    }
    
    /**
     * Manually register a PropertyContainer. To automatically register all PropertyContainers see
     * {@link #autoRegisterAllPropContainers(String)}.
     *
     * @param propContainerClass The PropertyContainer to register
     * @param <R> The PropertyContainer
     * @param <T> The class of the PropertyContainer
     * @return The instantiated and registered PropertyContainer
     */
    public <R, T extends Class<R>> R registerPropContainer(T propContainerClass) {
        Assert.assertNotNull(propContainerClass, "propContainerClass");

        init(propContainerClass);

        PropertyContainerWrapper propWrapper = containerWrapperMap.get(propContainerClass);

        if (propWrapper != null) {
            log.debug("[{}] class [{}] already registered.", simpleName(this), simpleName(propContainerClass));
            return (R) propWrapper.getPropContainer();
        }
    
        if (state == RUNNING || state == FAILED) {
            throw Ex.illegalState("Cannot register property containers after [{}] has already been ran.",
                    simpleName(PropertyLoader.class));
        }

        log.debug("Creating property container from class [{}].", propContainerClass.getName());

        // Resolve all the PropertyContainer data
        Set<String> profileList = robotContext.getRobotProfiles().getProfileList();
        Object propContainer = PropertyLoaderHelper.constructPropertyContainer(propContainerClass);
        List<PropertyMapping> mappingList = PropertyLoaderHelper.createMappings(propContainer);
        String propContainerName = PropertyLoaderHelper.resolvePropertyContainerName(propContainer);
        List<PropertyFileAccessor> propFileList = PropertyLoaderHelper.findPropertyFiles(propContainerClass, propContainerName, propDirList, profileList);

        // Create the PropertyContainer
        PropertyContainerWrapper wrapper = new PropertyContainerWrapper(propContainer, propContainerClass, propContainerName, mappingList, propFileList);

        containerWrapperMap.put(propContainerClass, wrapper);

        log.trace("Creating property container from class [{}] complete.", propContainerName);

        return (R) propContainer;
    }

    private void init(Class<?> propContainerClass) {
        Assert.assertNotNull(propContainerClass, "propContainerClass");
        
        if (state != NO_PROPERTY_CONTAINERS_REGISTERED)
            // Only run this method once
            return;

        state = INITIALIZING;
        initializePropertyDirs(propContainerClass);
    }
    
    /**
     * Determine the classpath (source code) and external property directories. The classpath properties directory
     * will be copied to a temp directory so we can access the files (can't access directly from within a JAR) and the
     * external directory will be created if it doesn't exist.
     *
     * @param propContainerClass The class to use to get a handle on the classpath. This should be one of the
     *                           PropertyContainer classes that was registered
     */
    private void initializePropertyDirs(Class<?> propContainerClass) {
        Assert.assertNotNull(propContainerClass, "propContainerClass");

        // Prop dir from source code
        Optional<File> sourceCodePropDir = PropertyLoaderHelper.createAndResolveSourceCodePropDir(sourceCodePropDirPath, propContainerClass);
        if (sourceCodePropDir.isPresent())
            propDirList.add(sourceCodePropDir.get());

        // Rio onboard prop dir
        Optional<File> rioOnBoardPropDir = PropertyLoaderHelper.createExternalPropDir(externalPropDir);
        if (rioOnBoardPropDir.isPresent())
            propDirList.add(rioOnBoardPropDir.get());
    }
    
    /**
     * <h1>DO NOT USE</h1>
     *
     * This method is only meant to be called by the {@link PeriodicRunner}.
     */
    @Override
    public void onPeriodicAsync() {
        if (runMode == MANUAL) {
            if (!loggedSwitchingToManualMode) {
                log.info("[{}] invoked manually. Ignoring all future [{}] invocations.", simpleName(this), simpleName(PeriodicRunner.class));
                loggedSwitchingToManualMode = true;
            }
    
            // We have already been called manually, ignore PeriodicRunner requests
            return;
        }
        
        runMode = PERIODIC_RUNNER;
        loadProperties();
    }
    
    /**
     * <h1>WARNING: This method is blocking!</h1>
     * <p><i>Do not call this from anywhere but <b>robotInit()</b> unless you know what you are
     * doing! Doing so will result in severe delays in the robot causing erratic and dangerous behavior!</i></p>
     *
     * <p></p>
     *
     * <p>After all the Property Containers have been registered, you can load the properties using this method.
     * This will parse all the files and set the values on the Property Container instances that were registered
     * using {@link #registerPropContainer(Class)} or {@link #autoRegisterAllPropContainers(String)}.</p>
     *
     * <p></p>
     *
     * <p>If you only require loading the properties one time and don’t want to automatically pick up changes to
     * .properties files then simply call this in robotInit() after you have registered all Property Containers.</p>
     *
     * <p></p>
     *
     * <p>Since any file IO is time consuming (comparatively speaking), if properties are to be loaded repeatedly,
     * this must be done on a separate thread than the main robot thread. It is strongly recommended to use the
     * {@link RobotRunner}. This will automatically run the {@link PropertyLoader} (reload the properties)
     * on an async periodic loop every 5 seconds.</p>
     *
     */
    public void loadProperties() {
    
        if (runMode != PERIODIC_RUNNER)
            runMode = MANUAL;

        if (state == FAILED)
            return;


        if (state == NO_PROPERTY_CONTAINERS_REGISTERED)
            return;


        state = RUNNING;

        for (PropertyContainerWrapper wrapper : containerWrapperMap.values()) {
            PropertyLoaderHelper.injectProperties(wrapper);
        }
    }
    
    /** @return The list of Property Containers that were registered */
    public Collection<? extends Object> getPropertyContainerList() {
        return getContainerWrapperList().stream().map(it -> it.getPropContainer()).collect(Collectors.toList());
    }
    
    /**
     * Get the Property Container of a specific class if it was registered. This will not automatically register the
     * Property Container if it doesn't exist like {@link #registerPropContainer(Class)} will.
     *
     * @param propContainerClass The type of Property Container to get
     * @return The Property Container if it was registered
     */
    public <T> Optional<T> getPropertyContainer(Class<? extends T> propContainerClass) {
        Assert.assertNotNull(propContainerClass, "propContainerClass");
        PropertyContainerWrapper wrapper = containerWrapperMap.get(propContainerClass);
        if (wrapper != null) {
            return Optional.of((T) wrapper.getPropContainer());
        } else {
            return Optional.empty();
        }
    }
    
    // Property dir config
    // ==================================================
    
    /**
     * @return The path relative to the classpath that properties files will be loaded from
     * @see #setSourceCodePropertiesDirPath(String)
     */
    public String getSourceCodePropDirPath() {
        return sourceCodePropDirPath;
    }
    
    /**
     * Set the path to the classpath directory to load properties files from (defaults to
     * {@link #DEFAULT_CONF_DIR_NAME}). <br/>
     * This path is relative to the resource directory which in a default gradle project is src/main/resources. <br/>
     * <br/>
     * Assuming this is a default gradle project: <br/>
     * "/foo/bar" results in src/main/resources/foo/bar"
     *
     * @param propDir the path to the classpath directory to load properties files from
     * @return this for chaining
     */
    public PropertyLoader setSourceCodePropertiesDirPath(String propDir) {
        Assert.assertNotNull(propDir, "propDir");
        this.sourceCodePropDirPath = FileUtil.normalize(propDir);
        return this;
    }
    
    /**
     * @return The external (onboard RIO) directory that properties files will be loaded from
     * @see #setExternalPropertiesDirPath(String)
     */
    public File getExternalPropDir() {
        return externalPropDir;
    }
    
    /**
     * Set the external (onboard RIO) directory path to load properties files from (defaults to
     * {@link #DEFAULT_CONF_DIR_NAME}). <br/>
     * If propDir starts with a forwardslash (/) the path will be considered absolute and used as is. If not,
     * the path will be created relative to the user directory.<br/>
     * <br/>
     * "/foo/bar" results in "/foo/bar"<br/>
     * TODO: Verify the below is correct when running on the RIO<br/>
     * "foo/bar" results in "/home/lvuser/foo/bar" or "C:/Users/YourUsernameHere/foo/bar" (depending on operating
     * system/user)
     *
     * @param propDir the external (onboard RIO) directory to load properties files from
     * @return this for chaining
     */
    public PropertyLoader setExternalPropertiesDirPath(String propDir) {
        Assert.assertNotNull(propDir, "propDir");
    
        propDir = propDir.replace("\\", "/");
        
        if (propDir.startsWith("/") || propDir.startsWith("~")) {
            return setExternalPropertiesDir(new File(FileUtil.normalize(propDir)));
        } else {
            return setExternalPropertiesDir(new File(FileUtils.getUserDirectory(), propDir));
        }
    }
    
    /**
     * Set the external (onboard RIO) directory to load properties files from  (defaults to
     * {@link #DEFAULT_CONF_DIR_NAME}).
     *
     * @param propDir the external (onboard RIO) directory to load properties files from
     * @return this for chaining
     */
    public PropertyLoader setExternalPropertiesDir(File propDir) {
        Assert.assertNotNull(propDir, "propDir");
        this.externalPropDir = propDir;
        return this;
    }
    
    // ===============================================================================================================
    // Test methods
    // ===============================================================================================================
    
    @VisibleForTesting
    Collection<PropertyContainerWrapper> getContainerWrapperList() {
        return Collections.unmodifiableCollection(containerWrapperMap.values());
    }
    
}


