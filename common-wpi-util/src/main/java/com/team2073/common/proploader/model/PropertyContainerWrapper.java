package com.team2073.common.proploader.model;

import java.util.Collections;
import java.util.List;

/**
 * @author Preston Briggs
 */
public class PropertyContainerWrapper {

    private final Object propContainer;
    private final Class<?> propContainerClass;
    private final List<PropertyMapping> mappingList;
    private final List<PropertyFileAccessor> propFileList;
    private final String name;

    public PropertyContainerWrapper(Object propContainer, Class<?> propContainerClass, String propContainerName, List<PropertyMapping> mappingList, List<PropertyFileAccessor> propFileList) {
        this.propContainerClass = propContainerClass;
        this.propContainer = propContainer;
        this.mappingList = mappingList;
        this.propFileList = propFileList;
        this.name = propContainerName;
    }

    public Class<?> getPropContainerClass() {
        return propContainerClass;
    }

    public Object getPropContainer() {
        return propContainer;
    }

    public List<PropertyMapping> getMappingList() {
        return mappingList;
    }

    public List<PropertyFileAccessor> getPropFileList() {
        return Collections.unmodifiableList(propFileList);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
