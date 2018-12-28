package com.team2073.common.proploader;

import com.team2073.common.proploader.PropertyLoaderTestFixtures.SimpleProperties;
import com.team2073.common.proploader.model.PropertyContainerWrapper;
import com.team2073.common.wpitest.BaseWpiTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.*;

/**
 * @author Preston Briggs
 */
public class PropertyLoaderRegisterTest extends BaseWpiTest {
    
    private PropertyLoader loader = new PropertyLoader();

    @Test
    @DisplayName("WHEN: Property container class is registered - THEN: Class is registered properly")
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
    
    private static class PrivateProperties { }

    @Test
    @DisplayName("WHEN: Property container class is not public class - THEN: Exception is thrown")
    void nonPublicClass() {
        Class<?> clazz = PrivateProperties.class;
        assertThatIllegalArgumentException()
                .isThrownBy(() -> loader.registerPropContainer(clazz))
                .withMessageStartingWith("Property container objects must have a PUBLIC no-argument constructor. Offending class: ")
                .withMessageContaining(clazz.getSimpleName());
    }
    
    public static abstract class AbstractProperties { }

    @Test
    @DisplayName("WHEN: Property container class is abstract class - THEN: Exception is thrown")
    void abstractClass() {
        Class<?> clazz = AbstractProperties.class;
        assertThatIllegalArgumentException()
                .isThrownBy(() -> loader.registerPropContainer(clazz))
                .withMessageStartingWith("Property container objects must not be abstract. Offending class: ")
                .withMessageContaining(clazz.getSimpleName());
    }
    
    public interface InterfaceProperties { }

    @Test
    @DisplayName("WHEN: Property container class is interface - THEN: Exception is thrown")
    void interfaceClass() {
        Class<?> clazz = InterfaceProperties.class;
        assertThatIllegalArgumentException()
                .isThrownBy(() -> loader.registerPropContainer(clazz))
                .withMessageStartingWith("Property container objects must not be an interface. Offending class: ")
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
                .withMessageStartingWith("Property container objects must have a no-argument constructor. Offending class: ")
                .withMessageContaining(clazz.getSimpleName());
    }
    
    public static class PrivateConstructorProperties {
        public PrivateConstructorProperties() {}
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
