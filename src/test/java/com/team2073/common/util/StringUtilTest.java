package com.team2073.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilTest {

    @Test
    public void passInStringShouldReturnFalse(){
        assertEquals(false, StringUtil.isEmpty("Hello"), "A full string must be not empty");
    }

    @Test
    public void passInUpperCaseShouldReturnLowerCase(){
        assertEquals("Hello", StringUtil.toFileCase("Hello"), "A file name cannot be uppercase");
    }

}