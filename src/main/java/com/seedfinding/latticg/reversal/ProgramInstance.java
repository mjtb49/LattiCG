package com.seedfinding.latticg.reversal;

import com.seedfinding.latticg.RandomReverser;
import com.seedfinding.latticg.reversal.calltype.CallType;
import com.seedfinding.latticg.reversal.calltype.java.NextBooleanCall;
import com.seedfinding.latticg.reversal.calltype.java.NextDoubleCall;
import com.seedfinding.latticg.reversal.calltype.java.NextFloatCall;
import com.seedfinding.latticg.reversal.calltype.java.NextIntCall;
import com.seedfinding.latticg.reversal.calltype.java.NextLongCall;
import com.seedfinding.latticg.reversal.calltype.java.UnboundedNextIntCall;
import com.seedfinding.latticg.util.LCG;
import org.jetbrains.annotations.ApiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.LongStream;

public class ProgramInstance {

    private final Program program;
    private final List<Object> observations = new ArrayList<>();
    private int callIndex = 0;

    @ApiStatus.Internal
    protected ProgramInstance(Program program) {
        this.program = program;
    }

    public Program getProgram() {
        return program;
    }

    @SuppressWarnings("unchecked")
    public <T> ProgramInstance add(Object value) {
        if (callIndex >= program.getCalls().size()) {
            throw new IndexOutOfBoundsException("Too many observations for the number of calls specified");
        }
        CallType<T> callType = (CallType<T>) program.getCalls().get(callIndex++);
        T observation = callType.getType().cast(value);
        observations.add(observation);
        return this;
    }

    public LongStream reverse() {
        if (!LCG.JAVA.equals(program.getLcg())) {
            throw new IllegalStateException("Only the Java LCG is currently supported");
        }

        if (callIndex != program.getCalls().size()) {
            throw new IllegalStateException("Not all specified calls have been given observations");
        }

        RandomReverser reverser = new RandomReverser(program.getFilteredSkips());
        if (this.program.isVerbose()) {
            reverser.setVerbose(true);
        }
        List<CallType<?>> calls = program.getCalls();
        List<Long> skips = program.getSkips();
        for (int i = 0; i < calls.size(); i++) {
            CallType<?> call = calls.get(i);
            Object observation = observations.get(i);
            reverser.addUnmeasuredSeeds(skips.get(i));

            if (call instanceof NextBooleanCall) {
                NextBooleanCall booleanCall = (NextBooleanCall) call;
                boolean value = (Boolean) observation;
                if (booleanCall.isInverted()) {
                    value = !value;
                }
                reverser.addNextBooleanCall(value);
            } else if (call instanceof NextDoubleCall) {
                double value = (Double) observation;
                reverser.addNextDoubleCall(value, value, true, true);
            } else if (call instanceof NextFloatCall) {
                float value = (Float) observation;
                reverser.addNextFloatCall(value, value, true, true);
            } else if (call instanceof NextIntCall) {
                NextIntCall intCall = (NextIntCall) call;
                int value = (Integer) observation;
                reverser.addNextIntCall(intCall.getBound(), value, value);
            } else if (call instanceof UnboundedNextIntCall) {
                int value = (Integer) observation;
                reverser.addNextIntCall(value, value);
            } else if (call instanceof NextLongCall) {
                long value = (Long) observation;
                reverser.addNextLongCall(value, value);
            } else if (call instanceof NextFloatCall.FloatRange) {
                NextFloatCall.FloatRange floatRange = (NextFloatCall.FloatRange) call;
                boolean value = (Boolean) observation;
                if (floatRange.isInverted()) {
                    value = !value;
                }
                // if we have the full range [0.0f;1.0f) then we don't observe that call
                if (floatRange.getMin() == 0.0f && !floatRange.isMinStrict() && floatRange.getMax() == 1.0f && floatRange.isMaxStrict()) {
                    value = false;
                }
                if (value) {
                    reverser.addNextFloatCall(floatRange.getMin(), floatRange.getMax(), !floatRange.isMinStrict(), !floatRange.isMaxStrict());
                } else {
                    // TODO support this
                    reverser.addUnmeasuredSeeds(1);
                }
            } else if (call instanceof NextIntCall.IntRange) {
                NextIntCall.IntRange intRange = (NextIntCall.IntRange) call;
                boolean value = (Boolean) observation;
                if (intRange.isInverted()) {
                    value = !value;
                }
                int min = intRange.getMin();
                int max = intRange.getMax();
                if (intRange.isMinStrict()) {
                    min++;
                }
                if (intRange.isMaxStrict()) {
                    max--;
                }
                // if we use the full range no need of monitoring that call
                if (intRange.getBound() == (max - min + 1)) {
                    value = false;
                }
                if (value) {
                    reverser.addNextIntCall(intRange.getBound(), min, max);
                } else {
                    // TODO: support this
                    reverser.addUnmeasuredSeeds(1);
                }
            } else if (call instanceof UnboundedNextIntCall.IntRange) {
                UnboundedNextIntCall.IntRange intRange = (UnboundedNextIntCall.IntRange) call;
                boolean value = (Boolean) observation;
                if (intRange.isInverted()) {
                    value = !value;
                }
                int min = intRange.getMin();
                int max = intRange.getMax();
                if (intRange.isMinStrict()) {
                    min++;
                }
                if (intRange.isMaxStrict()) {
                    max--;
                }
                // if we use the full range no need of monitoring that call
                if (min == Integer.MIN_VALUE && max == Integer.MAX_VALUE) {
                    value = false;
                }
                if (value) {
                    reverser.addNextIntCall(min, max);
                } else {
                    // TODO: support this
                    reverser.addUnmeasuredSeeds(1);
                }
            } else if (call instanceof NextDoubleCall.DoubleRange) {
                NextDoubleCall.DoubleRange doubleRange = (NextDoubleCall.DoubleRange) call;
                boolean value = (Boolean) observation;
                if (doubleRange.isInverted()) {
                    value = !value;
                }
                // if we have the full range [0.0D;1.0D) then we don't observe that call
                if (doubleRange.getMin() == 0.0D && !doubleRange.isMinStrict() && doubleRange.getMax() == 1.0D && doubleRange.isMaxStrict()) {
                    value = false;
                }
                if (value) {
                    reverser.addNextDoubleCall(doubleRange.getMin(), doubleRange.getMax(), !doubleRange.isMinStrict(), !doubleRange.isMaxStrict());
                } else {
                    // TODO support this
                    reverser.addUnmeasuredSeeds(2);
                }
            } else if (call instanceof NextLongCall.LongRange) {
                NextLongCall.LongRange intRange = (NextLongCall.LongRange) call;
                boolean value = (Boolean) observation;
                if (intRange.isInverted()) {
                    value = !value;
                }
                long min = intRange.getMin();
                long max = intRange.getMax();
                if (intRange.isMinStrict()) {
                    min++;
                }
                if (intRange.isMaxStrict()) {
                    max--;
                }
                if (value) {
                    reverser.addNextLongCall(min, max);
                } else {
                    // TODO: support this
                    reverser.addUnmeasuredSeeds(2);
                }
            } else {
                throw new IllegalStateException("Unsupported call type: " + call.getClass().getName());
            }
        }

        return reverser.findAllValidSeeds();
    }

}
