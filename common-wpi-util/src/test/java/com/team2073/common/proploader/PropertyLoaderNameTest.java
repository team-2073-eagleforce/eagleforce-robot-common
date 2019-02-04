package com.team2073.common.proploader;

import com.team2073.common.proploader.PropertyLoaderNameTest.OuterClass.InnerClassProperties;
import com.team2073.common.proploader.model.PropertyContainer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

/**
 * @author Preston Briggs
 */
class PropertyLoaderNameTest extends PropertyLoaderBaseTest {

    private static class FooProperties {
        public static final String CLASS_NAME_WITHOUT_PROPERTIES = "Foo";
    }

    @Test
    @DisplayName("WHEN: Property container class name ends in 'Properties' - THEN: 'Properties' is not included in the resolved properties file name")
    void endsInProperties() {
        String resolvedName = PropertyLoaderHelper.resolvePropertyContainerName(new FooProperties());
        assertThat(resolvedName).isEqualTo(FooProperties.CLASS_NAME_WITHOUT_PROPERTIES);
    }

    private static class OnewordProperties {
        public static final String CLASS_NAME_WITHOUT_PROPERTIES = "Oneword";
    }

    @Test
    @DisplayName("WHEN: Property container class name has one word - THEN: Properties file name is resolved correctly")
    void oneWord() {
        String resolvedName = PropertyLoaderHelper.resolvePropertyContainerName(new OnewordProperties());
        assertThat(resolvedName).isEqualTo(OnewordProperties.CLASS_NAME_WITHOUT_PROPERTIES);
    }

    private static class FooMultiWordProperties {
        public static final String CLASS_NAME_WITHOUT_PROPERTIES = "FooMultiWord";
    }

    @Test
    @DisplayName("WHEN: Property container class name has multiple words - THEN: Properties file name is resolved correctly")
    void multipleWords() {
        String resolvedName = PropertyLoaderHelper.resolvePropertyContainerName(new FooMultiWordProperties());
        assertThat(resolvedName).isEqualTo(FooMultiWordProperties.CLASS_NAME_WITHOUT_PROPERTIES);
    }
    
    @PropertyContainer(name = AnnotatedWithNameAttributeProperties.NAME_ATTRIBUTE_VALUE)
    public static class AnnotatedWithNameAttributeProperties {
        public static final String NAME_ATTRIBUTE_VALUE = "fooBar-BAZ";
    }
    
    @Test
    @DisplayName("WHEN: Property container class is annotated with @PropertyContainer with name attribute - THEN: The name attribute is used")
    void nameAttributeExists() {
        String resolvedName = PropertyLoaderHelper.resolvePropertyContainerName(new AnnotatedWithNameAttributeProperties());
        assertThat(resolvedName).isEqualTo(AnnotatedWithNameAttributeProperties.NAME_ATTRIBUTE_VALUE);
    }
    
    @PropertyContainer(name = "")
    public static class AnnotatedWithEmptyNameAttributeProperties {
        public static final String NAME_ATTRIBUTE_VALUE = "AnnotatedWithEmptyNameAttribute";
    }
    
    @Test
    @DisplayName("WHEN: Property container class is annotated with @PropertyContainer with empty name attribute - THEN: The name attribute is not used")
    void nameAttributeEmpty() {
        String resolvedName = PropertyLoaderHelper.resolvePropertyContainerName(new AnnotatedWithEmptyNameAttributeProperties());
        assertThat(resolvedName).isEqualTo(AnnotatedWithEmptyNameAttributeProperties.NAME_ATTRIBUTE_VALUE);
    }
    
    @PropertyContainer
    public static class AnnotatedWithOutNameAttributeProperties {
        public static final String CLASS_NAME_WITHOUT_PROPERTIES = "AnnotatedWithOutNameAttribute";
    }

    @Test
    @DisplayName("WHEN: Property container class is annotated with @PropertyContainer without name attribute - THEN: The name attribute is not used")
    void nameAttributeNotExists() {
        String resolvedName = PropertyLoaderHelper.resolvePropertyContainerName(new AnnotatedWithOutNameAttributeProperties());
        assertThat(resolvedName).isEqualTo(AnnotatedWithOutNameAttributeProperties.CLASS_NAME_WITHOUT_PROPERTIES);
    }
    
    public static class OuterClass {
        public static class InnerClassProperties {
            public static final String CLASS_NAME_WITHOUT_PROPERTIES = "InnerClass";
        }
    }
    
    @Test
    @DisplayName("WHEN: Property container class is inner class - THEN: Name is resolved properly")
    void innerClass() {
        String resolvedName = PropertyLoaderHelper.resolvePropertyContainerName(new InnerClassProperties());
        assertThat(resolvedName).isEqualTo(InnerClassProperties.CLASS_NAME_WITHOUT_PROPERTIES);
    }

}