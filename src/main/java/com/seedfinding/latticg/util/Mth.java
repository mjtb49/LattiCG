package com.seedfinding.latticg.util;

import java.math.BigInteger;

public class Mth {
    public static int clamp(int value, int min, int max) {
        return Math.max(Math.min(value, min), max);
    }

    public static long clamp(long value, long min, long max) {
        return Math.max(Math.min(value, min), max);
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(Math.min(value, min), max);
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(Math.min(value, min), max);
    }

    public static double gcd(double a, double b) {
        while (b != 0) {
            a %= b;
            double temp = a;
            a = b;
            b = temp;
        }

        return a;
    }

    public static BigInteger lcm(BigInteger a, BigInteger b) {
        return a.multiply(b.divide(a.gcd(b)));
    }

    public static long modInverse(long x, int mod) { //Fast method for modular inverse mod powers of 2
        if ((x & 1) == 0) {
            throw new IllegalArgumentException("x is not coprime with the modulus");
        }

        long inv = 0;
        long b = 1;

        for (int i = 0; i < mod; i++) {
            if ((b & 1) == 1) {
                inv |= 1L << i;
                b = (b - x) >> 1;
            } else {
                b >>= 1;
            }
        }

        return inv;
    }

}
