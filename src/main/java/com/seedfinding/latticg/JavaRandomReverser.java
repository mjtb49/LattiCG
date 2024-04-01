package com.seedfinding.latticg;

import com.seedfinding.latticg.math.component.BigFraction;
import com.seedfinding.latticg.math.component.BigMatrix;
import com.seedfinding.latticg.math.component.BigMatrixUtil;
import com.seedfinding.latticg.math.component.BigVector;
import com.seedfinding.latticg.math.lattice.LLL.LLL;
import com.seedfinding.latticg.math.lattice.LLL.Params;
import com.seedfinding.latticg.math.lattice.LLL.Result;
import com.seedfinding.latticg.math.lattice.enumeration.Enumerate;
import com.seedfinding.latticg.reversal.calltype.FilteredSkip;
import com.seedfinding.latticg.util.LCG;
import com.seedfinding.latticg.util.Mth;
import com.seedfinding.latticg.util.Rand;
import org.jetbrains.annotations.ApiStatus;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

@ApiStatus.Internal
public class JavaRandomReverser extends RandomReverser{

    @ApiStatus.Internal
    public JavaRandomReverser(List<FilteredSkip> filteredSkips) {
        super(LCG.JAVA, filteredSkips);
    }

    public void addNextIntCall(int n, int min, int max) {
        if (n <= 0) {
            throw new IllegalArgumentException(String.format("Bad bound for nextInt call can only be positive : %d", n));
        }

        // if (max < min || min < 0 || max >= n) {
        //     throw new IllegalArgumentException(String.format("Bounds should be 0<=min<=max<%d but were min: %d max: %d", n, min, max));
        // }

        if ((n & (-n)) == n) {// if n is a power of 2
            int log = Long.numberOfTrailingZeros(n);
            addMeasuredSeed(min * (1L << (48 - log)), max * (1L << (48 - log)) + (1L << (48 - log))  - 1);
        } else {
            addModuloMeasuredSeed(min * (1L << 17), (max * (1L << 17)) | 0x1ffff, n * (1L << 17));
        }
    }

    public void addNextIntCall(int min, int max) {
        // if (max < min) {
        //     throw new IndexOutOfBoundsException(String.format("Bounds should satisfy min<=max but were min: %d max: %d", min, max));
        // }
        addMeasuredSeed(min * (1L << (16)), max * (1L << 16) + (1L << 16) - 1);
    }

    public void consumeNextIntCalls(int numCalls, int bound) {
        long residue = (1L << 48) % ((1L << 17) * bound);
        if (residue != 0) {
            successChance *= Math.pow(1 - (double) residue / (double) (1L << 48), numCalls);
        }
        addUnmeasuredSeeds(numCalls);
    }

    public void addNextBooleanCall(boolean value) {
        if (value) {
            addNextIntCall(2, 1, 1);
        } else {
            addNextIntCall(2, 0, 0);
        }
    }

    public void consumeNextBooleanCalls(int numCalls) {
        addUnmeasuredSeeds(numCalls);
    }

    /**
     * Add a constraint that min {@literal <} / {@literal <}= nextFloat() {@literal <} / {@literal <}= max, with strict inequalities when minInclusive or
     * maxInclusive are false, respectively.
     * As per the Java Random documentation min {@literal >}= 0 and max {@literal <} 1
     *
     * @param min          low end of the valid range
     * @param max          high end of the valid range
     * @param minInclusive true if the low end of the valid range should be inclusive
     * @param maxInclusive true if the high end of the valid range should be inclusive
     */
    public void addNextFloatCall(float min, float max, boolean minInclusive, boolean maxInclusive) {
        // I think these constraints are unremoveable due to float precision worries.
        if (min < 0.0f || max < 0.0f || min > 1.0f || max > 1.0f) {
            throw new IllegalArgumentException(String.format("Bounds should have 0 <= min, max <=1 but were min: %f " +
                "max: %f with min included : %s and max included %s", min, max, minInclusive, maxInclusive));
        }
        float minInc = min;
        float maxInc = max;

        if (!minInclusive) {
            minInc = Math.nextUp(min);
        }

        if (maxInclusive) {
            maxInc = Math.nextUp(max);
        }

        // inclusive
        long minLong = (long) StrictMath.ceil(minInc * 0x1.0p24f);
        long maxLong = (long) StrictMath.ceil(maxInc * 0x1.0p24f) - 1;

        // if (maxLong < minLong) {
        //     throw new IllegalArgumentException("call has no valid range");
        // }

        long minSeed = minLong << 24;
        long maxSeed = (maxLong << 24) | 0xffffff;

        addMeasuredSeed(minSeed, maxSeed);
    }

    /**
     * Add a constraint that min {@literal <}= nextFloat() {@literal <} max.
     *
     * @param min low end of the valid range
     * @param max high end of the valid range
     */
    public void addNextFloatCall(float min, float max) {
        addNextFloatCall(min, max, true, false);
    }

    public void consumeNextFloatCalls(int numCalls) {
        addUnmeasuredSeeds(numCalls);
    }

    public void addNextLongCall(long min, long max) {
        if (max + 1 == min) { // escape the divide by zero on sides later
            throw new IllegalArgumentException("nextLong bounds give no actual constraint");
        }
        //TODO warn about / check for edge cases of large intervals being ruled out by the bottom bits (if later if statement fails)
        boolean minSignBit = ((min & 0x8000_0000L) != 0); //Would a long having value min run into a negative (int) cast
        boolean maxSignBit = ((max & 0x8000_0000L) != 0); //Would a long having value max run into a negative (int) cast
        long minFirstSeed, maxFirstSeed;

        if (minSignBit) {
            minFirstSeed = ((min >>> 32) + 1) << 16;
        } else {
            minFirstSeed = (min >>> 32) << 16;
        }

        if (maxSignBit) {
            maxFirstSeed = (((max >>> 32) + 2) << 16) - 1;
        } else {
            maxFirstSeed = (((max >>> 32) + 1) << 16) - 1;
        }
        addMeasuredSeed(minFirstSeed, maxFirstSeed);

        if (max-min < 1L<<32 && 0 <= max-min) { //Can we even talk about the second seed?
            addMeasuredSeed((min & 0xffff_ffffL) << 16, (((max & 0xffff_ffffL) + 1) << 16) - 1);
        } else {
            addUnmeasuredSeeds(1);
        }
    }

    public void consumeNextLongCalls(int numCalls) {
        addUnmeasuredSeeds(2L * numCalls);
    }

    /**
     * Add a constraint that min {@literal <} / {@literal <}= nextDouble() {@literal <} / {@literal <}= max, with strict inequalities when minInclusive or
     * maxInclusive are false, respectively.
     *
     * @param min          low end of the valid range
     * @param max          high end of the valid range
     * @param minInclusive true if the low end of the valid range should be inclusive
     * @param maxInclusive true if the high end of the valid range should be inclusive
     */
    public void addNextDoubleCall(double min, double max, boolean minInclusive, boolean maxInclusive) {
        // I think these constraints are unremoveable due to float precision worries.
        if (min < 0.0d || max < 0.0d || min > 1.0d || max > 1.0d) {
            throw new IllegalArgumentException(String.format("Bounds should have 0 <= min, max <=1 but were min: %f " +
                "max: %f with min included : %s and max included %s", min, max, minInclusive, maxInclusive));
        }
        double minInc = min;
        double maxInc = max;
        //TODO warn about / check for edge cases of large intervals being ruled out by the bottom bits (if later if statement fails)
        if (!minInclusive) {
            minInc = Math.nextUp(min);
        }

        if (maxInclusive) {
            maxInc = Math.nextUp(max);
        }

        // inclusive
        long minLong = (long) StrictMath.ceil(minInc * 0x1.0p53);
        long maxLong = (long) StrictMath.ceil(maxInc * 0x1.0p53) - 1;

        // if (maxLong < minLong) {
        //     throw new IllegalArgumentException("call has no valid range");
        // }

        long minSeed1 = (minLong >> 27) << 22;
        long maxSeed1 = ((maxLong >> 27) << 22) | 0x3fffff;

        addMeasuredSeed(minSeed1, maxSeed1);

        if (((maxLong - minLong) % (1L << 53)) < (1L << 27)) { //Can we even say anything about the second half?
            long minSeed2 = (minLong & 0x7ffffff) << 21;
            long maxSeed2 = ((maxLong & 0x7ffffff) << 21) | 0x1fffff;

            addMeasuredSeed(minSeed2, maxSeed2);
        } else {
            addUnmeasuredSeeds(1);
        }
    }

    /**
     * Add a constraint that min {@literal <}= nextDouble() {@literal <} max.
     *
     * @param min low end of the valid range
     * @param max high end of the valid range
     */
    public void addNextDoubleCall(double min, double max) {
        addNextDoubleCall(min, max, true, false);
    }

    public void consumeNextDoubleCalls(int numCalls) {
        addUnmeasuredSeeds(2L * numCalls);
    }

    /**
     * Add a constraint on the seed modulo something special. There is no handling for specific java Random calls
     * so to use this method requires investigating the implementation of the method one has periodic information on.
     *
     * @param min    low end, inclusive, of the valid range of seeds
     * @param max    high end, inclusive, of the valid range of seeds
     * @param newMod the alternate modulus to use.
     */
    public void addModConstraint(long min, long max, long newMod) {
        addModuloMeasuredSeed(min, max, newMod);
    }




}
