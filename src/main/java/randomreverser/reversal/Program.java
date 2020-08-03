package randomreverser.reversal;

import org.jetbrains.annotations.ApiStatus;
import randomreverser.reversal.calltype.CallType;
import randomreverser.util.LCG;

import java.util.List;

public class Program {

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
}
