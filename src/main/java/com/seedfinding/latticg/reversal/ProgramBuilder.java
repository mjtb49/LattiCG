package com.seedfinding.latticg.reversal;

import com.seedfinding.latticg.util.LCG;
import com.seedfinding.latticg.reversal.calltype.CallType;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;

@ApiStatus.Experimental
public class ProgramBuilder {

    private final LCG lcg;
    private final List<CallType<?>> calls = new ArrayList<>();
    private final List<Long> skips = new ArrayList<>();
    private long currentSkip = 0;

    ProgramBuilder(LCG lcg) {
        this.lcg = lcg;
    }

    public ProgramBuilder add(CallType<?> call) {
        calls.add(call);
        skips.add(currentSkip);
        currentSkip = 0;
        return this;
    }

    public ProgramBuilder skip(long steps) {
        currentSkip += steps;
        return this;
    }

    public Program build() {
        // TODO: improve this (a lot)
        return new Program(lcg, calls, skips);
    }

}
