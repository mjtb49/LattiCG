package com.seedfinding.latticg.reversal;

import com.seedfinding.latticg.reversal.calltype.CallType;
import com.seedfinding.latticg.util.LCG;
import com.seedfinding.latticg.util.Rand;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.LongStream;

public final class DynamicProgram {
    private final ProgramBuilder programBuilder;
    private final List<Object> values = new ArrayList<>();
    private boolean verbose = false;
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

    public DynamicProgram filteredSkip(Predicate<Rand> filter, long steps) {
        checkValid();
        programBuilder.filteredSkip(filter, steps);
        return this;
    }

    public DynamicProgram skip(long steps) {
        checkValid();
        programBuilder.skip(steps);
        return this;
    }

    public LongStream reverse() {
        checkValid();
        Program program = programBuilder.build();
        if (this.verbose) {
            program.setVerbose(true);
        }
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

    public boolean isVerbose() {
        return verbose;
    }

    public DynamicProgram setVerbose(boolean verbose) {
        this.verbose = verbose;
        return this;
    }
}
