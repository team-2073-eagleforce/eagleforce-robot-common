package com.team2073.common.util;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author pbriggs
 */
public class EnumUtil {

    public static void throwUnknownValueException(Enum<?> value) {
        throwUnknownValueException(value, false);
    }

    public static void throwUnknownValueExceptionWithValues(Enum<?> value) {
        throwUnknownValueException(value, true);
    }

    public static void throwUnknownValueException(Enum<?> value, boolean printValidValues) {
        String msg = String.format("Unknown enum: [%s].", value);
        if (printValidValues) {
            msg += String.format(" Valid values: [%s].", validValues(value));
        }
        throw new UnknownEnumValueException(msg);
    }

    public static String validValues(Enum<?> value) {
        return validValues(value.getDeclaringClass());
    }

    public static String validValues(Class<? extends Enum<?>> enumClass) {

        return Stream.of(enumClass.getEnumConstants()) //
                .map(e -> e.toString()) //
                .collect(Collectors.joining(", "));
    }

    public static class UnknownEnumValueException extends RuntimeException {
        public UnknownEnumValueException(String message) {
            super(message);
        }
    }
}
