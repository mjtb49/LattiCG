package com.seedfinding.latticg.math.lattice.LLL;

import com.seedfinding.latticg.math.component.BigFraction;

public class Params {
    public BigFraction delta = new BigFraction(75, 100);
    public boolean debug = false;
    public int maxStage = -1;
    public int pruneFactor = 0; // set to [10-15] for BlockSize >= 30.
    public static BigFraction recommendedDelta = new BigFraction(99, 100);

    public Params setPruneFactor(int pruneFactor) {
        this.pruneFactor = pruneFactor;
        return this;
    }

    public Params setMaxStage(int maxStage) {
        this.maxStage = maxStage;
        return this;
    }

    public Params setDelta(BigFraction delta) {
        this.delta = delta;
        return this;
    }

    public Params setDebug(boolean debug) {
        this.debug = debug;
        return this;
    }
}
