package com.seedfinding.latticg.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MthTest {

    @Test
    public void testGcdDouble1() {
        assertEquals(1, Mth.gcd(1, 1), 0);
    }

    @Test
    public void testGcdDouble2() {
        assertEquals(23, Mth.gcd(69, 230), 0);
    }

    @Test
    public void testGcdDouble3() {
        assertEquals(23, Mth.gcd(230, 69), 0);
    }

    @Test
    public void testGcdDouble4() {
        assertEquals(1, Mth.gcd(123136693, 234799), 0);
    }

    @Test
    public void testGcdDouble5() {
        assertEquals(0.25, Mth.gcd(0.75, 1), 0);
    }

    @Test
    public void testModInverse0() {
        assertThrows(IllegalArgumentException.class, () -> Mth.modInverse(0, 16));
    }

    @Test
    public void testModInverse1() {
        assertEquals(1, Mth.modInverse(1, 16));
    }

    @Test
    public void testModInverse2() {
        assertThrows(IllegalArgumentException.class, () -> Mth.modInverse(2, 16));
    }

    @Test
    public void testModInverse5deece66d() {
        assertEquals(0xdfe05bcb1365L, Mth.modInverse(0x5deece66dL, 48));
    }

    @Test
    public void testModInverseDfe05bcb1365() {
        assertEquals(0x5deece66dL, Mth.modInverse(0xdfe05bcb1365L, 48));
    }

}
