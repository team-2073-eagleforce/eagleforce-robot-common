package com.team2073.common.config;

//import com.google.inject.AbstractModule;
//import org.reflections.Reflections;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

public class PropertyLoader { //} extends AbstractModule {
	//	TODO: realoading values (oof) (talking to taters on thursday)


	private static final String DEFAULT_REMOTE_FILE_NAME = "eagleforce-properties";
	/**
	 * A property file will look something like [ mainbot-application.properties ] and
	 * the corresponding class would have a name of [ ApplicationProperties ] and have the @{@link PropertyContainer} annotation.
	 */
	private static final String PROPERTY_CLASS_SUFFIX = "Properties";
	/**
	 * A property file will look something like [ mainbot-application.properties ] and
	 * the corresponding class would have a name of [ ApplicationProperties ] and have the @{@link PropertyContainer} annotation.
	 */
	private static final String PROPERTY_FILE_SUFFIX = ".properties";
	private final Logger logger = LoggerFactory.getLogger(getClass());
	//	@Inject TODO: add inject later
	private ApplicationContext ctx = new ApplicationContext();
	private File primaryPropertyDirectory;
	private ClassLoader secondaryPropertyDirectory = Thread.currentThread().getContextClassLoader();


	public PropertyLoader() {
		primaryPropertyDirectory = new File(System.getProperty("user.home"));
		primaryPropertyDirectory = new File(primaryPropertyDirectory, DEFAULT_REMOTE_FILE_NAME);

	}

	public void setPrimaryPropertyDirectory(File primaryPropertyDirectory) {
		this.primaryPropertyDirectory = primaryPropertyDirectory;
	}

	public void setSecondaryPropertyDirectory(ClassLoader secondaryPropertyDirectory) {
		this.secondaryPropertyDirectory = secondaryPropertyDirectory;
	}

	public void loadProperties(Object propertyObject) {
		Class clazz = propertyObject.getClass();
		List<Properties> props = findProperties(resolvePropertyFileName(clazz, null));

		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			field.setAccessible(true);
			String property = null;
			for (Properties prop : props) {
				property = prop.getProperty(field.getName());
				if (property != null) {
					break;
				}
				logger.debug("Could not find property [" + prop + "]");
			}
			if (property == null) {
				logger.debug("All checks of the property were null");
			}

			try {
				if (property == null) {
					if (field.get(propertyObject) == null && field.isAnnotationPresent(NotNull.class)) {
						throw new IllegalStateException("This property is required and cannot be null: ["
								+ field.getName() + "] in class " + clazz.getSimpleName());
					}
				}
			} catch (IllegalArgumentException e) {
				logger.warn("The specified instance is the wrong type for the given field", e);
			} catch (IllegalAccessException e) {
				logger.warn("This should never happen", e);
			}
			Class<?> declaringClass = field.getType();
			if (property != null) {
				try {
					if (declaringClass.isAssignableFrom(Double.class)) {
						field.set(propertyObject, Double.parseDouble(property));
					} else if (declaringClass.isAssignableFrom(Integer.class)) {
						field.set(propertyObject, Integer.parseInt(property));
					} else if (declaringClass.isAssignableFrom(String.class)) {
						field.set(propertyObject, property);
					} else if (declaringClass.isAssignableFrom(Boolean.class)) {
						field.set(propertyObject, Boolean.parseBoolean(property));
					}
				} catch (NumberFormatException e) {
					logger.warn("The provided String does not contain a double", e);
				} catch (IllegalArgumentException e) {
					logger.warn("The provided instance is the wrong type for the declared field", e);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}

			field.setAccessible(false);
		}
	}

	public void loadProperties(List<Object> propertyObjectList) {
		for (Object it : propertyObjectList) {
			loadProperties(it);
		}
	}

	public void loadProperties(List<Object> propertyObjectList, List<String> activeProfiles) {
		ctx.setActiveProfiles(activeProfiles);
		for (Object it : propertyObjectList) {
			loadProperties(it);
		}
	}

	protected String resolvePropertyFileName(Class<?> propertyClass, Optional<String> profile) {
		String fileName = null;
		String className = propertyClass.getSimpleName();
		if (className.endsWith(PROPERTY_CLASS_SUFFIX)) {
			fileName = className.replace(PROPERTY_CLASS_SUFFIX, "");
			fileName = fileName.toLowerCase();
			if (profile.isPresent()) {
				fileName = profile.get().concat("-" + fileName);
			}
			fileName = fileName.concat(PROPERTY_FILE_SUFFIX);
		} else {
			logger.warn("Class [" + className + "] Name does not end with [{}]", PROPERTY_CLASS_SUFFIX);
		}
		return fileName;
	}


	public List<Properties> findProperties(String fileName) {
		List<Properties> propList = new ArrayList<>();
//		externalPropFile = new File(externalPropFile, "lvuser"); <-- this is the user, should only be needed if not logged in as lvuser
		File externalPropFile = new File(primaryPropertyDirectory, fileName);
		FileInputStream propsInput;
		Properties props = null;


//		1st priority, ctx remote files. Robot specific on RIO.
		for (String prefix : ctx.getActiveProfiles()) {
			String ctxFileName = prefix.concat("-" + fileName);
			FileInputStream ctxPropsInput;
			try {
				ctxPropsInput = new FileInputStream(ctxFileName);
				props = loadPropertiesFromPath(ctxPropsInput);
			} catch (FileNotFoundException e) {
				logger.debug("Could not find the file");
			}
			if (props != null)
				propList.add(props);
		}

//		===============================================================

//		2nd priority, non ctx remote files. Non robot specific, on RIO.
		try {
			propsInput = new FileInputStream(externalPropFile);
			props = loadPropertiesFromPath(propsInput);
		} catch (FileNotFoundException e) {
			logger.debug("Could not find the file on rio");
		}
		if (props != null) {
			propList.add(props);
		}

//		===============================================================

//		3rd priority, ctx local files. Robot specific in src.
		for (String prefix : ctx.getActiveProfiles()) {
			String ctxFileName = prefix.concat("-" + fileName);
			props = loadPropertiesFromPath(
					secondaryPropertyDirectory.getResourceAsStream(ctxFileName));

			if (props != null) {
				propList.add(props);
			}

		}

//		===============================================================

//		4th priority, non ctx local files. Non robot specific in src.
		props = loadPropertiesFromPath(
				secondaryPropertyDirectory.getResourceAsStream(fileName));
		if (props != null) {
			propList.add(props);
		}

//		Last priority is default values hardcoded in the definition of the class, these do not need to be added to the propList

		return propList;
	}

	private Properties loadPropertiesFromPath(InputStream inputStream) {
		Properties props = new Properties();
		try {
			props.load(inputStream);

		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		return props;
	}

	/**
	 * Finds every class annotated with @{@link PropertyContainer} and loads properties of a created instance (i think this is useless plz lmk if it is / isnt)
	 */
	public void init() {
		Reflections reflection = new Reflections("com.team2073");
		Set<Class<?>> subTypesOf = reflection.getTypesAnnotatedWith(PropertyContainer.class);
		for (Class<?> clazz : subTypesOf) {
			loadProperties(classToInstance(clazz));
		}
	}

	private Object classToInstance(Class<?> clazz) {
		Object instance = null;
		Constructor validConstructor = null;
		try {
			for (Constructor constructor : clazz.getConstructors()) {
				if (constructor.getParameterCount() == 0) {
					validConstructor = constructor;
				}
			}
			if (validConstructor == null) {
				throw new IllegalStateException("No valid (0 parameter) constructors found for class [" + clazz.getSimpleName() + "] cannot create an instance");
			} else {
				instance = clazz.newInstance();
			}
		} catch (InstantiationException e) {
			logger.warn("Cannot create new instance of this class, [" + clazz.getName() + "] ", e);
		} catch (IllegalAccessException e) {
			logger.warn("Cannot create new instance of this class, [" + clazz.getName() + "] ", e);
		}

		return instance;
	}

//	@Override
//	protected void configure() {
//		List<Object> init = init();
//		for (Object configuration : init) {
//			bind((Class<Object>) configuration.getClass()).toInstance(load(configuration.getClass()));
//		}
//	}

}
