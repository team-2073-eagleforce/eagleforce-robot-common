package com.team2073.common.proploader;

import com.google.common.base.Objects;
import com.team2073.common.ctx.RobotContext;
import com.team2073.common.exception.EagleEx;
import com.team2073.common.proploader.model.PropertyContainer;
import com.team2073.common.proploader.model.PropertyContainerField;
import com.team2073.common.proploader.model.PropertyContainerFieldIgnore;
import com.team2073.common.proploader.model.PropertyContainerWrapper;
import com.team2073.common.proploader.model.PropertyFileAccessor;
import com.team2073.common.proploader.model.PropertyMapping;
import com.team2073.common.util.Ex;
import com.team2073.common.util.FileUtil;
import com.team2073.common.util.JvmUtil;
import com.team2073.common.util.ReflectionUtil;
import com.team2073.common.util.StringUtil;
import javassist.Modifier;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.ReloadingFileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.ex.ConversionException;
import org.apache.commons.configuration2.reloading.PeriodicReloadingTrigger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.team2073.common.util.ClassUtil.*;

/**
 * @author Preston Briggs
 */
class PropertyLoaderHelper {

    /**
     * A property file will look something like [ application-mainbot.properties ] and
     * the corresponding class would have a name of [ ApplicationProperties ] and have the @{@link PropertyContainer} annotation.
     */
    private static final String PROPERTY_CLASS_SUFFIX = "Properties";
    /**
     * A property file will look something like [ application-mainbot.properties ] and
     * the corresponding class would have a name of [ ApplicationProperties ] and have the @{@link PropertyContainer} annotation.
     */
    private static final String PROPERTY_FILE_SUFFIX = ".properties";
    
    private static Logger log = LoggerFactory.getLogger(PropertyLoaderHelper.class);

    private static final Configurations configBuilder = new Configurations();
    private static final Set<LoggedInvalidDataTypeCompositeKey> alreadyLoggedInvalidDataType = new HashSet<>();
    
    static <T extends Class<?>> Object constructPropertyContainer(T propContainerClass) {
        return constructPropertyContainer(propContainerClass, Optional.empty());
    }
    
    static <T extends Class<?>> Object constructPropertyContainer(T propContainerClass, Optional<String> fieldPath) {
        
        Object propContainer;
        String classSimpleName = simpleName(propContainerClass);
        String offendingFieldMsg = "";
        if (fieldPath.isPresent()) {
            String prefix = fieldPath.get();
            prefix = StringUtils.removeEnd(prefix, ".");
            offendingFieldMsg = " Offending field: [" + prefix + "].";
        }

        if (propContainerClass.isInterface()) {
            throw Ex.illegalArg("Property container classes must not be an interface. " +
                    "Offending class: [{}].{}", classSimpleName, offendingFieldMsg);
        }

        if (Modifier.isAbstract(propContainerClass.getModifiers())) {
            throw Ex.illegalArg("Property container classes must not be abstract. " +
                    "Offending class: [{}].{}", classSimpleName, offendingFieldMsg);
        }
    
        if (ReflectionUtil.isInnerClass(propContainerClass)) {
            throw Ex.illegalArg("Property container classes must not be a NON-STATIC inner class " +
                    "(use 'public static class {}' instead of 'public class {}'). " +
                    "Offending class: [{}].{}", classSimpleName, offendingFieldMsg, classSimpleName, classSimpleName);
        }

        if (!Modifier.isPublic(propContainerClass.getModifiers())) {
            throw Ex.illegalArg("Property container classes must be public classes. " +
                    "Offending class: [{}].{}", classSimpleName, offendingFieldMsg);
        }

        Constructor<?> constructor = null;
        try {
            constructor = propContainerClass.getDeclaredConstructor();
        } catch (NoSuchMethodException e) {
            throw Ex.illegalArg("Property container classes must have a no-argument constructor. " +
                    "Offending class: [{}]", classSimpleName, offendingFieldMsg, e);
        }

        constructor.setAccessible(true);

        try {
            propContainer = constructor.newInstance();
            return propContainer;

        } catch (InvocationTargetException e) {
            throw Ex.illegalState("Property container object threw an exception during construction. " +
                    "Offending class: [{}].{} Exception: [{}]", classSimpleName, offendingFieldMsg, e.getCause().getMessage(), e);

        } catch (InstantiationException e) {
            throw EagleEx.newInternal("We verified the class is not abstract yet still get an " +
                    "InstantiationException when attempting to construct it. " +
                    "Offending class: [{}].{}", classSimpleName, offendingFieldMsg, e);

        } catch (IllegalAccessException e) {
            throw EagleEx.newInternal("We set the constructor to accessible yet we still get an " +
                    "IllegalAccessException when attempting to construct it. " +
                    "Offending class: [{}].{}", classSimpleName, offendingFieldMsg, e);

        } catch (IllegalArgumentException e) {
            throw EagleEx.newInternal("We verified a no-args constructor exists yet we still get an " +
                    "IllegalArgumentException when attempting to construct it. " +
                    "Offending class: [{}].{}", classSimpleName, offendingFieldMsg, e);
        }
    }

    static List<PropertyMapping> createMappings(Object propContainer) {
        return createMappings(propContainer, propContainer, 1, "");
    }
    
    /**
     * Create a {@link PropertyMapping} from every field in the given "propContainer".
     *
     * <ul>
     *     <li>Fields annotated with {@literal @}{@link PropertyContainerFieldIgnore} will be ignored</li>
     *     <li>The corresponding property key of a field will be resolved as the value of
     *     {@link PropertyContainerField#name()} if it exists, else the name of the field</li>
     *     <li>If the field is not a primitive type, it will be passed back into this method recursively to create a
     *     'nested' mapping (a Property Container class with other Property Container classes as fields)
     *          <ul>
     *              <li>The 'nested' Property Container's field's propterty keys will be resolved by taking the key
     *              of the 'parent' field and appending the resolved key of the nested Property Container's field</li>
     *              <li>This will keep happening recursively until a non-primitive field is reached</li>
     *          </ul>
     *     </li>
     * </ul>
     *
     *
     * @param rootPropContainer The initial Property Container that is being registered, needed mostly for logging purposes
     * @param propContainer The current Property Container to create mappings for, may be the same Object as rootPropContainer
     *                      or may be a nested Property Container field
     * @param depth The number of recursive calls, used to monitor a cyclical field and throw an exception if detected
     * @param keyPrefix The currently resolved property key, a nested key will be appended to this
     * @return The list of {@link PropertyMapping}s created
     */
    private static List<PropertyMapping> createMappings(Object rootPropContainer, Object propContainer, int depth,
            String keyPrefix) {

        List<PropertyMapping> propMappingList = new ArrayList<>();

        if (depth > 50) {
            String recursivePath = keyPrefix.replace(".", " > ");
            throw Ex.illegalState("Recursive property container detected. Check for a circular reference. " +
                    "Property container: [{}], Recursive path: [{}].", rootPropContainer, recursivePath);
        }

        List<Field> fields = ReflectionUtil.getInheritedPrivateFields(propContainer.getClass());

        for (Field field : fields) {
            
            if (field.getAnnotation(PropertyContainerFieldIgnore.class) != null)
                continue;
    
            if (Modifier.isStatic(field.getModifiers()))
                continue;
    
            if (Modifier.isFinal(field.getModifiers()))
                continue;
    
            PropertyContainerField propFieldAnnon = field.getAnnotation(PropertyContainerField.class);
            String propName;
            if (propFieldAnnon == null || StringUtil.isEmpty(propFieldAnnon.name()))
                propName = field.getName();
            else
                propName = propFieldAnnon.name();
            
            Class<?> type = field.getType();
            if (!ReflectionUtil.isPrimitiveOrWrapper(type) && !field.getType().isEnum()) {

                // This property field is actually another property class, recursively parse all it's fields
                // into mappings, prefixing with the current key
                Object childPropContainer = constructPropertyContainer(type, Optional.of(simpleName(rootPropContainer)
                        + "." + keyPrefix + propName));
                field.setAccessible(true);
                try {
                    field.set(propContainer, childPropContainer);
                } catch (IllegalAccessException e) {
                    throw Ex.illegalState("Exception setting field [{}].",
                            simpleName(propContainer) + "." + propName, e);
                }
    
                propMappingList.addAll(createMappings(rootPropContainer, childPropContainer, ++depth, keyPrefix + propName + "."));

            } else {

                // we have reached an actual property, create the mapping
                propMappingList.add(new PropertyMapping(propContainer, field, keyPrefix + propName));
            }
        }

        return propMappingList;
    }
    
    /**
     * Find all the .properties files for a given Property Container.
     */
    static List<PropertyFileAccessor> findPropertyFiles(Class<?> propContainerClass, String propContainerName, Iterable<File> dirList, Iterable<String> profileList) {

        List<PropertyFileAccessor> propFileList = new ArrayList<>();
        final String noProfilePropFileName;

        noProfilePropFileName = resolvePropertyFileName(propContainerClass, propContainerName, Optional.empty());

        for (File dir : dirList) {
            // Resolve a default (no-profile) property file. Ex: Foo.properties
            resolveCreateAndAddPropFile("", dir, noProfilePropFileName, propContainerName, propFileList);
        }

        for (String profile : profileList) {

            for (File dir : dirList) {
                // Resolve a profile-specific property file. Ex: Foo-profile-name.properties
                String propFileName = resolvePropertyFileName(propContainerClass, propContainerName, Optional.of(profile));
                resolveCreateAndAddPropFile(profile, dir, propFileName, propContainerName, propFileList);
            }
        }

        return propFileList;
    }
    
    /**
     * Resolve the name of a Property Container either using the {@literal @}{@link PropertyContainer} annotation's
     * "name" attribute if it exists, else using the class name, removing any trailing "Properties".
     */
    static String resolvePropertyContainerName(Object propContainer) {
        PropertyContainer annon = propContainer.getClass().getAnnotation(PropertyContainer.class);
        if (annon != null) {
            String name = annon.name();

            if (!StringUtil.isEmpty(name) && !name.equals(PropertyContainer.NULL))
                return name;
        }

        String propContainerName = simpleName(propContainer);

        if (propContainerName.endsWith(PROPERTY_CLASS_SUFFIX)) {
            // TODO: this could potentially replace multiple 'Properties' tokens, replace only the end instead
            // Ex: SomePropertiesFooProperties would turn into SomeFoo but it should turn into SomePropertiesFoo
            propContainerName = propContainerName.replace(PROPERTY_CLASS_SUFFIX, "");
        }

        return propContainerName;
    }

    private static void resolveCreateAndAddPropFile(String profile, File dir, String propFileName, String containerName, List<PropertyFileAccessor> propFileList) {
        File propFile = new File(dir, propFileName);

        if (propFile.exists()) {
            log.debug("Found existing property file. Property container: [{}], Profiles: [{}], File: [{}].",
                    containerName, profile, propFile.getAbsolutePath());
            PropertiesConfiguration propConfig = createConfiguration(containerName, propFile);
            propFileList.add(new PropertyFileAccessor(profile, propFile, propConfig, false));

        } else {
            try {
                FileUtils.touch(propFile);
                log.debug("Created non-existing property file. Property container: [{}], Profiles: [{}], File: [{}].",
                        containerName, profile, propFile.getAbsolutePath());
                PropertiesConfiguration propConfig = createConfiguration(containerName, propFile);
                propFileList.add(new PropertyFileAccessor(profile, propFile, propConfig, false));

            } catch (IOException e) {
                log.error("Exception creating property file. Not adding property file. Property container: [{}], Profiles: [{}], File: [{}].",
                        containerName, profile, propFile.getAbsolutePath(), e);
            }

        }
    }
    
    /**
     * Create the org.apache.commons.configuration2.{@link PropertiesConfiguration} object for loading properties from
     * a given file.
     */
    static PropertiesConfiguration createConfiguration(String propContainerName, File propFile) {

        if (!propFile.exists()) {
            throw EagleEx.newInternal("Cannot create property accessor for a non-existent file. " +
                    "Property container: [{}], File: [{}].", propContainerName, propFile.getAbsolutePath());
        }

        try {
            Parameters params = new Parameters();

            ReloadingFileBasedConfigurationBuilder<FileBasedConfiguration> builder =
                    new ReloadingFileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                            .configure(params.fileBased()
                                    .setFile(propFile));
            PeriodicReloadingTrigger trigger = new PeriodicReloadingTrigger(builder.getReloadingController(),
                    null, 1, TimeUnit.SECONDS);
            trigger.start();
            return builder.properties(propFile);
        } catch (ConfigurationException e) {
            throw Ex.illegalState("ConfigurationException occurred creating properties configuration. " +
                    "Property container: [{}]. Property file: [{}].", e);
        }
    }
    
    /**
     * Resolve the .properties file name taking into account the provided profile.
     */
    static String resolvePropertyFileName(Class<?> propContainerClass, String propContainerName, Optional<String> profile) {

        String fileName = propContainerName;

        if (profile.isPresent())
            fileName = fileName.concat("-" + profile.get());

        fileName = fileName.concat(PROPERTY_FILE_SUFFIX);

        if (profile.isPresent()) {
            log.debug("Class [{}] and profile [{}] resolved to property file name of [{}].", propContainerClass.getSimpleName(), profile.get(), fileName);
        } else {
            log.debug("Class [{}] resolved to property file name of [{}].", propContainerClass.getSimpleName(), fileName);
        }

        return fileName;
    }

    static Optional<File> createExternalPropDir(File rioPropDir) {

        if (rioPropDir.exists()) {
            log.debug("Using external properties directory of [{}].", rioPropDir.getAbsolutePath());
            return Optional.of(rioPropDir);
        } else {
            boolean success = rioPropDir.mkdirs();
    
            if (!success) {
                log.error("Exception creating external conf directory. Target dir: [{}].", rioPropDir);
                return Optional.empty();
            } else {
                log.debug("Using external properties directory of [{}].", rioPropDir.getAbsolutePath());
                return Optional.of(rioPropDir);
            }
        }
    }

    static Optional<File> createAndResolveSourceCodePropDir(String confDirName, Class<?> propContainerClass) {
    
        Optional<InputStream> inputStream = resolveSourceCodePropDirInputStream(confDirName, propContainerClass);
    
        if (!inputStream.isPresent()) {
            
            // Try adding/removing "/" prefix and search again
            if (confDirName.startsWith("/"))
                confDirName = confDirName.substring(1);
            else
                confDirName = "/" + confDirName;
    
            inputStream = resolveSourceCodePropDirInputStream(confDirName, propContainerClass);
    
            if (!inputStream.isPresent()) {
                
                // Remove leading slash for proper logging
                if (confDirName.startsWith("/"))
                    confDirName = confDirName.substring(1);
                
                log.info("No property directory found in source code at [src/main/resources/{}] (using classloader from [{}]). Skipping...",
                        confDirName, simpleName(propContainerClass));
                return Optional.empty();
            }
        }
    
        RobotContext robotContext = RobotContext.getInstance();
        File targetDir = new File(robotContext.getRobotDirectory().getTempDir(), "property-loader");
        targetDir = new File(targetDir, "copied-from-source-code");
        String timestamp = FileUtil.formatTimestamp(robotContext.getBootTimestamp());
        targetDir = new File(targetDir, confDirName);
        targetDir = new File(targetDir, timestamp);

        String targetDirPath = targetDir.getAbsolutePath();

        try {
            JvmUtil.copyResourcesToDirectory(PropertyLoaderHelper.class, confDirName, targetDir);
            log.debug("Copied source code properties from classpath directory [{}] to temp directory: [{}].", confDirName, targetDirPath);
            return Optional.of(targetDir);
        } catch (IOException e) {
            log.error("Exception copying source code properties to temp directory. " +
                    "Target dir: [{}].", targetDirPath, e);
            return Optional.empty();
        }
    }
    
    private static Optional<InputStream> resolveSourceCodePropDirInputStream(String confDirName, Class<?> propContainerClass) {
        
        // 1) Try finding conf dir using default classloader
        InputStream sourceCodePropDirStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(confDirName);
    
        if (sourceCodePropDirStream != null)
            return Optional.of(sourceCodePropDirStream);
        
        
        // 2) Try using class specific classloader
        sourceCodePropDirStream = propContainerClass.getResourceAsStream(confDirName);
    
        if (sourceCodePropDirStream != null)
            return Optional.of(sourceCodePropDirStream);
    
        return Optional.empty();
    
    }

    static void injectProperties(PropertyContainerWrapper containerWrapper) {

        List<PropertyMapping> mappingList = containerWrapper.getMappingList();
        List<PropertyFileAccessor> propFileList = containerWrapper.getPropFileList();

        for (PropertyFileAccessor accessor : propFileList) {

            PropertiesConfiguration propConfig = accessor.getPropConfig();

            for (PropertyMapping mapping : mappingList) {
    
                Object propContainer = mapping.getPropContainer();
                String propKey = mapping.getPropKey();
                Field field = mapping.getField();
                Class<?> type = field.getType();

                Object prop;
                try {
                    prop = propConfig.get(type, propKey);
                } catch (ConversionException e) {
                    logInvalidDataType(propConfig, propKey, field, type, mapping, accessor);
                    continue;
                }

                if (prop == null)
                    // This property was not found in the given properties file
                    continue;

                field.setAccessible(true);

                try {
                    field.set(propContainer, prop);
                } catch (IllegalAccessException e) {
                    log.warn("Exception occurred attempting to set field [{}] to value [{}].", field.getName(), prop.toString(), e);
                }
            }
        }
    }

    private static void logInvalidDataType(PropertiesConfiguration propConfig, String propKey, Field field,
            Class<?> type, PropertyMapping mapping, PropertyFileAccessor accessor) {

        LoggedInvalidDataTypeCompositeKey newKey = new LoggedInvalidDataTypeCompositeKey(mapping, accessor);
        boolean alreadyLogged = alreadyLoggedInvalidDataType.contains(newKey);

        if (alreadyLogged) {
            return;
        } else {
            alreadyLoggedInvalidDataType.add(newKey);
            Object actualValue = propConfig.get(Object.class, propKey);
            log.info("Invalid data type for property key/field name [{}/{}]. Expecting type [{}] but found value [{}]. " +
                            "Source file [{}]. (This will only be logged once per field/file combination)",
                    propKey, field.getName(), type.getSimpleName(), actualValue, accessor.getPropFile().getAbsolutePath());
        }
    }

    private static class LoggedInvalidDataTypeCompositeKey {
        public final PropertyMapping mapping;
        public final PropertyFileAccessor accessor;

        private LoggedInvalidDataTypeCompositeKey(PropertyMapping mapping, PropertyFileAccessor accessor) {
            this.mapping = mapping;
            this.accessor = accessor;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LoggedInvalidDataTypeCompositeKey that = (LoggedInvalidDataTypeCompositeKey) o;
            return Objects.equal(mapping, that.mapping) && Objects.equal(accessor, that.accessor);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(mapping, accessor);
        }
    }

}
