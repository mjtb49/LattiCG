package com.seedfinding.latticg.math.component;

import java.util.ArrayList;
import java.util.List;

public final class BigVectorUtil {
    private BigVectorUtil() {
    }

    public static BigVector fromString(String str) {
        str = str.trim();
        if (!str.startsWith("{") || !str.endsWith("}")) {
            throw new IllegalArgumentException("Illegal BigVector format");
        }

        List<BigFraction> fractions = new ArrayList<>();
        int fracStart = 1;
        for (int fracEnd = str.indexOf(',', fracStart); fracEnd >= 0; fracStart = fracEnd + 1, fracEnd = str.indexOf(',', fracStart)) {
            fractions.add(BigFractionUtil.fromString(str.substring(fracStart, fracEnd)));
        }
        fractions.add(BigFractionUtil.fromString(str.substring(fracStart, str.length() - 1)));

        return new BigVector(fractions.toArray(new BigFraction[0]));
    }
}
