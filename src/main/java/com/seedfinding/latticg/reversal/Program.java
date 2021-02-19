package com.seedfinding.latticg.reversal;

import com.seedfinding.latticg.util.LCG;
import org.jetbrains.annotations.ApiStatus;
import com.seedfinding.latticg.reversal.calltype.CallType;

import java.util.List;

@ApiStatus.Experimental
public class Program {
    private boolean verbose=false;
    private final LCG lcg;
    private final List<CallType<?>> calls;
    private final List<Long> skips;

    protected Program(LCG lcg, List<CallType<?>> calls, List<Long> skips) {
        this.lcg = lcg;
        this.calls = calls;
        this.skips = skips;
    }

    public static ProgramBuilder builder(LCG lcg) {
        return new ProgramBuilder(lcg);
    }

    public ProgramInstance start() {
        return new ProgramInstance(this);
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public LCG getLcg() {
        return lcg;
    }

    @ApiStatus.Internal
    public List<CallType<?>> getCalls() {
        return calls;
    }

    @ApiStatus.Internal
    public List<Long> getSkips() {
        return skips;
    }

    @ApiStatus.Internal
    public boolean isVerbose() {
        return verbose;
    }
}
