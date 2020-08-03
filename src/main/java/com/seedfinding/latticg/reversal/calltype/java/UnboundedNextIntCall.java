package com.seedfinding.latticg.reversal.calltype.java;

import com.seedfinding.latticg.reversal.calltype.RangeCallType;
import com.seedfinding.latticg.reversal.calltype.RangeableCallType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public class UnboundedNextIntCall extends RangeableCallType<Integer> {
    @ApiStatus.Internal
    static final UnboundedNextIntCall INSTANCE = new UnboundedNextIntCall();

    private static final Integer ABS_MIN = Integer.MIN_VALUE;
    private static final Integer ABS_MAX = Integer.MAX_VALUE;

    private UnboundedNextIntCall() {
        super(Integer.class);
    }

    @Override
    protected RangeCallType<Integer> createRangeCallType(Integer min, Integer max, boolean minStrict, boolean maxStrict, boolean inverted) {
        return new IntRange(min, max, minStrict, maxStrict, inverted);
    }

    @Override
    protected Integer getAbsoluteMin() {
        return ABS_MIN;
    }

    @Override
    protected Integer getAbsoluteMax() {
        return ABS_MAX;
    }

    @Override
    protected boolean isAbsoluteMaxStrict() {
        return false;
    }

    @ApiStatus.Internal
    public static class IntRange extends RangeCallType<Integer> {
        public IntRange(Integer min, Integer max, boolean minStrict, boolean maxStrict, boolean inverted) {
            super(min, max, minStrict, maxStrict, inverted);
        }

        @Override
        protected RangeCallType<Integer> createNew(Integer min, Integer max, boolean lowerStrict, boolean upperStrict, boolean inverted) {
            return new IntRange(min, max, lowerStrict, upperStrict, inverted);
        }
    }
}
