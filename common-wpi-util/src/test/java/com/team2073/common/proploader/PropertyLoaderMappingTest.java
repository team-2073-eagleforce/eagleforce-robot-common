package com.team2073.common.proploader;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * @author Preston Briggs
 */
public class PropertyLoaderMappingTest {
    
    private PropertyLoader loader = new PropertyLoader();
    
    public static class RecursiveParentProperties {
        private RecursiveChildProperties child;
    }
    
    public static class RecursiveChildProperties {
        private RecursiveParentProperties parent;
    }

    @Test
    @DisplayName("WHEN: Property container has recursive properties - THEN: Exception is thrown")
    void recursiveProperties() {
        Class<?> clazz = RecursiveParentProperties.class;
        assertThatIllegalStateException()
                .isThrownBy(() -> loader.registerPropContainer(clazz))
                .withMessageStartingWith("Recursive property container detected")
                .withMessageContaining(clazz.getSimpleName());
    }
    
    public static class InheritedParentProperties {
        private String foo;
    
        public String getFoo() {
            return foo;
        }
    }
    
    public static class InheritedChildProperties extends InheritedParentProperties {
        public String bar;
    }

    @Test
    @DisplayName("WHEN: Property container has inherited properties - THEN: Inherited properties are mapped properly")
    void inheritedProperties() {
        final String expectedFooValue = "abc";
        final String expectedBarValue = "xyz";
    
        Class<InheritedChildProperties> clazz = InheritedChildProperties.class;
        loader.setSourceCodeConfDirPath("com/team2073/common/proploader/PropertyLoaderMappingTest/inherited-properties");
        InheritedChildProperties parentProperties = loader.registerPropContainer(clazz);
    
        loader.loadProperties();
    
        String actualFooValue = parentProperties.getFoo();
        String actualBarValue = parentProperties.bar;
    
        assertThat(actualFooValue).isEqualTo(expectedFooValue);
        assertThat(actualBarValue).isEqualTo(expectedBarValue);
    }
    
    public static class ParentProperties {
        private String foo;
        private ChildProperties child;
    }
    
    public static class ChildProperties {
        private String bar;
    }

    @Test
    @DisplayName("WHEN: Property container has nested class properties - THEN: Nested class properties are mapped and loaded properly")
    void nestedProperties() {
        final String expectedFooValue = "abc";
        final String expectedChildBarValue = "xyz";
        
        Class<ParentProperties> clazz = ParentProperties.class;
        loader.setSourceCodeConfDirPath("com/team2073/common/proploader/PropertyLoaderMappingTest/nested-properties");
        ParentProperties parentProperties = loader.registerPropContainer(clazz);
    
        loader.loadProperties();
    
        String actualFooValue = parentProperties.foo;
        String actualChildBarValue = parentProperties.child.bar;
    
        assertThat(actualFooValue).isEqualTo(expectedFooValue);
        assertThat(actualChildBarValue).isEqualTo(expectedChildBarValue);
    }
}
