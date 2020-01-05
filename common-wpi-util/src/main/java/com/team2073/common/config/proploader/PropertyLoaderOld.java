package com.team2073.common.config.proploader;

//import com.google.inject.AbstractModule;
//import org.reflections.Reflections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PropertyLoaderOld { //} extends AbstractModule {






	//	TODO: realoading values (oof) (talking to taters on thursday)
/*	I had an idea for this, when we load values could we keep a list of all the instances that had values
	loaded into them, and when we update, we just re load the new values?

	this might require a method in the domain object that is called tho, or we could not but it would mean the domain objects have to be used in a very specific way

	something like:

	ArrayList<Object> instances;

	public void loadProperty(Object propertyObject){
		instances.add(propertyObject);

		...
	}

	public void reloadProperties(){
		for(Object instance : instances){
			loadProperty(instance);
		}
	}

	on second thought we might have to make the instances a map or some other type that can hold the context that the instance was registered with also


	another option would just to have subsystems have a method that is  called whenever we reload values,
	and in that method they have access to this class and they can call load on all of their property files


 */


	private static final String DEFAULT_REMOTE_FILE_NAME = "conf";

	private final Logger logger = LoggerFactory.getLogger(getClass());
	//	@Inject TODO: add inject later
	private String primaryPropertyDirectoryName = DEFAULT_REMOTE_FILE_NAME;
//	private PropertyFileNameResolver propertyFileNameResolver = new DefaultPropertyFileNameResolver();
	private File primaryPropertyDirectory;
	private ClassLoader secondaryPropertyDirectory = Thread.currentThread().getContextClassLoader();

	private void init() {
		if (primaryPropertyDirectory == null) {
			primaryPropertyDirectory = new File(System.getProperty("user.home"));
			primaryPropertyDirectory = new File(primaryPropertyDirectory, primaryPropertyDirectoryName);
		}
	}

	public void loadProperties(List<Object> propertyObjectList) {
		for (Object it : propertyObjectList) {
			loadProperties(it);
		}
	}

	public void loadProperties(List<Object> propertyObjectList, List<String> context) {
		for (Object it : propertyObjectList) {
			loadProperties(it, context);
		}
	}

	public void loadProperties(Object propertyObject) {
		loadProperties(propertyObject, new ArrayList<>());
	}

	public void loadProperties(Object propertyObject, List<String> context) {
//		init();
//		Class clazz = propertyObject.getClass();
//		List<Properties> props = findProperties(propertyFileNameResolver.resolvePropertyFileName(clazz), context);
//
//		Field[] fields = clazz.getDeclaredFields();
//		for (Field field : fields) {
//			field.setAccessible(true);
//			String property = null;
//			for (Properties prop : props) {
//				property = prop.getProperty(field.getName());
//				if (property != null) {
//					break;
//				}
//			}
//			if (property == null) {
//				logger.error("All checks of the property for field [{}] in class [{}] were null", field.getName(), clazz.getSimpleName());
//			}
//
//			try {
//				if (property == null) {
//					if (field.get(propertyObject) == null && field.isAnnotationPresent(NotNull.class)) {
//						throw new IllegalStateException("This property is required and cannot be null: ["
//								+ field.getName() + "] in class " + clazz.getSimpleName());
//					}
//				}
//			} catch (IllegalArgumentException e) {
//				logger.warn("The specified instance is the wrong type for the given field", e);
//			} catch (IllegalAccessException e) {
//				logger.warn("This should never happen", e);
//			}
//			Class<?> declaringClass = field.getType();
//			if (property != null) {
//				try {
//					if (declaringClass.isAssignableFrom(Double.class) || declaringClass.isAssignableFrom(double.class)) {
//						field.set(propertyObject, Double.parseDouble(property));
//					} else if (declaringClass.isAssignableFrom(Integer.class) || declaringClass.isAssignableFrom(int.class)) {
//						field.set(propertyObject, Integer.parseInt(property));
//					} else if (declaringClass.isAssignableFrom(String.class)) {
//						field.set(propertyObject, property);
//					} else if (declaringClass.isAssignableFrom(Boolean.class) || declaringClass.isAssignableFrom(boolean.class)) {
//						field.set(propertyObject, Boolean.parseBoolean(property));
//					}
//				} catch (NumberFormatException e) {
//					logger.warn("The provided String does not contain a double", e);
//				} catch (IllegalArgumentException e) {
//					logger.warn("The provided instance is the wrong type for the declared field", e);
//				} catch (IllegalAccessException e) {
//					e.printStackTrace();
//				}
//			}
//
//			field.setAccessible(false);
//		}
	}

	private List<Properties> findProperties(String fileName, List<String> ctx) {
		List<Properties> propList = new ArrayList<>();
//		externalPropFile = new File(externalPropFile, "lvuser"); <-- this is the user, should only be needed if not logged in as lvuser
		final File externalPropFile = new File(primaryPropertyDirectory, fileName);

		FileInputStream propsInput;
		Properties props = new Properties();

//		TODO: please check this, im not sure if im accessing the remote files correctly

		if (ctx != null) {
//		1st priority, ctx remote files. Robot specific on RIO.
			for (String prefix : ctx) {
				String ctxFileName = prefix.concat("-" + fileName);
				File externalCtxPropFile = new File(primaryPropertyDirectory, ctxFileName);
				FileInputStream ctxPropsInput;
				try {
					ctxPropsInput = new FileInputStream(externalCtxPropFile);
					props = loadPropertiesFromPath(ctxPropsInput);
				} catch (FileNotFoundException e) {
					logger.debug("Could not find ctx file on RIO: [{}]", externalCtxPropFile);
				}
				if (!props.isEmpty())
					propList.add(props);
			}
		}
//		===============================================================

//		2nd priority, non ctx remote files. Non robot specific, on RIO.
		try {
			propsInput = new FileInputStream(externalPropFile);
			props = loadPropertiesFromPath(propsInput);
		} catch (FileNotFoundException e) {
			logger.debug("Could not find the file on rio");
		}
		if (!props.isEmpty()) {
			propList.add(props);
		}

//		===============================================================

//		3rd priority, ctx local files. Robot specific in src.
		if (ctx != null) {
			for (String prefix : ctx) {
				String ctxFileName = prefix.concat("-" + fileName);
				try {
					props = loadPropertiesFromPath(
							secondaryPropertyDirectory.getResourceAsStream(ctxFileName));
				} catch (Exception e) {
					logger.debug("couldnt find ctx file in local dir [{}]", ctxFileName);
				}

				if (!props.isEmpty()) {
					propList.add(props);
				}

			}
		}

//		===============================================================

//		4th priority, non ctx local files. Non robot specific in src.
		try {
			props = loadPropertiesFromPath(
					secondaryPropertyDirectory.getResourceAsStream(fileName));
		} catch (Exception e) {
			logger.debug("couldnt find file in local dir [{}]", fileName);
		}
		if (!props.isEmpty()) {
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
			return new Properties();
		}

		return props;
	}

	public PropertyLoaderOld setPrimaryPropertyDirectoryName(String primaryPropertyDirectoryName) {
		this.primaryPropertyDirectoryName = primaryPropertyDirectoryName;
		return this;
	}

	public PropertyLoaderOld setPrimaryPropertyDirectory(File primaryPropertyDirectory) {
		this.primaryPropertyDirectory = primaryPropertyDirectory;
		return this;
	}

	public PropertyLoaderOld setSecondaryPropertyDirectory(ClassLoader secondaryPropertyDirectory) {
		this.secondaryPropertyDirectory = secondaryPropertyDirectory;
		return this;
	}

//	public PropertyLoaderOld setPropertyFileNameResolver(PropertyFileNameResolver propertyFileNameResolver) {
//		this.propertyFileNameResolver = propertyFileNameResolver;
//		return this;
//	}

	@FunctionalInterface
	public interface PropertyFileNameResolver {
		String resolvePropertyFileName(Class<?> propertyClass);
	}

}
