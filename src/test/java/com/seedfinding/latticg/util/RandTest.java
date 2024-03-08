package com.seedfinding.latticg.util;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RandTest {

    @Test
    public void testNextInt() {
        assertEquals(new Random(47).nextInt(), Rand.ofSeedScrambled(47).nextInt());
    }

    @Test
    public void testNextIntPowerOf2() {
        assertEquals(new Random(462746238476L).nextInt(1024), Rand.ofSeedScrambled(462746238476L).nextInt(1024));
    }

    @Test
    public void testNextIntNonPowerOf2() {
        assertEquals(new Random(0xcafebabe).nextInt(100000), Rand.ofSeedScrambled(0xcafebabe).nextInt(100000));
    }

    @Test
    public void testNextFloat() {
        assertEquals(new Random(0x53ee71ebe11eL).nextFloat(), Rand.ofSeedScrambled(0x53ee71ebe11eL).nextFloat(), 0);
    }

    @Test
    public void testNextDouble() {
        assertEquals(new Random(98765L).nextDouble(), Rand.ofSeedScrambled(98765L).nextDouble(), 0);
    }

    @Test
    public void testNextLong() {
        assertEquals(new Random(12345).nextLong(), Rand.ofSeedScrambled(12345).nextLong());
    }

    @Test
    public void testNextBoolean() {
        // just so it doesn't pass by chance
        for (long seed = 0; seed < 48; seed++) {
            assertEquals(new Random(seed * Integer.MAX_VALUE).nextBoolean(), Rand.ofSeedScrambled(seed * Integer.MAX_VALUE).nextBoolean());
        }
    }

}
