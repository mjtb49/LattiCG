package com.seedfinding.latticg.util;

import java.util.Objects;

public final class Pair<A, B> {

    private final A a;
    private final B b;

    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public Pair(Pair<? extends A, ? extends B> other) {
        this(other.a, other.b);
    }

    public A getFirst() {
        return this.a;
    }

    public B getSecond() {
        return this.b;
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other == null || other.getClass() != Pair.class) return false;
        Pair<?, ?> that = (Pair<?, ?>) other;
        return Objects.equals(this.a, that.a) && Objects.equals(this.b, that.b);
    }

    @Override
    public String toString() {
        return "(" + a + ", " + b + ")";
    }

}
