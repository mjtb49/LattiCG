package com.seedfinding.latticg.reversal.calltype;

import com.seedfinding.latticg.util.LCG;
import com.seedfinding.latticg.util.Rand;

import java.util.function.Predicate;

public class FilteredSkip {
    private final LCG skipLCG;
    private final Predicate<Rand> filter;

    public FilteredSkip(long currentIndex, Predicate<Rand> filter) {
        this.skipLCG = LCG.JAVA.combine(currentIndex);
        this.filter = filter;
    }

    public boolean checkState(Rand rand) {
        rand.advance(skipLCG);
        return this.filter.test(rand);
    }
}
