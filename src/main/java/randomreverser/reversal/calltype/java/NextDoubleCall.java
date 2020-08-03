package randomreverser.reversal.calltype.java;

import org.jetbrains.annotations.ApiStatus;
import randomreverser.reversal.calltype.RangeCallType;
import randomreverser.reversal.calltype.RangeableCallType;

@ApiStatus.Experimental
public class NextDoubleCall extends RangeableCallType<Double> {
    @ApiStatus.Internal
    static final NextDoubleCall INSTANCE = new NextDoubleCall();

    private static final Double ABS_MIN = 0.0;
    private static final Double ABS_MAX = 1.0;

    private NextDoubleCall() {
        super(Double.class);
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

    @ApiStatus.Internal
    public static class DoubleRange extends RangeCallType<Double> {

        public DoubleRange(Double min, Double max, boolean minStrict, boolean maxStrict, boolean inverted) {
            super(min, max, minStrict, maxStrict, inverted);
        }

        @Override
        protected RangeCallType<Double> createNew(Double min, Double max, boolean lowerStrict, boolean upperStrict, boolean inverted) {
            return new DoubleRange(min, max, lowerStrict, upperStrict, inverted);
        }
    }
}
