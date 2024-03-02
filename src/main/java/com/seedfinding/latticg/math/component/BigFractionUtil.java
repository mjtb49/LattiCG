package com.seedfinding.latticg.math.component;

import java.math.BigInteger;

public final class BigFractionUtil {
    private BigFractionUtil() {
    }

    public static BigFraction fromString(String str) {
        str = str.trim();
        int slashIndex = str.indexOf('/');
        if (slashIndex == -1) {
            return new BigFraction(new BigInteger(str));
        } else {
            String numerator = str.substring(0, slashIndex).trim();
            String denominator = str.substring(slashIndex + 1).trim();
            return new BigFraction(new BigInteger(numerator), new BigInteger(denominator));
        }
    }
}
