package com.team2073.common.proploader;

import com.team2073.common.proploader.model.PropertyContainer;
import com.team2073.common.util.Ex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author pbriggs
 */
class ReflectionSearchingPropertyLoader {
	
	
	public ReflectionSearchingPropertyLoader() {
		throw Ex.notImplemented("ReflectionSearchingPropertyLoader");
	}
	
	
	
	
	// Warning: this class is not ready yet
	

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private PropertyLoader propLoader;

	private String basePackage;

	/**
	 * @param basePackage The base package to scan from to find classes annotated with {@literal @}{@link PropertyContainer}.
	 */
	public ReflectionSearchingPropertyLoader(String basePackage) {
		this(basePackage, new PropertyLoader());
	}

	/**
	 *
	 * @param basePackage The base package to scan from to find classes annotated with {@literal @}{@link PropertyContainer}.
	 * @param propLoader A custom {@link PropertyLoader} in case custom logic is required
	 */
	public ReflectionSearchingPropertyLoader(String basePackage, PropertyLoader propLoader) {
		this.basePackage = basePackage;
		this.propLoader = new PropertyLoader();
	}

	/** Finds every class annotated with @{@link PropertyContainer} and loads properties of a created instance. */
	public void loadFromPackage() {
		loadFromPackage(new ArrayList<>());
	}

	public void loadFromPackage(String activeProfile) {
		// TODO: assertNotNull
		loadFromPackage(Arrays.asList(activeProfile));
	}

	public void loadFromPackage(List<String> activeProfileList) {
		// TODO: assertNotNull
//		List<Object> propertyObjectList = new ArrayList<>();
//		Reflections reflection = new Reflections(basePackage);
//		Set<Class<?>> subTypesOf = reflection.getTypesAnnotatedWith(PropertyContainer.class);
//		for (Class<?> clazz : subTypesOf) {
//			try {
//				Object propertyObject = classToInstance(clazz);
//				propLoader.registerPropContainer(propertyObject);
//				propertyObjectList.add(propertyObject);
//			} catch (IllegalAccessException | InstantiationException e) {
//				// TODO: Log a warning. This is most likely a problem with our code.
//			}
//		}
	}

	private Object classToInstance(Class<?> clazz) throws IllegalAccessException, InstantiationException {
		Object instance = null;
		Constructor validConstructor = null;
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

		return instance;
	}
}
