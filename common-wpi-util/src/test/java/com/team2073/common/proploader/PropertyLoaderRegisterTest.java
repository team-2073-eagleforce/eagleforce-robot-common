package com.team2073.common.proploader;

import com.team2073.common.proploader.PropertyLoaderRegisterTest.OuterClass.InnerProperties;
import com.team2073.common.proploader.PropertyLoaderTestFixtures.SimpleProperties;
import com.team2073.common.proploader.model.PropertyContainerWrapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * @author Preston Briggs
 */
public class PropertyLoaderRegisterTest extends PropertyLoaderBaseTest {
    
    @Test
    @DisplayName("WHEN: Property container class is registered - THEN: Class is registered properly (smoke test)")
    void smokeTest() {
        SimpleProperties expectedContainer = loader.registerPropContainer(SimpleProperties.class);
    
        Collection<PropertyContainerWrapper> wrapperList = loader.getContainerWrapperList();
        assertThat(wrapperList).size().isEqualTo(1);
    
        Object actualContainer = wrapperList.iterator().next().getPropContainer();
        assertThat(actualContainer).isEqualTo(expectedContainer);
    }

    @Test
    @DisplayName("WHEN: Property container class is registered multiple times - THEN: Only one instance is registered")
    void multipleTimes() {
        loader.registerPropContainer(SimpleProperties.class);
        loader.registerPropContainer(SimpleProperties.class);
    
        Collection<PropertyContainerWrapper> wrapperList = loader.getContainerWrapperList();
        assertThat(wrapperList).size().isEqualTo(1);
    }
    
    @Test
    @DisplayName("WHEN: Property container class is registered and then requested - THEN: Property container object is retrieved properly")
    void retrieve() {
        Class<SimpleProperties> clazz = SimpleProperties.class;
        SimpleProperties expectedContainer = loader.registerPropContainer(clazz);
        Optional<SimpleProperties> propertyContainer = loader.getPropertyContainer(clazz);
    
        assertThat(propertyContainer).hasValue(expectedContainer);
    }
    
    private static class PrivateProperties { }

    @Test
    @DisplayName("WHEN: Property container class is not public class - THEN: Exception is thrown")
    void nonPublicClass() {
        Class<?> clazz = PrivateProperties.class;
        assertThatIllegalArgumentException()
                .isThrownBy(() -> loader.registerPropContainer(clazz))
                .withMessageStartingWith("Property container classes must be public classes. Offending class: ")
                .withMessageContaining(clazz.getSimpleName());
    }
    
    public static abstract class AbstractProperties { }

    @Test
    @DisplayName("WHEN: Property container class is abstract class - THEN: Exception is thrown")
    void abstractClass() {
        Class<?> clazz = AbstractProperties.class;
        assertThatIllegalArgumentException()
                .isThrownBy(() -> loader.registerPropContainer(clazz))
                .withMessageStartingWith("Property container classes must not be abstract. Offending class: ")
                .withMessageContaining(clazz.getSimpleName());
    }
    
    public interface InterfaceProperties { }
    
    @Test
    @DisplayName("WHEN: Property container class is interface - THEN: Exception is thrown")
    void interfaceClass() {
        Class<?> clazz = InterfaceProperties.class;
        assertThatIllegalArgumentException()
                .isThrownBy(() -> loader.registerPropContainer(clazz))
                .withMessageStartingWith("Property container classes must not be an interface. Offending class: ")
                .withMessageContaining(clazz.getSimpleName());
    }
    
    public static class OuterClass {
        public class InnerProperties { }
    }
    
    @Test
    @DisplayName("WHEN: Property container class is non-static inner class - THEN: Exception is thrown")
    void innerClass() {
        Class<?> clazz = InnerProperties.class;
        assertThatIllegalArgumentException()
                .isThrownBy(() -> loader.registerPropContainer(clazz))
                .withMessageStartingWith("Property container classes must not be a NON-STATIC inner class")
                .withMessageContaining(clazz.getSimpleName());
    }
    
    public static class NoArgProperties {
        public NoArgProperties(String foo) {}
    }

    @Test
    @DisplayName("WHEN: Property container class does not have no-arg constructor - THEN: Exception is thrown")
    void missingNoArgConstructor() {
        Class<?> clazz = NoArgProperties.class;
        assertThatIllegalArgumentException()
                .isThrownBy(() -> loader.registerPropContainer(clazz))
                .withMessageStartingWith("Property container classes must have a no-argument constructor. Offending class: ")
                .withMessageContaining(clazz.getSimpleName());
    }
    
    public static class PrivateConstructorProperties {
        private PrivateConstructorProperties() {}
    }

    @Test
    @DisplayName("WHEN: Property container class has private no-arg constructor - THEN: Class is still registered properly")
    void privateNoArgConstructor() {
        Class<?> clazz = PrivateConstructorProperties.class;
        assertThatCode(() -> loader.registerPropContainer(clazz)).doesNotThrowAnyException();
        assertThat(loader.getContainerWrapperList()).size().isEqualTo(1);
    }
    
    public static class ExceptionThrowingProperties {
        public ExceptionThrowingProperties() {
            throw new RuntimeException("Exception thrown from constructor");
        }
    }

    @Test
    @DisplayName("WHEN: Property container class throws exception in constructor - THEN: Exception is thrown")
    void constructorException() {
        Class<?> clazz = ExceptionThrowingProperties.class;
        assertThatIllegalStateException()
                .isThrownBy(() -> loader.registerPropContainer(clazz))
                .withMessageStartingWith("Property container object threw an exception during construction. Offending class: ")
                .withMessageContaining(clazz.getSimpleName());
    }
}
