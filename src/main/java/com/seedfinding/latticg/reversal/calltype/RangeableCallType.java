package com.seedfinding.latticg.reversal.calltype;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Experimental
public abstract class RangeableCallType<T extends Comparable<T>> extends CallType<T> {
    public RangeableCallType(Class<T> type, int numCalls) {
        super(type, numCalls);
    }

    @Override
    public CallType<Boolean> betweenII(T min, T max) {
        return createRangeCallType(min, max, false, false, false);
    }

    @Override
    public CallType<Boolean> betweenIE(T min, T max) {
        return createRangeCallType(min, max, false, true, false);
    }

    @Override
    public CallType<Boolean> betweenEI(T min, T max) {
        return createRangeCallType(min, max, true, false, false);
    }

    @Override
    public CallType<Boolean> betweenEE(T min, T max) {
        return createRangeCallType(min, max, true, true, false);
    }

    @Override
    public CallType<Boolean> equalTo(T value) {
        return betweenII(value, value);
    }

    @Override
    public CallType<Boolean> lessThan(T value) {
        return createRangeCallType(getAbsoluteMin(), value, isAbsoluteMinStrict(), true, false);
    }

    @Override
    public CallType<Boolean> lessThanEqual(T value) {
        return createRangeCallType(getAbsoluteMin(), value, isAbsoluteMinStrict(), false, false);
    }

    @Override
    public CallType<Boolean> greaterThan(T value) {
        return createRangeCallType(value, getAbsoluteMax(), true, isAbsoluteMaxStrict(), false);
    }

    @Override
    public CallType<Boolean> greaterThanEqual(T value) {
        return createRangeCallType(value, getAbsoluteMax(), false, isAbsoluteMaxStrict(), false);
    }

    protected abstract RangeCallType<T> createRangeCallType(T min, T max, boolean minStrict, boolean maxStrict, boolean inverted);

    protected abstract T getAbsoluteMin();

    protected abstract T getAbsoluteMax();

    protected boolean isAbsoluteMinStrict() {
        return false;
    }

    protected boolean isAbsoluteMaxStrict() {
        return true;
    }
}
