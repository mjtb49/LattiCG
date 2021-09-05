package com.seedfinding.latticg.reversal.calltype.java;

import com.seedfinding.latticg.reversal.calltype.RangeCallType;
import com.seedfinding.latticg.reversal.calltype.RangeableCallType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public class NextLongCall extends RangeableCallType<Long> {
    @ApiStatus.Internal
    static final NextLongCall INSTANCE = new NextLongCall();

    private static final Long ABS_MIN = Long.MIN_VALUE;
    private static final Long ABS_MAX = Long.MAX_VALUE;

    private NextLongCall() {
        super(Long.class);
    }

    @Override
    protected RangeCallType<Long> createRangeCallType(Long min, Long max, boolean minStrict, boolean maxStrict, boolean inverted) {
        return new LongRange(min, max, minStrict, maxStrict, inverted);
    }

    @Override
    protected Long getAbsoluteMin() {
        return ABS_MIN;
    }

    @Override
    protected Long getAbsoluteMax() {
        return ABS_MAX;
    }

    @Override
    protected boolean isAbsoluteMaxStrict() {
        return false;
    }

    @ApiStatus.Internal
    public static class LongRange extends RangeCallType<Long> {

        public LongRange(Long min, Long max, boolean minStrict, boolean maxStrict, boolean inverted) {
            super(min, max, minStrict, maxStrict, inverted);
        }

        @Override
        protected RangeCallType<Long> createNew(Long min, Long max, boolean lowerStrict, boolean upperStrict, boolean inverted) {
            return new LongRange(min, max, lowerStrict, upperStrict, inverted);
        }
    }
}
