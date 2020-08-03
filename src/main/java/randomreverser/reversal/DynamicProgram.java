package randomreverser.reversal;

import randomreverser.reversal.calltype.CallType;
import randomreverser.util.LCG;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

public final class DynamicProgram {

    private final ProgramBuilder programBuilder;
    private final List<Object> values = new ArrayList<>();
    private boolean valid = true;

    private DynamicProgram(LCG lcg) {
        programBuilder = Program.builder(lcg);
    }

    public static DynamicProgram create(LCG lcg) {
        return new DynamicProgram(lcg);
    }

    public <T> DynamicProgram add(CallType<T> callType, T value) {
        checkValid();
        programBuilder.add(callType);
        values.add(value);
        return this;
    }

    public DynamicProgram add(CallType<Boolean> callType) {
        return add(callType, true);
    }

    public DynamicProgram skip(long steps) {
        checkValid();
        programBuilder.skip(steps);
        return this;
    }

    public LongStream reverse() {
        checkValid();
        Program program = programBuilder.build();
        ProgramInstance instance = program.start();
        for (Object value : values) {
            instance.add(value);
        }
        LongStream seeds = instance.reverse();
        valid = false;
        return seeds;
    }

    private void checkValid() {
        if (!valid) {
            throw new IllegalStateException("This DynamicProgram has already been used");
        }
    }

}
