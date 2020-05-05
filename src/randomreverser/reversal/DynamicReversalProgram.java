package randomreverser.reversal;

import randomreverser.reversal.calltype.CallType;
import randomreverser.util.LCG;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

public final class DynamicReversalProgram {

    private final ReversalProgramBuilder programBuilder;
    private final List<Object> values = new ArrayList<>();
    private boolean valid = true;

    private DynamicReversalProgram(LCG lcg) {
        programBuilder = ReversalProgram.builder(lcg);
    }

    public static DynamicReversalProgram create(LCG lcg) {
        return new DynamicReversalProgram(lcg);
    }

    public <T> DynamicReversalProgram add(CallType<T> callType, T value) {
        checkValid();
        programBuilder.add(callType);
        values.add(value);
        return this;
    }

    public DynamicReversalProgram add(CallType<Boolean> callType) {
        return add(callType, true);
    }

    public DynamicReversalProgram skip(long steps) {
        checkValid();
        programBuilder.skip(steps);
        return this;
    }

    public LongStream reverse() {
        checkValid();
        ReversalProgram program = programBuilder.build();
        ReversalProgramInstance instance = program.start();
        for (Object value : values) {
            instance.add(value);
        }
        LongStream seeds = instance.reverse();
        valid = false;
        return seeds;
    }

    private void checkValid() {
        if (!valid) {
            throw new IllegalStateException("This InterpretedReversalProgram has already been used");
        }
    }

}
