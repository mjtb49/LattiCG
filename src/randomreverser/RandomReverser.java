package randomreverser;

import randomreverser.util.LCG;
import randomreverser.util.Mth;
import randomreverser.math.component.BigMatrix;
import randomreverser.math.component.BigVector;
import randomreverser.math.component.Matrix;
import randomreverser.math.component.Vector;
import randomreverser.math.lattice.LLL;
import randomreverser.math.lattice.Enumerate;
import randomreverser.util.Rand;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

public class RandomReverser {

    private static final BigDecimal MOD = new BigDecimal(281474976710656L);
    private static final BigDecimal MULT = new BigDecimal(25214903917L);
    private static final BigDecimal ONE = new BigDecimal(1);
    private static final BigDecimal ZERO = new BigDecimal(0);

    private ArrayList<Long> mins;
    private ArrayList<Long> maxes;
    private ArrayList<Integer> gaps;
    private int dimensions;
    private boolean verbose;

    public RandomReverser() {
        verbose = false;
        dimensions = 0;
        mins = new ArrayList<>();
        maxes = new ArrayList<>();
        gaps = new ArrayList<>();
        gaps.add(0);
    }

    //TODO Make this pick and choose which dimensions to use instead of using all of them
    public ArrayList<Long> findAllValidSeeds() {
        if (verbose)
            System.out.println("Gaps: " + gaps);
        if (mins.size() != dimensions || maxes.size() != dimensions || gaps.size() -1 != dimensions){
            //TODO Bad Input What Do
            return null;
        }

        BigVector sideLengths =  new BigVector(dimensions);
        for (int i = 0; i < dimensions; i++) {
            sideLengths.set(i,new BigDecimal(maxes.get(i)-mins.get(i)+1));
        }
        BigDecimal lcm = ONE;
        for (int i = 0; i < dimensions; i++) {
            lcm = sideLengths.get(i).multiply(lcm).divide(Mth.gcd(lcm, sideLengths.get(i)));
        }

        BigMatrix scales = new BigMatrix(dimensions,dimensions);
        for (int i = 0; i < dimensions; i++) {
            for (int j = 0; j < dimensions; j++)
                scales.set(i,j,ZERO);
            scales.set(i,i,lcm.divide(sideLengths.get(i)));
            // Hacky solution: scales.set(i,i,ONE);
        }

        BigMatrix unscaledLattice = new BigMatrix(dimensions,dimensions);

        for(int i = 0; i < dimensions; i++) {
            for (int j = 0; j < dimensions; j++)
                unscaledLattice.set(i,j, ZERO);

            if (i == 0) {
                unscaledLattice.set(0, i, ONE);
            } else {
                unscaledLattice.set(0, i, unscaledLattice.get(0, i-1));
                //for (int j = 0; j < gaps.get(i); j ++) {
                BigInteger tempMult = MULT.toBigInteger().modPow(BigInteger.valueOf(gaps.get(i)), MOD.toBigInteger());
                unscaledLattice.set(0, i, unscaledLattice.get(0,i).multiply(new BigDecimal(tempMult)).remainder(MOD));
                //}
                unscaledLattice.set(i, i, MOD);
            }

        }

        BigMatrix scaledLattice = unscaledLattice.multiply(scales);

        LLL.Params params = new LLL.Params().setDelta(.99).setDebug(false);
        if(verbose)
            System.out.println("Reducing:\n"+scaledLattice.toPrettyString());

        BigMatrix transformations = new BigMatrix.Factory().identityMatrix(dimensions);
        BigMatrix result = LLL.reduce(scaledLattice, params, transformations);
        //System.out.println("found:\n" + transformations.multiply(unscaledLattice).toPrettyString());

        if(verbose) {
            System.out.println("Found Reduced Scaled Basis:\n" + result.toPrettyString());
            System.out.println("Found Reduced Basis:\n" + transformations.multiply(unscaledLattice).toPrettyString());
            //System.out.println("Found Reduced Basis:\n" + result.multiply(scales.inverse()).toPrettyString());
        }
        //Matrix m = new Matrix.Factory().fromBigMatrix(result.multiply(scales.inverse()));
        Matrix m = new Matrix.Factory().fromBigMatrix(transformations.multiply(unscaledLattice));
        Vector vecMins = new Vector(dimensions);
        Vector vecMaxes = new Vector(dimensions);
        Vector offsets = new Vector(dimensions);
        Rand rand = Rand.ofInternalSeed(0);
        for (int i = 0; i < dimensions; i++) {
            vecMins.set(i, (double) mins.get(i));
            vecMaxes.set(i, (double) maxes.get(i));
            offsets.set(i,rand.getSeed());
            if (i != dimensions-1)
                rand.advance(gaps.get(i+1));
        }
        if (verbose) {
            System.out.println("Mins: "+vecMins);
            System.out.println("Maxes: "+vecMaxes);
            System.out.println("Offsets: "+offsets);
        }
        LCG r = LCG.JAVA.combine(-gaps.get(0));

        ArrayList<Long> results = new ArrayList<>();
        for (Vector n : Enumerate.enumerate(dimensions, vecMins, vecMaxes, m, offsets)) {
            if(verbose)
                System.out.println("Found: " + r.nextSeed((long) m.getColumn(0).dot(n))+" at "+n);
            results.add(r.nextSeed((long) m.getColumn(0).dot(n)));
        }
        //TODO undo the scale and return, perhaps chuck an enumerate in here and call the method solve or something.

        return results;
    }

    private void addMeasuredSeed(long min, long max) {
        gaps.set(dimensions, gaps.get(dimensions)+1);
        mins.add(min);
        maxes.add(max);
        dimensions += 1;
        gaps.add(0);
    }

    private void addUnmeasuredSeeds(int numSeeds) {
        gaps.set(dimensions, gaps.get(dimensions) + numSeeds);
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

    public void consumeNextBooleanCall() {
        addUnmeasuredSeeds(1);
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
        //TODO make sure this works
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
