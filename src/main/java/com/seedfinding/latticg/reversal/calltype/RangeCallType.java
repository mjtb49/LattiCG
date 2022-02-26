package com.seedfinding.latticg.reversal.calltype;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public abstract class RangeCallType<T extends Comparable<T>> extends CallType<Boolean> {
    private final T min;
    private final T max;
    private final boolean minStrict;
    private final boolean maxStrict;
    private final boolean inverted;

    public RangeCallType(T min, T max, boolean minStrict, boolean maxStrict, boolean inverted, int numCalls) {
        super(Boolean.class, numCalls);
        if (min.compareTo(max) > 0) {
            throw new IllegalArgumentException(String.format("min (%s) > max (%s)", min, max));
        }
        if ((minStrict || maxStrict) && min.equals(max)) {
            throw new IllegalArgumentException(String.format("min (%s) == max (%s)", min, max));
        }
        this.min = min;
        this.max = max;
        this.minStrict = minStrict;
        this.maxStrict = maxStrict;
        this.inverted = inverted;
    }

    protected abstract RangeCallType<T> createNew(T min, T max, boolean lowerStrict, boolean upperStrict, boolean inverted);

    @Override
    public CallType<Boolean> not() {
        return createNew(min, max, minStrict, maxStrict, !inverted);
    }

    @Override
    public CallType<Boolean> equalTo(Boolean value) {
        return value == inverted ? this.not() : this;
    }

    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }

    public boolean isMinStrict() {
        return minStrict;
    }

    public boolean isMaxStrict() {
        return maxStrict;
    }

    public boolean isInverted() {
        return inverted;
    }
}
