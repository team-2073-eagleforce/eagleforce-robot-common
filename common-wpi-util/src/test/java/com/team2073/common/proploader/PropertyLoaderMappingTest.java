package com.team2073.common.proploader;

import com.team2073.common.proploader.PropertyLoaderMappingTest.ParentClass.NestedClass.SubNestedClass;
import com.team2073.common.proploader.model.PropertyContainerField;
import com.team2073.common.proploader.model.PropertyContainerFieldIgnore;
import com.team2073.common.proploader.model.PropertyContainerWrapper;
import com.team2073.common.proploader.model.PropertyMapping;
import org.apache.commons.configuration2.DatabaseConfiguration;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * @author Preston Briggs
 */
public class PropertyLoaderMappingTest extends PropertyLoaderBaseTest {
    
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
    
    public static class StaticFieldProperties {
        public static String STATIC_FIELD;
    }
    
    @Test
    @DisplayName("WHEN: Property container has static field - THEN: Field is not mapped")
    void staticField() {
        Class<StaticFieldProperties> clazz = StaticFieldProperties.class;
        loader.registerPropContainer(clazz);
        Collection<PropertyContainerWrapper> wrapperList = loader.getContainerWrapperList();
        
        assertThat(wrapperList).size().isEqualTo(1);
        
        List<PropertyMapping> mappingList = wrapperList.iterator().next().getMappingList();
        assertThat(mappingList).isEmpty();
    }
    
    public static class FinalFieldProperties {
        public final String finalField = "foo";
    }
    
    @Test
    @DisplayName("WHEN: Property container has final field - THEN: Field is not mapped")
    void finalField() {
        Class<FinalFieldProperties> clazz = FinalFieldProperties.class;
        loader.registerPropContainer(clazz);
        Collection<PropertyContainerWrapper> wrapperList = loader.getContainerWrapperList();
        
        assertThat(wrapperList).size().isEqualTo(1);
        
        List<PropertyMapping> mappingList = wrapperList.iterator().next().getMappingList();
        assertThat(mappingList).isEmpty();
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
        loader.setSourceCodePropertiesDirPath("com/team2073/common/proploader/PropertyLoaderMappingTest/inherited-properties");
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
        loader.setSourceCodePropertiesDirPath("com/team2073/common/proploader/PropertyLoaderMappingTest/nested-properties");
        ParentProperties parentProperties = loader.registerPropContainer(clazz);
    
        loader.loadProperties();
        
        String actualFooValue = parentProperties.foo;
        String actualChildBarValue = parentProperties.child.bar;
        
        assertThat(actualFooValue).isEqualTo(expectedFooValue);
        assertThat(actualChildBarValue).isEqualTo(expectedChildBarValue);
    }
    
    public static class NonPropertyContainerProperties {
        private String bar;
        private DatabaseConfiguration log;
    }
    
    @Test
    @DisplayName("WHEN: Property container has non-property container nested class properties - THEN: Exception is thrown")
    void nonPropContainerNestedProperties() {
        
        // This test is kinda pointless cause this actually fails due to classes containing fields that are interfaces etc.
        Class<NonPropertyContainerProperties> clazz = NonPropertyContainerProperties.class;
        assertThatIllegalArgumentException().isThrownBy(() -> loader.registerPropContainer(clazz));
    }
    
    public static class CustomFieldNameProperties {
        public static final String FIELD_NAME = "foobarbaz";
        @PropertyContainerField(name = FIELD_NAME)
        private String foo;
    }
    
    @Test
    @DisplayName("WHEN: Property container field has @PropertyContainerField - THEN: Custom name is used")
    void customFieldName() {
        Class<CustomFieldNameProperties> clazz = CustomFieldNameProperties.class;
        loader.registerPropContainer(clazz);
        Collection<PropertyContainerWrapper> wrapperList = loader.getContainerWrapperList();
        
        assertThat(wrapperList).size().isEqualTo(1);
        
        List<PropertyMapping> mappingList = wrapperList.iterator().next().getMappingList();
        assertThat(mappingList).size().isEqualTo(1);
        
        String fieldName = mappingList.get(0).getPropKey();
        assertThat(fieldName).isEqualTo(CustomFieldNameProperties.FIELD_NAME);
    }
    
    public static class IgnoredFieldProperties {
        @PropertyContainerFieldIgnore
        private String foo;
        private String bar;
    }
    
    public static class ParentClass {
        
        @PropertyContainerField(name = "foo")
        private NestedClass nestedClass;
        
        public static class NestedClass {
            
            @PropertyContainerField(name = "bar")
            private SubNestedClass subNestedClass;
            
            public static class SubNestedClass {
    
                public static final String EXPECTED_PROP_NAME = "foo.bar.baz";
                @PropertyContainerField(name = "baz")
                private String foo;
            }
        }
    }
    
    @Test
    @DisplayName("WHEN: Property container class is nested Property Container with fields annotated with @PropertyContainerField - THEN: Name is resolved properly")
    void nestedPropertyContainer() {
        loader.registerPropContainer(ParentClass.class);
        Collection<PropertyContainerWrapper> containerWrapperList = loader.getContainerWrapperList();
    
        assertThat(containerWrapperList).size().isEqualTo(1);
        List<PropertyMapping> mappingList = containerWrapperList.iterator().next().getMappingList();
    
        assertThat(mappingList).size().isEqualTo(1);
        String propKey = mappingList.get(0).getPropKey();
    
        assertThat(propKey).isEqualTo(SubNestedClass.EXPECTED_PROP_NAME);
    }
    
    @Test
    @DisplayName("WHEN: Property container field has @PropertyContainerFieldIgnore - THEN: Field is not mapped")
    void ignoredField() {
        Class<IgnoredFieldProperties> clazz = IgnoredFieldProperties.class;
        loader.registerPropContainer(clazz);
        Collection<PropertyContainerWrapper> wrapperList = loader.getContainerWrapperList();
        
        assertThat(wrapperList).size().isEqualTo(1);
        
        List<PropertyMapping> mappingList = wrapperList.iterator().next().getMappingList();
        assertThat(mappingList).size().isEqualTo(1);
        
        String fieldName = mappingList.get(0).getField().getName();
        assertThat(fieldName).isEqualTo("bar");
    }
}
