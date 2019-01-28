package com.team2073.common.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MathUtilTest {
    @Test
    void degreeToRadian() {
        Assertions.assertEquals(MathUtil.degreesToRadians(45), Math.PI/4, .000001);
        Assertions.assertEquals(MathUtil.degreesToRadians(90), Math.PI/2, .000001);
        Assertions.assertEquals(MathUtil.degreesToRadians(135), 3*Math.PI/4, .000001);
    }

    @Test
    void radianToDegree() {
        Assertions.assertEquals(MathUtil.radiansToDegrees(Math.PI/4), 45, .000001);
        Assertions.assertEquals(MathUtil.radiansToDegrees(Math.PI/2), 90, .000001);
        Assertions.assertEquals(MathUtil.radiansToDegrees(3*Math.PI/4), 135, .000001);
    }

    @Test
    void degreeSine(){
        Assertions.assertEquals(MathUtil.degreeSine(45), Math.sqrt(2)/2, .000001);
        Assertions.assertEquals(MathUtil.degreeSine(30), 1./2., .000001);
        Assertions.assertEquals(MathUtil.degreeSine(120), Math.sqrt(3)/2, .000001);
    }

    @Test
    void degreeCosine(){
        Assertions.assertEquals(MathUtil.degreeCosine(45), Math.sqrt(2)/2, .000001);
        Assertions.assertEquals(MathUtil.degreeCosine(60), 1./2., .000001);
        Assertions.assertEquals(MathUtil.degreeCosine(120), -1./2., .000001);
    }

    @Test
    void degreeTangent(){
        Assertions.assertEquals(MathUtil.degreeTangent(45), 1., .000001);
        Assertions.assertEquals(MathUtil.degreeTangent(30), 1./Math.sqrt(3), .000001);
        Assertions.assertEquals(MathUtil.degreeTangent(120), -Math.sqrt(3), .000001);
    }
}