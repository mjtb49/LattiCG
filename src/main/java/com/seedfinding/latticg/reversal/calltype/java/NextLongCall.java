package com.seedfinding.latticg.reversal.calltype.java;

import com.seedfinding.latticg.reversal.calltype.CallType;
import com.seedfinding.latticg.reversal.calltype.RangeCallType;
import com.seedfinding.latticg.reversal.calltype.RangeableCallType;
import com.seedfinding.latticg.util.Range;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public class NextLongCall extends RangeableCallType<Long> {
    @ApiStatus.Internal
    static final NextLongCall INSTANCE = new NextLongCall();

    private static final Long ABS_MIN = Long.MIN_VALUE;
    private static final Long ABS_MAX = Long.MAX_VALUE;

    private NextLongCall() {
        super(Long.class, 2);
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

    @Override
    public CallType<Range<Long>> ranged() {
        return new Ranged(Long.MAX_VALUE);
    }

    @Override
    public CallType<Range<Long>> ranged(Long expectedSize) {
        return new Ranged(expectedSize);
    }

    @ApiStatus.Internal
    public static class LongRange extends RangeCallType<Long> {

        public LongRange(Long min, Long max, boolean minStrict, boolean maxStrict, boolean inverted) {
            super(min, max, minStrict, maxStrict, inverted, 2);
        }

        @Override
        protected RangeCallType<Long> createNew(Long min, Long max, boolean lowerStrict, boolean upperStrict, boolean inverted) {
            return new LongRange(min, max, lowerStrict, upperStrict, inverted);
        }
    }

    @ApiStatus.Internal
    public static final class Ranged extends CallType<Range<Long>> {
        private final long expectedSize;

        private Ranged(long expectedSize) {
            super(Range.type(), 2);
            this.expectedSize = expectedSize;
        }

        public long getExpectedSize() {
            return expectedSize;
        }

        @Override
        public CallType<Range<Long>> not() {
            return new RangedInverted(expectedSize);
        }
    }

    @ApiStatus.Internal
    public static final class RangedInverted extends CallType<Range<Long>> {
        private final long expectedSize;

        private RangedInverted(long expectedSize) {
            super(Range.type(), 2);
            this.expectedSize = expectedSize;
        }

        public long getExpectedSize() {
            return expectedSize;
        }

        @Override
        public CallType<Range<Long>> not() {
            return new Ranged(expectedSize);
        }
    }
}
