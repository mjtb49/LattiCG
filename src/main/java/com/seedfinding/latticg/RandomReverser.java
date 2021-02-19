package com.seedfinding.latticg;

import com.seedfinding.latticg.math.component.BigFraction;
import com.seedfinding.latticg.math.component.BigMatrix;
import com.seedfinding.latticg.math.component.BigVector;
import com.seedfinding.latticg.math.lattice.LLL.LLL;
import com.seedfinding.latticg.math.lattice.LLL.Params;
import com.seedfinding.latticg.math.lattice.LLL.Result;
import com.seedfinding.latticg.math.lattice.enumeration.Enumerate;
import com.seedfinding.latticg.util.LCG;
import com.seedfinding.latticg.util.Mth;
import com.seedfinding.latticg.util.Rand;
import org.jetbrains.annotations.ApiStatus;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.stream.LongStream;

@ApiStatus.Internal
public class RandomReverser {

    private static final BigInteger MOD = BigInteger.valueOf(281474976710656L);
    private static final BigInteger MULT = BigInteger.valueOf(25214903917L);

    private BigMatrix lattice;

    private final ArrayList<Long> mins;
    private final ArrayList<Long> maxes;
    private final ArrayList<Long> callIndices;
    private long currentCallIndex;
    private int dimensions;
    private boolean verbose;

    private double successChance;

    public RandomReverser() {
        verbose = false;
        dimensions = 0;
        mins = new ArrayList<>();
        maxes = new ArrayList<>();
        callIndices = new ArrayList<>();
        currentCallIndex = 0;
        successChance = 1.0;
    }

    //TODO Make this pick and choose which dimensions to use instead of using all of them
    public LongStream findAllValidSeeds() {
        if (dimensions == 0) {
            return LongStream.range(0, 1L << 48);
        }

        createLattice();
        BigVector lower = new BigVector(dimensions);
        BigVector upper = new BigVector(dimensions);
        BigVector offset = new BigVector(dimensions);
        Rand rand = Rand.ofInternalSeed(0L);

        for (int i = 0; i < dimensions; i++) {
            lower.set(i, new BigFraction(mins.get(i)));
            upper.set(i, new BigFraction(maxes.get(i)));
            offset.set(i, new BigFraction(rand.getSeed()));

            if (i != dimensions - 1) {
                rand.advance(callIndices.get(i + 1) - callIndices.get(i));
            }
        }

        if (verbose) {
            System.out.println("Mins: " + lower);
            System.out.println("Maxes: " + upper);
            System.out.println("Offsets: " + offset);
        }

        LCG r = LCG.JAVA.combine(-callIndices.get(0));

        if (successChance != 1.0)
            System.err.printf("Ignored approximately %.2e of all seeds %n", 1 - successChance);;

        return Enumerate.enumerate(lattice.transpose(), lower, upper, offset)
                .map(vec -> vec.get(0))
                .map(BigFraction::getNumerator)
                .mapToLong(BigInteger::longValue)
                .map(r::nextSeed);
    }

    private void createLattice() {
       if (verbose)
           System.out.println("Call Indices: " + callIndices);
       if (mins.size() != dimensions || maxes.size() != dimensions || callIndices.size() != dimensions){
           //TODO Bad Input What Do
           return;
       }

       BigInteger[] sideLengths =  new BigInteger[dimensions]; //The lengths of the sides of the cuboid in which our seeds must fall

       for (int i = 0; i < dimensions; i++) {
           sideLengths[i] = BigInteger.valueOf(maxes.get(i)-mins.get(i)+1);
       }

       BigInteger lcm = BigInteger.ONE;
       for (int i = 0; i < dimensions; i++) {
           lcm = Mth.lcm(lcm, sideLengths[i]);
       }

       BigMatrix scales = new BigMatrix(dimensions,dimensions);
       for (int i = 0; i < dimensions; i++) {
           for (int j = 0; j < dimensions; j++)
               scales.set(i,j,BigFraction.ZERO);
           scales.set(i,i,new BigFraction(lcm.divide(sideLengths[i])));
       }

       BigMatrix unscaledLattice = lattice;
       if (verbose)
           System.out.println("Looking for points on:\n"+unscaledLattice.toPrettyString());

       BigMatrix scaledLattice = unscaledLattice.multiply(scales);
       Params params = new Params().setDelta(Params.recommendedDelta).setDebug(false);
       if(verbose)
           System.out.println("Reducing:\n"+scaledLattice.toPrettyString());
      // BigMatrix transformations = BigMatrix.identityMatrix(dimensions);
       Result result = LLL.reduce(scaledLattice, params);
       //System.out.println("found:\n" + transformations.multiply(unscaledLattice).toPrettyString());
       if(verbose) {
           System.out.println("Found Reduced Scaled Basis:\n" + result.getReducedBasis().toPrettyString());
          // System.out.println("Found Reduced Basis:\n" + result.getTransformations().multiply(unscaledLattice).toPrettyString());
           System.out.println("Found Reduced Basis:\n" + result.getReducedBasis().multiply(scales.inverse()).toPrettyString());
       }
       //Matrix m = new Matrix.Factory().fromBigMatrix(result.multiply(scales.inverse()));
      // lattice = result.getTransformations().multiply(unscaledLattice);
        lattice = result.getReducedBasis().multiply(scales.inverse());
    }

    private void addMeasuredSeed(long min, long max) {
        //callIndices.set(dimensions, callIndices.get(dimensions)+1);
        mins.add(min);
        maxes.add(max);
        dimensions += 1;
        currentCallIndex += 1;
        callIndices.add(currentCallIndex);
        BigMatrix newLattice = new BigMatrix(dimensions+1,dimensions);
        if (dimensions != 1) {
            for (int row = 0; row < dimensions; row++)
                for (int col = 0; col < dimensions - 1; col++)
                    newLattice.set(row, col, lattice.get(row, col));
        }
        BigInteger tempMult = MULT.modPow(BigInteger.valueOf(callIndices.get(dimensions - 1) - callIndices.get(0)), MOD);
        newLattice.set(0,dimensions - 1, new BigFraction(tempMult));
        newLattice.set(dimensions, dimensions - 1, new BigFraction(MOD));
        lattice = newLattice;
    }

    private void addModuloMeasuredSeed(long min, long max, long mod) {
        long residue = (1L << 48) % mod; // (1L << 48) specific to Java LCG
        if (residue != 0) {
            successChance *= 1 - (double) residue / (double) (1L << 48);
            //First condition - is the seed real. This conveys more info than it seems since the normal mod vector not present.
            mins.add(0L);
            maxes.add((1L << 48) - residue); // in the case the seed is > (1L << 48) - residue, the do while in java's nextInt will trigger.
            currentCallIndex += 1;
            callIndices.add(currentCallIndex);
            //Second condition - does the seed have a number within the bounds in its residue class.
            mins.add(min);
            maxes.add(max);
            callIndices.add(currentCallIndex); //We don't increment the call index here because this is really 2 conditions on the same seed

            dimensions+=2; //We added 2 conditions

            BigMatrix newLattice = new BigMatrix(dimensions+1,dimensions);

            if (dimensions != 2) { //Copy the old lattice over
                for (int row = 0; row < dimensions - 1; row++)
                    for (int col = 0; col < dimensions - 2; col++)
                        newLattice.set(row, col, lattice.get(row, col));
            }

            BigInteger tempMult = MULT.modPow(BigInteger.valueOf(callIndices.get(dimensions - 1) - callIndices.get(0)), MOD);
            newLattice.set(0,dimensions - 2, new BigFraction(tempMult));
            newLattice.set(0,dimensions - 1, new BigFraction(tempMult));

            //vector capturing the effect of the modulo 2^48 operation on the residue class modulo mod
            newLattice.set(dimensions-1, dimensions - 1, new BigFraction(MOD));
            newLattice.set(dimensions-1, dimensions - 2, new BigFraction(MOD));

            //vector identifying everything in residue classes modulo mod
            newLattice.set(dimensions, dimensions - 1, new BigFraction(mod));

            //update the lattice.
            lattice = newLattice;
        } else {
            // the conditions are compatible so we can get away with just one new dimension. Caution should
            // be taken in case the this condition is the very first one in the lattice as other calls
            // may force upper bits
            mins.add(min);
            maxes.add(max);
            dimensions += 1;
            currentCallIndex += 1;
            callIndices.add(currentCallIndex);
            BigMatrix newLattice = new BigMatrix(dimensions+1,dimensions);
            if (dimensions != 1) {
                for (int row = 0; row < dimensions; row++)
                    for (int col = 0; col < dimensions - 1; col++)
                        newLattice.set(row, col, lattice.get(row, col));
            } else if (!MOD.equals(BigInteger.valueOf(mod))) {
                //TODO find a way to recover the seed in the case we only have constraints of this type
                System.err.println("First call not a bound on a seed. Junk output may be produced.");
            }
            //we might be able to use the provided modulus here instead
            BigInteger tempMult = MULT.modPow(BigInteger.valueOf(callIndices.get(dimensions - 1) - callIndices.get(0)), MOD);
            newLattice.set(0,dimensions - 1, new BigFraction(tempMult));
            newLattice.set(dimensions, dimensions - 1, new BigFraction(mod)); //Note this is not MOD.
            lattice = newLattice;
        }
    }

    public void addUnmeasuredSeeds(long numSeeds) {
        currentCallIndex += numSeeds;
        //callIndices.set(dimensions, callIndices.get(dimensions) + numSeeds);
    }

    public void addNextIntCall(int n, int min, int max) {
        if ((n & (-n)) == n) {// if n is a power of 2
            int log = Long.numberOfTrailingZeros(n);
            addMeasuredSeed(min * (1L << (48 - log)), (max+1) * (1L << (48 - log)) - 1);
        }
        else {
            addModuloMeasuredSeed(min*(1L << 17), (max*(1L << 17)) | 0x1ffff, n*(1L << 17));
        }
    }

    public void addNextIntCall(int min, int max) {
        addMeasuredSeed(min * (1L << (16)), (max+1) * (1L << (16)) - 1);
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
            addNextIntCall(2,1,1);
        } else {
            addNextIntCall(2,0,0);
        }
    }

    public void consumeNextBooleanCalls( int numCalls) {
        addUnmeasuredSeeds(numCalls);
    }

    /**
     * Add a constraint that min {@literal <} / {@literal <}= nextFloat() {@literal <} / {@literal <}= max, with strict inequalities when minInclusive or
     * maxInclusive are false, respectively.
     *
     * @param min low end of the valid range
     * @param max high end of the valid range
     * @param minInclusive true if the low end of the valid range should be inclusive
     * @param maxInclusive true if the high end of the valid range should be inclusive
     */
    public void addNextFloatCall(float min, float max, boolean minInclusive, boolean maxInclusive) {
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

        if (maxLong < minLong) {
            throw new IllegalArgumentException("call has no valid range");
        }

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
        //TODO warn about / check for the sign bit making a result wrong, 1/4th of results can be false positives in worst case
        boolean minSignBit = ((min & 0x8000_0000) != 0); //Would a long having value min run into a negative (int) cast
        boolean maxSignBit = ((max & 0x8000_0000) != 0); //Would a long having value max run into a negative (int) cast
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
        if (min >>> 32 == max >>> 32) { //Can we even talk about the second seed?
            addMeasuredSeed((min & 0xffff_ffffL) << 16, (((max & 0xffff_ffffL) + 1) << 16)-1);
        } else {
            addUnmeasuredSeeds(1);
        }

    }

    public void consumeNextLongCalls(int numCalls) {
        addUnmeasuredSeeds(2 * numCalls);
    }

    /**
     * Add a constraint that min {@literal <} / {@literal <}= nextDouble() {@literal <} / {@literal <}= max, with strict inequalities when minInclusive or
     * maxInclusive are false, respectively.
     *
     * @param min low end of the valid range
     * @param max high end of the valid range
     * @param minInclusive true if the low end of the valid range should be inclusive
     * @param maxInclusive true if the high end of the valid range should be inclusive
     */
    public void addNextDoubleCall(double min, double max, boolean minInclusive, boolean maxInclusive) {
        double minInc = min;
        double maxInc = max;

        if (!minInclusive) {
            minInc = Math.nextUp(min);
        }

        if (maxInclusive) {
            maxInc = Math.nextUp(max);
        }

        // inclusive
        long minLong = (long) StrictMath.ceil(minInc * 0x1.0p53);
        long maxLong = (long) StrictMath.ceil(maxInc * 0x1.0p53) - 1;

        if (maxLong < minLong) {
            throw new IllegalArgumentException("call has no valid range");
        }

        long minSeed1 = (minLong >> 27) << 22;
        long maxSeed1 = ((maxLong >> 27) << 22) | 0x3fffff;

        addMeasuredSeed(minSeed1, maxSeed1);

        //TODO this is not the only time we can speak about the second half. What if maxLong >>> 27 - minLong >>> 27 == 1
        if (minLong >>> 27 == maxLong >>> 27) { //Can we even say anything about the second half
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
        addUnmeasuredSeeds(2*numCalls);
    }

    /**
     * Add a constraint on the seed modulo something special. There is no handling for specific java Random calls
     * so to use this method requires investigating the implementation of the method one has periodic information on.
     * @param min low end, inclusive, of the valid range of seeds
     * @param max high end, inclusive, of the valid range of seeds
     * @param newMod the alternate modulus to use.
     */
    public void addModConstraint(long min, long max, long newMod) {
        addModuloMeasuredSeed(min,max,newMod);
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

}
