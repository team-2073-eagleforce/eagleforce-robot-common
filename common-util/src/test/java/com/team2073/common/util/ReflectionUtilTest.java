package com.team2073.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.team2073.common.util.ReflectionUtil.getPrimitiveType;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * @author Lana Wong
 */
public class ReflectionUtilTest {

    @Test
    @DisplayName("WHEN: Passing wrapper type Double into getPrimitiveType method  - THEN: Return a PrimitiveType.DOUBLE")
    void returnPrimitiveWrapperDouble() {
        ReflectionUtil.PrimitiveType type = getPrimitiveType(Double.class);
        System.out.print(type);
        assertThat(type).isEqualTo(ReflectionUtil.PrimitiveType.DOUBLE);
    }

    @Test
    @DisplayName("WHEN: Passing wrapper type Integer into getPrimitiveType method  - THEN: Return a PrimitiveType.INTEGER")
    void returnPrimitiveWrapperInteger() {
        ReflectionUtil.PrimitiveType type = getPrimitiveType(Integer.class);
        assertThat(type).isEqualTo(ReflectionUtil.PrimitiveType.INTEGER);
    }

    @Test
    @DisplayName("WHEN: Passing wrapper type Long into getPrimitiveType method  - THEN: Return a PrimitiveType.LONG")
    void returnPrimitiveWrapperLong() {
        ReflectionUtil.PrimitiveType type = getPrimitiveType(Long.class);
        assertThat(type).isEqualTo(ReflectionUtil.PrimitiveType.LONG);
    }

    @Test
    @DisplayName("WHEN: Passing wrapper type Float into getPrimitiveType method - THEN: Return a PrimitiveType.FLOAT")
    void returnPrimitiveWrapperFloat(){
        ReflectionUtil.PrimitiveType type = getPrimitiveType(Float.class);
        assertThat(type).isEqualTo(ReflectionUtil.PrimitiveType.FLOAT);
    }

    @Test
    @DisplayName("WHEN: Passing wrapper type String into getPrimitiveType method - THEN: Returns a PrimitiveType.STRING")
    void returnPrimitiveWrapperString(){
        ReflectionUtil.PrimitiveType type = getPrimitiveType(String.class);
        assertThat(type).isEqualTo(ReflectionUtil.PrimitiveType.STRING);
    }

    @Test
    @DisplayName("WHEN:countingStringAsPrimitive = false - THEN: Return null")
    void returnNullIfStringNotPrimitive(){
        ReflectionUtil.PrimitiveType type = getPrimitiveType(String.class, false);
        System.out.print(type);
        assertThat(type).isNull();
    }

    @Test
    @DisplayName("WHEN: null passed in to getPrimitiveType method - THEN: throws exception")
    void returnNull(){
        Class<?> clazz = null;
        assertThatIllegalArgumentException().isThrownBy(() -> getPrimitiveType(clazz));
    }

    @Test
    @DisplayName("WHEN: Passing wrapper type Byte into getPrimitiveType method  - THEN: Return a PrimitiveType.BYTE")
    void returnPrimitiveWrapperByte(){
        ReflectionUtil.PrimitiveType type = getPrimitiveType(Byte.class);
        assertThat(type).isEqualTo(ReflectionUtil.PrimitiveType.BYTE);
    }

    @Test
    @DisplayName("WHEN: Passing wrapper type Character into getPrimitiveType method  - THEN: Return a PrimitiveType.CHARACTER")
    void returnPrimitiveWrapperChar(){
        ReflectionUtil.PrimitiveType type = getPrimitiveType(Character.class);
        assertThat(type).isEqualTo(ReflectionUtil.PrimitiveType.CHARACTER);
    }

    @Test
    @DisplayName("WHEN: Passing wrapper type Short into getPrimitiveType method  - THEN: Return a PrimitiveType.SHORT")
    void returnPrimitiveWrapperShort(){
        ReflectionUtil.PrimitiveType type = getPrimitiveType(Short.class);
        assertThat(type).isEqualTo(ReflectionUtil.PrimitiveType.SHORT);
    }

    @Test
    @DisplayName("WHEN: Passing double into getPrimitiveType method  - THEN: Return a PrimitiveType.DOUBLE")
    void returnPrimitiveDouble() {
        ReflectionUtil.PrimitiveType type = getPrimitiveType(double.class);
        assertThat(type).isEqualTo(ReflectionUtil.PrimitiveType.DOUBLE);
    }

    @Test
    @DisplayName("WHEN: Passing integer into getPrimitiveType method  - THEN: Return a PrimitiveType.INTEGER")
    void returnPrimitiveInteger() {
        ReflectionUtil.PrimitiveType type = getPrimitiveType(int.class);
        assertThat(type).isEqualTo(ReflectionUtil.PrimitiveType.INTEGER);
    }

    @Test
    @DisplayName("WHEN: Passing long into getPrimitiveType method  - THEN: Return a PrimitiveType.LONG")
    void returnPrimitiveLong() {
        ReflectionUtil.PrimitiveType type = getPrimitiveType(long.class);
        assertThat(type).isEqualTo(ReflectionUtil.PrimitiveType.LONG);
    }

    @Test
    @DisplayName("WHEN: Passing float into getPrimitiveType method - THEN: Return a PrimitiveType.FLOAT")
    void returnPrimitiveFloat(){
        ReflectionUtil.PrimitiveType type = getPrimitiveType(float.class);
        assertThat(type).isEqualTo(ReflectionUtil.PrimitiveType.FLOAT);
    }

    @Test
    @DisplayName("WHEN: Passing byte into getPrimitiveType method  - THEN: Return a PrimitiveType.BYTE")
    void returnPrimitiveByte(){
        ReflectionUtil.PrimitiveType type = getPrimitiveType(byte.class);
        assertThat(type).isEqualTo(ReflectionUtil.PrimitiveType.BYTE);
    }

    @Test
    @DisplayName("WHEN: Passing char into getPrimitiveType method  - THEN: Return a PrimitiveType.CHARACTER")
    void returnPrimitiveChar(){
        ReflectionUtil.PrimitiveType type = getPrimitiveType(char.class);
        assertThat(type).isEqualTo(ReflectionUtil.PrimitiveType.CHARACTER);
    }

    @Test
    @DisplayName("WHEN: Passing short into getPrimitiveType method  - THEN: Return a PrimitiveType.SHORT")
    void returnPrimitiveShort(){
        ReflectionUtil.PrimitiveType type = getPrimitiveType(short.class);
        assertThat(type).isEqualTo(ReflectionUtil.PrimitiveType.SHORT);
    }
}
