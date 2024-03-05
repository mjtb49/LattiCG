package com.seedfinding.latticg.util;

import java.util.Objects;

public final class Range<T> {
    private final T min;
    private final T max;
    private final boolean minInclusive;
    private final boolean maxInclusive;

    private Range(T min, T max, boolean minInclusive, boolean maxInclusive) {
        this.min = min;
        this.max = max;
        this.minInclusive = minInclusive;
        this.maxInclusive = maxInclusive;
    }

    public static <T extends Comparable<T>> Range<T> of(T minInclusive, T maxExclusive) {
        return new Range<>(minInclusive, maxExclusive, true, false);
    }

    public static <T extends Comparable<T>> Range<T> ofInclusive(T min, T max) {
        return new Range<>(min, max, true, true);
    }

    public static <T extends Comparable<T>> Range<T> of(T min, T max, boolean minInclusive, boolean maxInclusive) {
        return new Range<>(min, max, minInclusive, maxInclusive);
    }

    public T min() {
        return min;
    }

    public T max() {
        return max;
    }

    public boolean minInclusive() {
        return minInclusive;
    }

    public boolean maxInclusive() {
        return maxInclusive;
    }

    @SuppressWarnings("unchecked")
    public boolean contains(T value) {
        int minCmp = ((Comparable<T>) min).compareTo(value);
        if (minInclusive ? minCmp < 0 : minCmp <= 0) {
            return false;
        }
        int maxCmp = ((Comparable<T>) max).compareTo(value);
        return maxInclusive ? maxCmp <= 0 : maxCmp < 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(min, max, minInclusive, maxInclusive);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Range)) {
            return false;
        }
        Range<?> that = (Range<?>) obj;
        return min.equals(that.min) && max.equals(that.max) && minInclusive == that.minInclusive && maxInclusive == that.maxInclusive;
    }

    @Override
    public String toString() {
        return String.format("%s%s, %s%s", minInclusive ? "[" : "(", min, max, maxInclusive ? "]" : ")");
    }

    @SuppressWarnings("unchecked")
    public static <T extends Comparable<T>> Class<Range<T>> type() {
        return (Class<Range<T>>) (Class<?>) Range.class;
    }
}
