package com.seedfinding.latticg.reversal.calltype.java;

import com.seedfinding.latticg.reversal.calltype.RangeCallType;
import com.seedfinding.latticg.reversal.calltype.RangeableCallType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public class NextIntCall extends RangeableCallType<Integer> {
    private final int bound;
    private static final Integer ABS_MIN = 0;
    private final Integer ABS_MAX;

    @ApiStatus.Internal
    protected NextIntCall(int bound) {
        super(Integer.class, 1);
        this.bound = bound;
        ABS_MAX = bound;
    }

    @Override
    protected RangeCallType<Integer> createRangeCallType(Integer min, Integer max, boolean minStrict, boolean maxStrict, boolean inverted) {
        return new IntRange(bound, min, max, minStrict, maxStrict, inverted);
    }

    @Override
    protected Integer getAbsoluteMin() {
        return ABS_MIN;
    }

    @Override
    protected Integer getAbsoluteMax() {
        return ABS_MAX;
    }

    public int getBound() {
        return bound;
    }

    @ApiStatus.Internal
    public static class IntRange extends RangeCallType<Integer> {
        private final int bound;

        public IntRange(int bound, Integer min, Integer max, boolean minStrict, boolean maxStrict, boolean inverted) {
            super(min, max, minStrict, maxStrict, inverted, 1);
            this.bound = bound;
        }

        @Override
        protected RangeCallType<Integer> createNew(Integer min, Integer max, boolean lowerStrict, boolean upperStrict, boolean inverted) {
            return new IntRange(bound, min, max, lowerStrict, upperStrict, inverted);
        }

        public int getBound() {
            return bound;
        }
    }
}
