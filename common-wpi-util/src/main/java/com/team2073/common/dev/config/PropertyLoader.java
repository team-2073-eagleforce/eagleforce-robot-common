package com.team2073.common.dev.config;

import com.google.inject.AbstractModule;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

public class PropertyLoader extends AbstractModule {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	//	@Inject TODO: add inject later
	private ApplicationContext ctx = new ApplicationContext();

	public List<Properties> findProperties(String fileName) {
		List<Properties> propList = new ArrayList<>();
		File externalPropFile = new File(System.getProperty("user.home"));
//		externalPropFile = new File(externalPropFile, "lvuser"); <-- this is the user, should only be needed if not logged in as lvuser
		externalPropFile = new File(externalPropFile, "eagleforce-properties");
		externalPropFile = new File(externalPropFile, fileName);
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
					Thread.currentThread().getContextClassLoader().getResourceAsStream(ctxFileName));

			if (props != null) {
				propList.add(props);
			}

		}

//		===============================================================

//		4th priority, non ctx local files. Non robot specific in src.
		props = loadPropertiesFromPath(
				Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName));
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

	private String classNameToFileName(String className) {
		String fileName = null;
		if (className.endsWith("Properties")) {
			fileName = className.replace("Properties", "");
			fileName = fileName.toLowerCase();
			fileName = fileName.concat(".properties");
		} else {
			logger.warn("Class [" + className + "] Name does not end with [Properties]  ");
		}
		return fileName;
	}

//	TODO: *check*  Look in a file based on the class name (convert class name to file name) (strip property names, think of how the logging was set up)
//	TODO: *check* Create hierarchy of property values (external, internal properties, internal defaults, fail if @NotNull) 
//	TODO: *check* Find all classes that implement configuration and pass them into this method
//	TODO: realoading values (oof) (talking to taters on thursday)
//	TODO: *check* allow different profiles (application-test.properties vs mainbot vs practicebot)

	public List<Object> init() {

		Reflections reflection = new Reflections("com.team2073");
		Set<Class<?>> subTypesOf = reflection.getTypesAnnotatedWith(com.team2073.common.dev.config.Properties.class);
		List<Object> configList = new ArrayList<>();
		for (Class<?> clazz : subTypesOf) {
			Object load = load(clazz);
			configList.add(load);
		}
		return configList;
	}

	public Object load(Class<?> clazz) {
		List<Properties> props = findProperties(classNameToFileName(clazz.getSimpleName()));
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
					if (field.get(instance) == null && field.isAnnotationPresent(NotNull.class)) {
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
						field.set(instance, Double.parseDouble(property));
					} else if (declaringClass.isAssignableFrom(Integer.class)) {
						field.set(instance, Integer.parseInt(property));
					} else if (declaringClass.isAssignableFrom(String.class)) {
						field.set(instance, property);
					} else if (declaringClass.isAssignableFrom(Boolean.class)) {
						field.set(instance, Boolean.parseBoolean(property));
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

		return instance;
	}

	@Override
	protected void configure() {
		List<Object> init = init();
		for (Object configuration : init) {
			bind((Class<Object>) configuration.getClass()).toInstance(load(configuration.getClass()));
		}
	}

}
