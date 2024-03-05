package com.seedfinding.latticg.reversal.calltype.java;

import com.seedfinding.latticg.reversal.calltype.CallType;
import com.seedfinding.latticg.reversal.calltype.RangeCallType;
import com.seedfinding.latticg.reversal.calltype.RangeableCallType;
import com.seedfinding.latticg.util.Range;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public class NextDoubleCall extends RangeableCallType<Double> {
    @ApiStatus.Internal
    static final NextDoubleCall INSTANCE = new NextDoubleCall();

    private static final Double ABS_MIN = 0.0;
    private static final Double ABS_MAX = 1.0;

    private NextDoubleCall() {
        super(Double.class, 2);
    }

    @Override
    protected RangeCallType<Double> createRangeCallType(Double min, Double max, boolean minStrict, boolean maxStrict, boolean inverted) {
        return new DoubleRange(min, max, minStrict, maxStrict, inverted);
    }

    @Override
    protected Double getAbsoluteMin() {
        return ABS_MIN;
    }

    @Override
    protected Double getAbsoluteMax() {
        return ABS_MAX;
    }

    @Override
    public CallType<Range<Double>> ranged() {
        return new Ranged(0.5);
    }

    @Override
    public CallType<Range<Double>> ranged(Double expectedSize) {
        return new Ranged(expectedSize);
    }

    @ApiStatus.Internal
    public static class DoubleRange extends RangeCallType<Double> {

        public DoubleRange(Double min, Double max, boolean minStrict, boolean maxStrict, boolean inverted) {
            super(min, max, minStrict, maxStrict, inverted, 2);
        }

        @Override
        protected RangeCallType<Double> createNew(Double min, Double max, boolean lowerStrict, boolean upperStrict, boolean inverted) {
            return new DoubleRange(min, max, lowerStrict, upperStrict, inverted);
        }
    }

    @ApiStatus.Internal
    public static final class Ranged extends CallType<Range<Double>> {
        private final double expectedSize;

        private Ranged(double expectedSize) {
            super(Range.type(), 2);
            this.expectedSize = expectedSize;
        }

        public double getExpectedSize() {
            return expectedSize;
        }

        @Override
        public CallType<Range<Double>> not() {
            return new RangedInverted(expectedSize);
        }
    }

    @ApiStatus.Internal
    public static final class RangedInverted extends CallType<Range<Double>> {
        private final double expectedSize;

        private RangedInverted(double expectedSize) {
            super(Range.type(), 2);
            this.expectedSize = expectedSize;
        }

        public double getExpectedSize() {
            return expectedSize;
        }

        @Override
        public CallType<Range<Double>> not() {
            return new Ranged(expectedSize);
        }
    }
}
