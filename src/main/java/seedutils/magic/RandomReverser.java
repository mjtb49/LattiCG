package main.java.seedutils.magic;

import main.java.seedutils.math.MathHelper;
import main.java.seedutils.math.component.BigMatrix;
import main.java.seedutils.math.component.BigVector;
import main.java.seedutils.math.component.Matrix;
import main.java.seedutils.math.component.Vector;
import main.java.seedutils.math.lattice.LLL;
import main.java.seedutils.math.lattice.Enumerate;
import main.java.seedutils.Rand;

import java.math.BigDecimal;
import java.util.ArrayList;

public class RandomReverser {

    private static final BigDecimal MOD = new BigDecimal(281474976710656L);
    private static final BigDecimal MULT = new BigDecimal(25214903917L);
    private static final BigDecimal ONE = new BigDecimal(1);
    private static final BigDecimal ZERO = new BigDecimal(0);

    private ArrayList<Long> mins = new ArrayList<>();
    private ArrayList<Long> maxes = new ArrayList<>();
    private ArrayList<Integer> gaps = new ArrayList<>();
    private int dimensions = 0;

    //TODO Make this pick and choose which dimensions to use instead of using all of them
    public BigMatrix findAllValidSeeds() {
        if (mins.size() != dimensions || maxes.size() != dimensions || gaps.size() != dimensions){
            //TODO Bad Input What Do
            return null;
        }

        BigVector sideLengths =  new BigVector(dimensions);
        for (int i = 0; i < dimensions; i++) {
            sideLengths.set(i,new BigDecimal(maxes.get(i)-mins.get(i)));
        }
        BigDecimal lcm = ONE;
        for (int i = 0; i < dimensions; i++) {
            lcm = sideLengths.get(i).multiply(lcm).divide(MathHelper.gcd(lcm, sideLengths.get(i)));
        }

        BigMatrix scales = new BigMatrix(dimensions,dimensions);
        for (int i = 0; i < dimensions; i++) {
            for (int j = 0; j < dimensions; j++)
                scales.set(i,j,ZERO);
            scales.set(i,i,lcm.divide(sideLengths.get(i)));
        }

        BigMatrix unscaledLattice = new BigMatrix(dimensions,dimensions);

        for(int i = 0; i < dimensions; i++) {
            for (int j = 0; j < dimensions; j++)
                unscaledLattice.set(i,j, ZERO);

            if (i == 0) {
                unscaledLattice.set(0, i, ONE);
            } else {
                unscaledLattice.set(0, i, unscaledLattice.get(0, i-1));
                unscaledLattice.set(i, i, MOD);
            }

            for (int j = 0; j < gaps.get(i); j ++) {
                unscaledLattice.set(0,i, unscaledLattice.get(0,i).multiply(MULT).remainder(MOD));
            }

        }

        BigMatrix scaledLattice = unscaledLattice.multiply(scales);

        LLL.Params params = new LLL.Params().setDelta(.99).setDebug(false);
        System.out.println("Reducing: "+scaledLattice);
        BigMatrix result = LLL.reduce(scaledLattice, params);
        System.out.println("Found Reduced Basis: " + result.multiply(scales.inverse()));

        Matrix m = new Matrix.Factory().fromBigMatrix(result.multiply(scales.inverse()));
        Vector vecMins = new Vector(dimensions);
        Vector vecMaxes = new Vector(dimensions);
        Vector offsets = new Vector(dimensions);
        Rand rand = new Rand(0x5deece66dL);
        for (int i = 0; i < dimensions; i++) {
            vecMins.set(i, (double) mins.get(i));
            vecMaxes.set(i, (double) maxes.get(i));
            offsets.set(i,rand.getSeed());
            if (i != dimensions-1)
                rand.advance(gaps.get(i+1));
        }
        System.out.println(vecMins);
        System.out.println(vecMaxes);
        System.out.println(offsets);
        for (Vector n :Enumerate.enumerate(dimensions, vecMins, vecMaxes, m, offsets)) {
            System.out.println("Found: " +(m.transpose().multiply(n)).add(offsets)+" at "+n);
        }
        //TODO undo the scale and return, perhaps chuck an enumerate in here and call the method solve or something.

        return null;
    }


    public void addNextIntCall(int n, int min, int max) {
        if (gaps.size()==0) {
            gaps.add(0);
        } else { gaps.add(1); }
        dimensions += 1;
        if ((n & (-n)) == n) {// if n is a power of 2
            int log = MagicMath.countTrailingZeroes(n);
            mins.add(min * (1L << (48 - log)));
            maxes.add(max * (1L << (48 - log)));
        }
        else {
            System.err.println("Reversal now has small chance of failure");
            //TODO
        }
    }

    public void addNextIntCall(int min, int max) {
        if (gaps.size()==0) {
            gaps.add(0);
        } else { gaps.add(1); }
        dimensions += 1;
        mins.add(min * (1L << (16)));
        maxes.add(max * (1L << (16)) - 1);
    }

    public void consumeNextIntCalls(int numCalls) {
        //TODO add handling for the potential of skipping a call in here
        gaps.set(dimensions,gaps.get(dimensions) + numCalls);
    }

}
