package com.seedfinding.latticg.reversal;

import com.seedfinding.latticg.reversal.calltype.CallType;
import com.seedfinding.latticg.reversal.calltype.FilteredSkip;
import com.seedfinding.latticg.util.LCG;
import com.seedfinding.latticg.util.Rand;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@ApiStatus.Experimental
public class ProgramBuilder {

    private final LCG lcg;
    private final List<CallType<?>> calls = new ArrayList<>();
    private final List<Long> skips = new ArrayList<>();
    private final List<FilteredSkip> filteredSkips = new ArrayList<>();
    private long currentSkip = 0;
    private long currentIndex = 0;

    ProgramBuilder(LCG lcg) {
        this.lcg = lcg;
    }

    public ProgramBuilder add(CallType<?> call) {
        calls.add(call);
        skips.add(currentSkip);
        currentSkip = 0;
        currentIndex += call.getNumCalls();
        return this;
    }

    public ProgramBuilder skip(long steps) {
        currentSkip += steps;
        currentIndex += steps;
        return this;
    }

    public ProgramBuilder filteredSkip(Predicate<Rand> filter, long steps) {
        filteredSkips.add(new FilteredSkip(currentIndex, filter));
        currentSkip += steps;
        currentIndex += steps;
        return this;
    }

    public Program build() {
        // TODO: improve this (a lot)
        return new Program(lcg, calls, skips, filteredSkips);
    }

}
