package randomreverser.reversal.calltype.java;

import org.jetbrains.annotations.ApiStatus;
import randomreverser.reversal.calltype.RangeCallType;
import randomreverser.reversal.calltype.RangeableCallType;

@ApiStatus.Experimental
public class NextIntCall extends RangeableCallType<Integer> {
    private final int bound;
    private static final Integer ABS_MIN = 0;
    private final Integer ABS_MAX;

    @ApiStatus.Internal
    NextIntCall(int bound) {
        super(Integer.class);
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
            super(min, max, minStrict, maxStrict, inverted);
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
