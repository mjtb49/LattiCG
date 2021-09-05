package com.seedfinding.latticg.reversal;

import com.seedfinding.latticg.reversal.calltype.CallType;
import com.seedfinding.latticg.reversal.calltype.FilteredSkip;
import com.seedfinding.latticg.util.LCG;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@ApiStatus.Experimental
public class Program {
    private final LCG lcg;
    private final List<CallType<?>> calls;
    private final List<Long> skips;
    private final List<FilteredSkip> filteredSkips;
    private boolean verbose = false;

    protected Program(LCG lcg, List<CallType<?>> calls, List<Long> skips, List<FilteredSkip> filteredSkips) {
        this.lcg = lcg;
        this.calls = calls;
        this.skips = skips;
        this.filteredSkips = filteredSkips;
    }

    public static ProgramBuilder builder(LCG lcg) {
        return new ProgramBuilder(lcg);
    }

    public ProgramInstance start() {
        return new ProgramInstance(this);
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
    public List<FilteredSkip> getFilteredSkips() {
        return filteredSkips;
    }

    @ApiStatus.Internal
    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
}
