package com.team2073.common.proploader;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.team2073.common.ctx.RobotContext;
import com.team2073.common.exception.EagleEx;
import com.team2073.common.proploader.model.PropertyContainer;
import com.team2073.common.proploader.model.PropertyContainerWrapper;
import com.team2073.common.proploader.model.PropertyFileAccessor;
import com.team2073.common.proploader.model.PropertyMapping;
import com.team2073.common.util.Ex;
import com.team2073.common.util.FileUtil;
import com.team2073.common.util.JvmUtil;
import com.team2073.common.util.ReflectionUtil;
import com.team2073.common.util.StringUtil;
import javassist.Modifier;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.ex.ConversionException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    private static final Configurations configBuilder = new Configurations();
    private static final Set<LoggedInvalidDataTypeCompositeKey> alreadyLoggedInvalidDataType = new HashSet();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss.SSS");


    private static Logger log = LoggerFactory.getLogger(PropertyLoaderHelper.class);

    static <T extends Class<?>> Object constructPropertyContainer(T propContainerClass) {
        Object propContainer;
        String classSimpleName = simpleName(propContainerClass);

        if (propContainerClass.isInterface()) {
            throw Ex.illegalArg("Property container objects must not be an interface. " +
                    "Offending class: [{}].", classSimpleName);
        }

        if (Modifier.isAbstract(propContainerClass.getModifiers())) {
            throw Ex.illegalArg("Property container objects must not be abstract. " +
                    "Offending class: [{}].", classSimpleName);
        }

        if (!Modifier.isPublic(propContainerClass.getModifiers())) {
            throw Ex.illegalArg("Property container objects must have a PUBLIC no-argument constructor. " +
                    "Offending class: [{}].", classSimpleName);
        }

        Constructor<?> constructor = null;
        try {
            constructor = propContainerClass.getConstructor();
        } catch (NoSuchMethodException e) {
            throw Ex.illegalArg("Property container objects must have a no-argument constructor. " +
                    "Offending class: [{}]", classSimpleName, e);
        }

        constructor.setAccessible(true);

        try {
            propContainer = constructor.newInstance();
            return propContainer;

        } catch (InvocationTargetException e) {
            throw Ex.illegalState("Property container object threw an exception during construction. " +
                    "Offending class: [{}].", classSimpleName, e);

        } catch (InstantiationException e) {
            throw EagleEx.newInternal("We verified the class is not abstract yet still get an " +
                    "InstantiationException when attempting to construct it. " +
                    "Offending class: [{}].", classSimpleName, e);

        } catch (IllegalAccessException e) {
            throw EagleEx.newInternal("We set the constructor to accessible yet we still get an " +
                    "IllegalAccessException when attempting to construct it. " +
                    "Offending class: [{}].", classSimpleName, e);

        } catch (IllegalArgumentException e) {
            throw EagleEx.newInternal("We verified a no-args constructor exists yet we still get an " +
                    "IllegalArgumentException when attempting to construct it. " +
                    "Offending class: [{}].", classSimpleName, e);
        }
    }

    static List<PropertyMapping> createMappings(Object propContainer) {
        return createMappings(propContainer, propContainer, 1, "");
    }

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
            String fieldName = field.getName();
            Class<?> type = field.getType();
            if (!ReflectionUtil.isPrimitiveOrWrapper(type)) {

                // This property field is actually another property class, recursively parse all it's fields
                // into mappings, prefixing with the current key
                Object childPropContainer = constructPropertyContainer(type);
                field.setAccessible(true);
                try {
                    field.set(propContainer, childPropContainer);
                } catch (IllegalAccessException e) {
                    throw Ex.illegalState("Exception setting field [{}].",
                            simpleName(propContainer) + "." + fieldName, e);
                }
    
                propMappingList.addAll(createMappings(rootPropContainer, childPropContainer, ++depth, keyPrefix + fieldName + "."));

            } else {

                // we have reached an actual property, create the mapping
                propMappingList.add(new PropertyMapping(propContainer, field, keyPrefix + fieldName));
            }
        }

        return propMappingList;
    }

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
                // Resovle a profile-specific property file. Ex: Foo-profile-name.properties
                String propFileName = resolvePropertyFileName(propContainerClass, propContainerName, Optional.of(profile));
                resolveCreateAndAddPropFile(profile, dir, propFileName, propContainerName, propFileList);
            }
        }

        return propFileList;
    }

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

    static PropertiesConfiguration createConfiguration(String propContainerName, File propFile) {

        if (!propFile.exists()) {
            throw EagleEx.newInternal("Cannot create property accessor for a non-existent file. " +
                    "Property container: [{}], File: [{}].", propContainerName, propFile.getAbsolutePath());
        }

        try {
            return configBuilder.properties(propFile);
        } catch (ConfigurationException e) {
            throw Ex.illegalState("ConfigurationException occurred creating properties configuration. " +
                    "Property container: [{}]. Property file: [{}].", e);
        }
    }

    @VisibleForTesting
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
            return Optional.of(rioPropDir);
        } else {
            boolean success = rioPropDir.mkdirs();
    
            if (!success) {
                log.error("Exception creating external conf directory. " +
                        "Target dir: [{}].", rioPropDir);
                return Optional.empty();
            } else {
                return Optional.of(rioPropDir);
            }
        }
    }

    static Optional<File> createAndResolveSourceCodePropDir(String confDirName, Class<?> propContainerClass) {
        InputStream sourceCodePropDirStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(confDirName);
        if (sourceCodePropDirStream == null) {
            log.info("No property directory found in source code at [src/main/resources/{}]. Skipping...", confDirName);
            return Optional.empty();

        } else {
            RobotContext robotContext = RobotContext.getInstance();
            File targetDir = new File(robotContext.getRobotDirectory().getTempDir(), "property-loader");
            targetDir = new File(targetDir, "copied-from-source-code");
            String timestamp = FileUtil.formatTimestamp(RobotContext.getInstance().getBootTimestamp());
            targetDir = new File(targetDir, confDirName);
            targetDir = new File(targetDir, timestamp);

            String targetDirPath = targetDir.getAbsolutePath();

            try {
                JvmUtil.copyResourcesToDirectory(PropertyLoaderHelper.class, confDirName, targetDir);
                log.debug("Copied source code properties to temp directory: [{}].", targetDirPath);
                return Optional.of(targetDir);
            } catch (IOException e) {
                log.error("Exception copying source code properties to temp directory. " +
                        "Target dir: [{}].", targetDirPath, e);
                return Optional.empty();
            }
        }
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
