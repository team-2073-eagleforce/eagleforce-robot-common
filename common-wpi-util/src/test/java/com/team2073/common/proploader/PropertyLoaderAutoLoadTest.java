package com.team2073.common.proploader;

import com.team2073.common.proploader.autoload.test1.BarProperties;
import com.team2073.common.proploader.autoload.test1.FooProperties;
import com.team2073.common.proploader.autoload.test1.RootAutoLoad1Class;
import com.team2073.common.proploader.autoload.test2.OuterClass.InnerProperties;
import com.team2073.common.proploader.autoload.test2.RootAutoLoad2Class;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;

/**
 * @author Preston Briggs
 */
public class PropertyLoaderAutoLoadTest extends PropertyLoaderBaseTest {
    
    @Test
    @DisplayName("WHEN: Auto registering all @PropertyContainer classes - THEN: Expected classes are registered")
    void autoRegister() {
        Class<?> clazz = RootAutoLoad1Class.class;
        
        loader.autoRegisterAllPropContainers(clazz);
        List<Class<?>> classList = loader.getContainerWrapperList().stream().map(it -> it.getPropContainerClass()).collect(Collectors.toList());
        
        assertThat(classList).containsExactlyInAnyOrder(FooProperties.class, BarProperties.class);
    }
    
    @Test
    @DisplayName("WHEN: Auto registering static nested class - THEN: Expected classes are registered")
    void interfaceClass() {
        Class<?> clazz = RootAutoLoad2Class.class;
        
        loader.autoRegisterAllPropContainers(clazz);
        List<Class<?>> classList = loader.getContainerWrapperList().stream().map(it -> it.getPropContainerClass()).collect(Collectors.toList());
        
        assertThat(classList).containsExactlyInAnyOrder(InnerProperties.class);
    }
    
}
