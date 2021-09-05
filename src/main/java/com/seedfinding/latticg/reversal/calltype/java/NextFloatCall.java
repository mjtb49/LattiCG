package com.seedfinding.latticg.reversal.calltype.java;

import com.seedfinding.latticg.reversal.calltype.RangeCallType;
import com.seedfinding.latticg.reversal.calltype.RangeableCallType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public class NextFloatCall extends RangeableCallType<Float> {
    @ApiStatus.Internal
    static final NextFloatCall INSTANCE = new NextFloatCall();

    private static final Float ABS_MIN = 0f;
    private static final Float ABS_MAX = 1f;

    private NextFloatCall() {
        super(Float.class);
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

    @ApiStatus.Internal
    public static class FloatRange extends RangeCallType<Float> {
        public FloatRange(Float min, Float max, boolean minStrict, boolean maxStrict, boolean inverted) {
            super(min, max, minStrict, maxStrict, inverted);
        }

        @Override
        protected RangeCallType<Float> createNew(Float min, Float max, boolean lowerStrict, boolean upperStrict, boolean inverted) {
            return new FloatRange(min, max, lowerStrict, upperStrict, inverted);
        }
    }
}
