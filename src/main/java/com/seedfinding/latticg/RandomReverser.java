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
public class RandomReverser {
    protected final BigInteger MOD;
    protected final BigInteger MULT;

    protected final LCG lcg;
    protected final ArrayList<BigInteger> mins;
    protected final ArrayList<BigInteger> maxes;
    protected final ArrayList<Long> callIndices;
    protected final List<FilteredSkip> filteredSkips;
    protected BigMatrix lattice;
    protected long currentCallIndex;
    protected int dimensions;
    protected boolean verbose;
    protected double successChance;

    @ApiStatus.Internal
    public RandomReverser(LCG lcg, List<FilteredSkip> filteredSkips) {
        if (lcg.modulus > 0) {
            this.MOD = BigInteger.valueOf(lcg.modulus);
        } else {
            this.MOD = BigInteger.valueOf(lcg.modulus).add(BigInteger.valueOf(2).pow(64));
        }
        this.MULT = BigInteger.valueOf(lcg.multiplier).mod(MOD);
        this.lcg = lcg;

        this.verbose = false;
        this.dimensions = 0;
        this.mins = new ArrayList<>();
        this.maxes = new ArrayList<>();
        this.callIndices = new ArrayList<>();
        this.currentCallIndex = 0;
        this.successChance = 1.0;
        this.filteredSkips = filteredSkips;
    }

    //TODO Make this pick and choose which dimensions to use instead of using all of them
    public LongStream findAllValidSeeds() {
        if (dimensions == 0) {
            return LongStream.range(0, this.lcg.modulus);
        }

        createLattice();
        BigVector lower = new BigVector(dimensions);
        BigVector upper = new BigVector(dimensions);
        BigVector offset = new BigVector(dimensions);
        Rand rand = Rand.ofInternalSeed(lcg, 0L);

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

        LCG r = lcg.combine(-callIndices.get(0));

        if (successChance != 1.0)
            System.err.printf("Ignored approximately %.2e of all seeds %n", 1 - successChance);

        return Enumerate.enumerate(lattice.transpose(), lower, upper, offset)
            .map(vec -> vec.get(0))
            .map(BigFraction::getNumerator)
            .mapToLong(BigInteger::longValue)
            .map(r::nextSeed)
            .filter(seed -> {
                    for (FilteredSkip call : this.filteredSkips) {
                        Rand rr = Rand.ofInternalSeed(lcg, seed);
                        if (!call.checkState(rr)) {
                            return false;
                        }
                    }
                    return true;
                }
            );
    }

    private void createLattice() {
        if (verbose)
            System.out.println("Call Indices: " + callIndices);
        if (mins.size() != dimensions || maxes.size() != dimensions || callIndices.size() != dimensions) {
            //TODO Bad Input What Do
            return;
        }

        BigInteger[] sideLengths = new BigInteger[dimensions]; //The lengths of the sides of the cuboid in which our seeds must fall

        for (int i = 0; i < dimensions; i++) {
            sideLengths[i] = maxes.get(i).subtract(mins.get(i)).add(BigInteger.ONE);
        }

        BigInteger lcm = BigInteger.ONE;
        for (int i = 0; i < dimensions; i++) {
            lcm = Mth.lcm(lcm, sideLengths[i]);
        }

        BigMatrix scales = new BigMatrix(dimensions, dimensions);
        for (int i = 0; i < dimensions; i++) {
            for (int j = 0; j < dimensions; j++)
                scales.set(i, j, BigFraction.ZERO);
            scales.set(i, i, new BigFraction(lcm.divide(sideLengths[i])));
        }

        BigMatrix unscaledLattice = lattice;
        if (verbose)
            System.out.println("Looking for points on:\n" + BigMatrixUtil.toPrettyString(unscaledLattice));

        BigMatrix scaledLattice = unscaledLattice.multiply(scales);
        Params params = new Params().setDelta(Params.recommendedDelta).setDebug(false);
        if (verbose)
            System.out.println("Reducing:\n" + BigMatrixUtil.toPrettyString(scaledLattice));
        // BigMatrix transformations = BigMatrix.identityMatrix(dimensions);
        Result result = LLL.reduce(scaledLattice, params);
        //System.out.println("found:\n" + transformations.multiply(unscaledLattice).toPrettyString());
        if (verbose) {
            System.out.println("Found Reduced Scaled Basis:\n" + BigMatrixUtil.toPrettyString(result.getReducedBasis()));
            // System.out.println("Found Reduced Basis:\n" + result.getTransformations().multiply(unscaledLattice).toPrettyString());
            System.out.println("Found Reduced Basis:\n" + BigMatrixUtil.toPrettyString(result.getReducedBasis().multiply(BigMatrixUtil.inverse(scales))));
        }
        //Matrix m = new Matrix.Factory().fromBigMatrix(result.multiply(scales.inverse()));
        // lattice = result.getTransformations().multiply(unscaledLattice);
        lattice = result.getReducedBasis().multiply(BigMatrixUtil.inverse(scales));
    }

    public void addMeasuredSeed(long min, long max) {
        addMeasuredSeed(BigInteger.valueOf(min), BigInteger.valueOf(max));
    }

    public void addMeasuredSeed(BigInteger min, BigInteger max) {
        min = min.mod(MOD);
        max = max.mod(MOD);
        if (max.compareTo(min) < 0) {
            max = max.add(MOD);
        }
        //callIndices.set(dimensions, callIndices.get(dimensions)+1);
        mins.add(min);
        maxes.add(max);
        dimensions += 1;
        currentCallIndex += 1;
        callIndices.add(currentCallIndex);
        BigMatrix newLattice = new BigMatrix(dimensions + 1, dimensions);
        if (dimensions != 1) {
            for (int row = 0; row < dimensions; row++)
                for (int col = 0; col < dimensions - 1; col++)
                    newLattice.set(row, col, lattice.get(row, col));
        }
        BigInteger tempMult = MULT.modPow(BigInteger.valueOf(callIndices.get(dimensions - 1) - callIndices.get(0)), MOD);
        newLattice.set(0, dimensions - 1, new BigFraction(tempMult));
        newLattice.set(dimensions, dimensions - 1, new BigFraction(MOD));
        lattice = newLattice;
    }

    public void addModuloMeasuredSeed(long min, long max, long mod) {
        addModuloMeasuredSeed(BigInteger.valueOf(min), BigInteger.valueOf(max), BigInteger.valueOf(mod));
    }

    public void addModuloMeasuredSeed(BigInteger min, BigInteger max, BigInteger measured_mod) {

        min = min.mod(measured_mod);
        max = max.mod(measured_mod);
        if (max.compareTo(min) < 0) {
            max = max.add(measured_mod);
        }

        BigInteger residue = MOD.mod(measured_mod);
        if (!residue.equals(BigInteger.ZERO)) {
            successChance *= 1 - residue.doubleValue() / (double)  lcg.modulus;
            //First condition - is the seed real. This conveys more info than it seems since the normal mod vector is not present.
            mins.add(BigInteger.ZERO);
            maxes.add(MOD.subtract(residue)); // in the case the seed is > MOD - residue, the do while in java's nextInt will trigger. In general seeds larger than this are unsupported behavior
            currentCallIndex += 1;
            callIndices.add(currentCallIndex);
            //Second condition - does the seed have a number within the bounds in its residue class.
            mins.add(min);
            maxes.add(max);
            callIndices.add(currentCallIndex); //We don't increment the call index here because this is really 2 conditions on the same seed

            dimensions += 2; //We added 2 conditions

            BigMatrix newLattice = new BigMatrix(dimensions + 1, dimensions);

            if (dimensions != 2) { //Copy the old lattice over
                for (int row = 0; row < dimensions - 1; row++)
                    for (int col = 0; col < dimensions - 2; col++)
                        newLattice.set(row, col, lattice.get(row, col));
            }

            BigInteger tempMult = MULT.modPow(BigInteger.valueOf(callIndices.get(dimensions - 1) - callIndices.get(0)), MOD);
            newLattice.set(0, dimensions - 2, new BigFraction(tempMult));
            newLattice.set(0, dimensions - 1, new BigFraction(tempMult));

            //vector capturing the effect of the modulo 2^48 operation on the residue class modulo measured_mod
            newLattice.set(dimensions - 1, dimensions - 1, new BigFraction(MOD));
            newLattice.set(dimensions - 1, dimensions - 2, new BigFraction(MOD));

            //vector identifying everything in residue classes modulo measured_mod
            newLattice.set(dimensions, dimensions - 1, new BigFraction(measured_mod));

            //update the lattice.
            lattice = newLattice;
        } else {
            // the conditions are compatible, so we can get away with just one new dimension. Caution should
            // be taken in the case this condition is the very first one in the lattice as other calls
            // may force upper bits
            mins.add(min);
            maxes.add(max);
            dimensions += 1;
            currentCallIndex += 1;
            callIndices.add(currentCallIndex);
            BigMatrix newLattice = new BigMatrix(dimensions + 1, dimensions);
            if (dimensions != 1) {
                for (int row = 0; row < dimensions; row++)
                    for (int col = 0; col < dimensions - 1; col++)
                        newLattice.set(row, col, lattice.get(row, col));
            } else if (!MOD.equals(measured_mod)) {
                //TODO find a way to recover the seed in the case we only have constraints of this type
                System.err.println("First call not a bound on a seed. Junk output may be produced.");
            }
            //we might be able to use the provided modulus here instead
            BigInteger tempMult = MULT.modPow(BigInteger.valueOf(callIndices.get(dimensions - 1) - callIndices.get(0)), MOD);
            newLattice.set(0, dimensions - 1, new BigFraction(tempMult));
            newLattice.set(dimensions, dimensions - 1, new BigFraction(measured_mod)); //Note this is not MOD.
            lattice = newLattice;
        }
    }

    public void addUnmeasuredSeeds(long numSeeds) {
        currentCallIndex += numSeeds;
        //callIndices.set(dimensions, callIndices.get(dimensions) + numSeeds);
    }

    public GenerationInfo createGenerationInfo() {
        if (dimensions == 0) {
            return new GenerationInfo(0, new BigMatrix(0, 0), new BigVector(new BigFraction[0]), lcg, 1);
        }

        createLattice();
        BigVector offset = new BigVector(dimensions);
        Rand rand = Rand.ofInternalSeed(lcg, 0L);

        for (int i = 0; i < dimensions; i++) {
            offset.set(i, new BigFraction(rand.getSeed()));

            if (i != dimensions - 1) {
                rand.advance(callIndices.get(i + 1) - callIndices.get(i));
            }
        }

        LCG r = lcg.combine(-callIndices.get(0));

        return new GenerationInfo(dimensions, lattice.transpose(), offset, r, successChance);
    }

    public static final class GenerationInfo {
        public final int dimensions;
        public final BigMatrix basis;
        public final BigVector offset;
        public final LCG r;
        public final double successChance;

        private GenerationInfo(int dimensions, BigMatrix basis, BigVector offset, LCG r, double successChance) {
            this.dimensions = dimensions;
            this.basis = basis;
            this.offset = offset;
            this.r = r;
            this.successChance = successChance;
        }
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
}
