package com.seedfinding.latticg.reversal.calltype.java;

import com.seedfinding.latticg.reversal.calltype.CallType;
import com.seedfinding.latticg.reversal.calltype.RangeCallType;
import com.seedfinding.latticg.reversal.calltype.RangeableCallType;
import com.seedfinding.latticg.util.Range;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public class UnboundedNextIntCall extends RangeableCallType<Integer> {
    @ApiStatus.Internal
    static final UnboundedNextIntCall INSTANCE = new UnboundedNextIntCall();

    private static final Integer ABS_MIN = Integer.MIN_VALUE;
    private static final Integer ABS_MAX = Integer.MAX_VALUE;

    private UnboundedNextIntCall() {
        super(Integer.class, 1);
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

    @Override
    public CallType<Range<Integer>> ranged() {
        return new Ranged(Integer.MAX_VALUE);
    }

    @Override
    public CallType<Range<Integer>> ranged(Integer expectedSize) {
        return new Ranged(expectedSize);
    }

    @ApiStatus.Internal
    public static class IntRange extends RangeCallType<Integer> {
        public IntRange(Integer min, Integer max, boolean minStrict, boolean maxStrict, boolean inverted) {
            super(min, max, minStrict, maxStrict, inverted, 1);
        }

        @Override
        protected RangeCallType<Integer> createNew(Integer min, Integer max, boolean lowerStrict, boolean upperStrict, boolean inverted) {
            return new IntRange(min, max, lowerStrict, upperStrict, inverted);
        }
    }

    @ApiStatus.Internal
    public static final class Ranged extends CallType<Range<Integer>> {
        private final int expectedSize;

        private Ranged(int expectedSize) {
            super(Range.type(), 1);
            this.expectedSize = expectedSize;
        }

        public int getExpectedSize() {
            return expectedSize;
        }

        @Override
        public CallType<Range<Integer>> not() {
            return new RangedInverted(expectedSize);
        }
    }

    @ApiStatus.Internal
    public static final class RangedInverted extends CallType<Range<Integer>> {
        private final int expectedSize;

        private RangedInverted(int expectedSize) {
            super(Range.type(), 1);
            this.expectedSize = expectedSize;
        }

        public int getExpectedSize() {
            return expectedSize;
        }

        @Override
        public CallType<Range<Integer>> not() {
            return new Ranged(expectedSize);
        }
    }
}
