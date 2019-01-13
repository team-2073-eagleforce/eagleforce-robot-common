package com.team2073.common.proploader.model;

import java.lang.reflect.Field;

/**
 * @author Preston Briggs
 */
public class PropertyMapping {

    private final Object propContainer;
    private final Field field;
    private final String propKey;

    public PropertyMapping(Object propContainer, Field field, String propKey) {
        this.propContainer = propContainer;
        this.field = field;
        this.propKey = propKey;
    }
    
    public Object getPropContainer() {
        return propContainer;
    }
    
    public Field getField() {
        return field;
    }

    public String getPropKey() {
        return propKey;
    }

    @Override
    public String toString() {
        return field.getName() + ":" + propKey;
    }
}
