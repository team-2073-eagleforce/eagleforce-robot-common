package com.team2073.common.proploader;

import com.google.common.annotations.VisibleForTesting;
import com.team2073.common.assertion.Assert;
import com.team2073.common.ctx.RobotContext;
import com.team2073.common.proploader.model.PropertyContainerWrapper;
import com.team2073.common.proploader.model.PropertyFileAccessor;
import com.team2073.common.proploader.model.PropertyMapping;
import com.team2073.common.util.Ex;
import com.team2073.common.util.FileUtil;
import org.apache.commons.io.FileUtils;
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

import static com.team2073.common.proploader.PropertyLoader.PropertyLoaderState.*;
import static com.team2073.common.util.ClassUtil.*;

/**
 * @author Jason Stanley
 * @author Preston Briggs
 */
public class PropertyLoader {

    private static final String DEFAULT_CONF_DIR_NAME = "conf";

    public static enum PropertyLoaderState {
        NEW, INITIALIZING, RUNNING, FAILED;
    }

    private Logger log = LoggerFactory.getLogger(getClass());

    // State
    private PropertyLoaderState state = NEW;

    // Customizable config
    private String sourceCodeConfDirPath = DEFAULT_CONF_DIR_NAME;
    private File externalConfDir = RobotContext.getInstance().getRobotDirectory().getConfDir();

    // Other
    private final List<File> propDirList = new ArrayList<>();
    private final Map<Class<?>, PropertyContainerWrapper> containerWrapperMap = new HashMap<>();

    public <R, T extends Class<R>> R registerPropContainer(T propContainerClass) {

        Assert.assertNotNull(propContainerClass, "propContainerClass");

        if (state == RUNNING || state == FAILED) {
            throw Ex.illegalState("Cannot register property containers after [{}] has already been ran.",
                    simpleName(PropertyLoader.class));
        }

        init(propContainerClass);

        PropertyContainerWrapper propWrapper = containerWrapperMap.get(propContainerClass);

        if (propWrapper != null)
            return (R) propWrapper.getPropContainer();

        log.debug("Creating property container from class [{}].", propContainerClass.getName());

        // Resolve all the PropertyContainer data
        Set<String> profileList = RobotContext.getInstance().getRobotProfiles().getProfileList();
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
        if (state != NEW)
            return;

        state = INITIALIZING;
        initializePropertyDirs(propContainerClass);
    }

    private void initializePropertyDirs(Class<?> propContainerClass) {

        // Prop dir from source code
        Optional<File> sourceCodePropDir = PropertyLoaderHelper.createAndResolveSourceCodePropDir(sourceCodeConfDirPath, propContainerClass);
        if (sourceCodePropDir.isPresent())
            propDirList.add(sourceCodePropDir.get());

        // Rio onboard prop dir
        Optional<File> rioOnBoardPropDir = PropertyLoaderHelper.createExternalPropDir(externalConfDir);
        if (rioOnBoardPropDir.isPresent())
            propDirList.add(rioOnBoardPropDir.get());
    }
    
    public void loadProperties() {

        if (state == FAILED)
            return;


        if (state == NEW)
            return;


        state = RUNNING;

        for (PropertyContainerWrapper wrapper : containerWrapperMap.values()) {
            PropertyLoaderHelper.injectProperties(wrapper);
        }
    }
    
    // Conf dir config
    // ==================================================
    
    /**
     * Set the path to the classpath directory to load properties files from (defaults to {@link #DEFAULT_CONF_DIR_NAME}).
     * This is relative to the resource directory which is generally src/main/resources. <br/>
     * <br/>
     * "/foo/bar" results in src/main/resources/foo/bar"
     *
     * @param confDir the path to the classpath directory to load properties files from
     * @return this for chaining
     */
    public PropertyLoader setSourceCodeConfDirPath(String confDir) {
        Assert.assertNotNull(confDir, "confDir");
        this.sourceCodeConfDirPath = FileUtil.normalize(confDir);
        return this;
    }
    
    /**
     * Set the external (onboard RIO) directory to load properties files from (defaults to {@link #DEFAULT_CONF_DIR_NAME}).
     * If confDir starts with a forward or backslash the path will be considered absolute and used as is. If not, the path will
     * be created relative to the user directory.<br/>
     * <br/>
     * "/foo/bar" results in "/foo/bar"<br/>
     * TODO: Verify the below is correct when running on the RIO<br/>
     * "foo/bar" results in "/home/lvuser/foo/bar" or "C:/Users/YourUsernameHere/foo/bar" (depending on operating system/user)
     *
     * @param confDir the external (onboard RIO) directory to load properties files from
     * @return this for chaining
     */
    public PropertyLoader setExternalConfDirPath(String confDir) {
        Assert.assertNotNull(confDir, "confDir");
    
        confDir = confDir.replace("\\", "/");
        
        if (confDir.startsWith("/") || confDir.startsWith("~")) {
            return setExternalConfDir(new File(FileUtil.normalize(confDir)));
        } else {
            return setExternalConfDir(new File(FileUtils.getUserDirectory(), confDir));
        }
    }
    
    /**
     * Set the external (onboard RIO) directory to load properties files from  (defaults to {@link #DEFAULT_CONF_DIR_NAME}).
     *
     * @param confDir the external (onboard RIO) directory to load properties files from
     * @return this for chaining
     */
    public PropertyLoader setExternalConfDir(File confDir) {
        this.externalConfDir = confDir;
        return this;
    }
    
    // ===============================================================================================================
    // Test methods
    // ===============================================================================================================
    
    @VisibleForTesting
    Collection<PropertyContainerWrapper> getContainerWrapperList() {
        return Collections.unmodifiableCollection(containerWrapperMap.values());
    }
    
    @VisibleForTesting
    String getSourceCodeConfDirPath() {
        return sourceCodeConfDirPath;
    }
    
    @VisibleForTesting
    File getExternalConfDir() {
        return externalConfDir;
    }
}


