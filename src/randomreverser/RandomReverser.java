package randomreverser;

import randomreverser.math.component.*;
import randomreverser.util.LCG;
import randomreverser.util.Mth;
import randomreverser.math.lattice.LLL;
import randomreverser.math.lattice.Enumerate;
import randomreverser.util.Rand;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class RandomReverser {

    private static final BigInteger MOD = BigInteger.valueOf(281474976710656L);
    private static final BigInteger MULT = BigInteger.valueOf(25214903917L);

    private BigMatrix lattice;

    private ArrayList<Long> mins;
    private ArrayList<Long> maxes;
    private ArrayList<Integer> callIndices;
    private int currentCallIndex;
    private int dimensions;
    private boolean verbose;

    public RandomReverser() {
        verbose = false;
        dimensions = 0;
        mins = new ArrayList<>();
        maxes = new ArrayList<>();
        callIndices = new ArrayList<>();
        currentCallIndex = 0;
    }

    //TODO Make this pick and choose which dimensions to use instead of using all of them
    public ArrayList<Long> findAllValidSeeds() {
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

        ArrayList<Long> results = Enumerate.enumerate(lattice.transpose(), lower, upper, offset)
                .map(vec -> vec.get(0))
                .map(BigFraction::getNumerator)
                .map(BigInteger::longValue)
                .collect(Collectors.toCollection(ArrayList::new));

        if (verbose) {
            for (long seed : results) {
                System.out.println("found: " + r.nextSeed(seed));
            }
        }

        return results;
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

       BigMatrix unscaledLattice = new BigMatrix(dimensions,dimensions);
       for(int i = 0; i < dimensions; i++) {
           for (int j = 0; j < dimensions; j++)
               unscaledLattice.set(i,j, BigFraction.ZERO);
           if (i == 0) {
               unscaledLattice.set(0, i, BigFraction.ONE);
           } else {
               unscaledLattice.set(0, i, unscaledLattice.get(0, i-1));
               //for (int j = 0; j < callIndices.get(i); j ++) {
               BigInteger tempMult = MULT.modPow(BigInteger.valueOf(callIndices.get(i) - callIndices.get(0)), MOD);
               unscaledLattice.set(0, i, new BigFraction(tempMult));
               //}
               unscaledLattice.set(i, i, new BigFraction(MOD));
           }
       }
       BigMatrix scaledLattice = unscaledLattice.multiply(scales);
       LLL.Params params = new LLL.Params().setDelta(.99).setDebug(false);
       if(verbose)
           System.out.println("Reducing:\n"+scaledLattice.toPrettyString());
      // BigMatrix transformations = BigMatrix.identityMatrix(dimensions);
       LLL.Result result = LLL.reduce(scaledLattice, params);
       //System.out.println("found:\n" + transformations.multiply(unscaledLattice).toPrettyString());
       if(verbose) {
           System.out.println("Found Reduced Scaled Basis:\n" + result.getReducedBasis().toPrettyString());
           System.out.println("Found Reduced Basis:\n" + result.getTransformations().multiply(unscaledLattice).toPrettyString());
           //System.out.println("Found Reduced Basis:\n" + result.multiply(scales.inverse()).toPrettyString());
       }
       //Matrix m = new Matrix.Factory().fromBigMatrix(result.multiply(scales.inverse()));
       lattice = result.getTransformations().multiply(unscaledLattice);
    }

    private void addMeasuredSeed(long min, long max) {
        //callIndices.set(dimensions, callIndices.get(dimensions)+1);
        mins.add(min);
        maxes.add(max);
        dimensions += 1;
        currentCallIndex += 1;
        callIndices.add(currentCallIndex);
    }

    private void addUnmeasuredSeeds(int numSeeds) {
        currentCallIndex += numSeeds;
        //callIndices.set(dimensions, callIndices.get(dimensions) + numSeeds);
    }

    public void addNextIntCall(int n, int min, int max) {
        if ((n & (-n)) == n) {// if n is a power of 2
            int log = Long.numberOfTrailingZeros(n);
            addMeasuredSeed(min * (1L << (48 - log)), (max+1) * (1L << (48 - log)) - 1);
        }
        else {
            System.err.println("Reversal now has small chance of failure");
            //TODO
            consumeNextIntCalls(1);
        }
    }

    public void addNextIntCall(int min, int max) {
        addMeasuredSeed(min * (1L << (16)), (max+1) * (1L << (16)) - 1);
    }

    public void consumeNextIntCalls(int numCalls) {
        //TODO add handling for the potential of skipping a call in here
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

    public void addNextFloatCall(float min, float max) {
        long minSeed = ((long) (min  * (1 << 24))) << 24;
        long maxSeed = ((long) ((max * (1 << 24))) << 24) | 0xffffff;
        addMeasuredSeed(minSeed, maxSeed);
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

    public void addNextDoubleCall(double min, double max) {
        long minAsLong = (long) (min * (double) (1L << 53));
        long maxAsLong = (long) (max * (double) (1L << 53));
        addMeasuredSeed((minAsLong >>> 27) << 22, (((maxAsLong >>> 27) +1 )<<22) - 1);

        if (minAsLong >>> 27 == maxAsLong >>> 27) { //Can we even say anything about the second half
            addMeasuredSeed((minAsLong & 0x7ff_ffff) << 21, (((maxAsLong & 0x7ff_ffff)+1) << 21) - 1);
        } else {
            addUnmeasuredSeeds(1);
        }
    }

    public void consumeNextDoubleCalls(int numCalls) {
        addUnmeasuredSeeds(2*numCalls);
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

}
