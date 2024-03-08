package com.seedfinding.latticg.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LCGTest {

    private static final long JAVA_MODULUS = LCG.JAVA.modulus;

    @Test
    public void testJavaCombineLCG() {
        assertEquals(new LCG(0xBB20B4600A69L, 0x40942DE6BAL, JAVA_MODULUS), LCG.JAVA.combine(LCG.JAVA));
    }

    @Test
    public void testJavaCombine0() {
        assertEquals(new LCG(1, 0, JAVA_MODULUS), LCG.JAVA.combine(0));
    }

    @Test
    public void testJavaCombine1() {
        assertEquals(LCG.JAVA, LCG.JAVA.combine(1));
    }

    @Test
    public void testJavaCombine2() {
        assertEquals(new LCG(0xBB20B4600A69L, 0x40942DE6BAL, JAVA_MODULUS), LCG.JAVA.combine(2));
    }

    @Test
    public void testJavaCombineM1() {
        assertEquals(new LCG(0xDFE05BCB1365L, 0x615C0E462AA9L, JAVA_MODULUS), LCG.JAVA.combine(-1));
    }

    @Test
    public void testJavaCombine1000000() {
        assertEquals(new LCG(0x84C8B05AB101L, 0x879086B10040L, JAVA_MODULUS), LCG.JAVA.combine(1000000));
    }

    @Test
    public void testJavaInvert() {
        assertEquals(LCG.JAVA.combine(-1), LCG.JAVA.invert());
    }

}
