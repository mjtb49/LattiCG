package com.seedfinding.latticg.reversal.calltype.java;

import com.seedfinding.latticg.reversal.calltype.CallType;
import com.seedfinding.latticg.reversal.calltype.RangeCallType;
import com.seedfinding.latticg.reversal.calltype.RangeableCallType;
import com.seedfinding.latticg.util.Range;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public class NextFloatCall extends RangeableCallType<Float> {
    @ApiStatus.Internal
    static final NextFloatCall INSTANCE = new NextFloatCall();

    private static final Float ABS_MIN = 0f;
    private static final Float ABS_MAX = 1f;

    private NextFloatCall() {
        super(Float.class, 1);
    }

    @Override
    protected RangeCallType<Float> createRangeCallType(Float min, Float max, boolean minStrict, boolean maxStrict, boolean inverted) {
        return new FloatRange(min, max, minStrict, maxStrict, inverted);
    }

    @Override
    protected Float getAbsoluteMin() {
        return ABS_MIN;
    }

    @Override
    protected Float getAbsoluteMax() {
        return ABS_MAX;
    }

    @Override
    public CallType<Range<Float>> ranged() {
        return new Ranged(0.5f);
    }

    @Override
    public CallType<Range<Float>> ranged(Float expectedSize) {
        return new Ranged(expectedSize);
    }

    @ApiStatus.Internal
    public static class FloatRange extends RangeCallType<Float> {
        public FloatRange(Float min, Float max, boolean minStrict, boolean maxStrict, boolean inverted) {
            super(min, max, minStrict, maxStrict, inverted, 1);
        }

        @Override
        protected RangeCallType<Float> createNew(Float min, Float max, boolean lowerStrict, boolean upperStrict, boolean inverted) {
            return new FloatRange(min, max, lowerStrict, upperStrict, inverted);
        }
    }

    @ApiStatus.Internal
    public static final class Ranged extends CallType<Range<Float>> {
        private final float expectedSize;

        private Ranged(float expectedSize) {
            super(Range.type(), 1);
            this.expectedSize = expectedSize;
        }

        public float getExpectedSize() {
            return expectedSize;
        }

        @Override
        public CallType<Range<Float>> not() {
            return new RangedInverted(expectedSize);
        }
    }

    @ApiStatus.Internal
    public static final class RangedInverted extends CallType<Range<Float>> {
        private final float expectedSize;

        private RangedInverted(float expectedSize) {
            super(Range.type(), 1);
            this.expectedSize = expectedSize;
        }

        public float getExpectedSize() {
            return expectedSize;
        }

        @Override
        public CallType<Range<Float>> not() {
            return new Ranged(expectedSize);
        }
    }
}
