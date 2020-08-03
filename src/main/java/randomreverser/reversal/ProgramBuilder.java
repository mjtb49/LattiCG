package randomreverser.reversal;

import randomreverser.reversal.calltype.CallType;
import randomreverser.util.LCG;

import java.util.ArrayList;
import java.util.List;

public class ProgramBuilder {

    private final LCG lcg;
    private List<CallType<?>> calls = new ArrayList<>();
    private List<Long> skips = new ArrayList<>();
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
