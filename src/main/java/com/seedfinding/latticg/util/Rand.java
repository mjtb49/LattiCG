package com.seedfinding.latticg.util;

import java.lang.reflect.Field;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class Rand {

    private final LCG lcg;
    private long seed;

    private Rand(LCG lcg) {
        this.lcg = lcg;
    }

    public static Rand ofInternalSeed(LCG lcg, long seed) {
        Rand rand = new Rand(lcg);
        rand.setInternalSeed(seed);
        return rand;
    }

    public static Rand ofSeedScrambled(LCG lcg, long seed) {
        Rand rand = new Rand(lcg);
        rand.setSeedScrambled(seed);
        return rand;
    }

    public static Rand ofInternalSeed(long seed) {
        return ofInternalSeed(LCG.JAVA, seed);
    }

    public static Rand ofSeedScrambled(long seed) {
        return ofSeedScrambled(LCG.JAVA, seed);
    }

    public static Rand copyOf(Rand other) {
        Rand rand = new Rand(other.lcg);
        rand.seed = other.seed;
        return rand;
    }

    public static Rand copyOf(Random random) {
        if (random instanceof RandomWrapper) {
            return copyOf(((RandomWrapper) random).delegate);
        } else if (random.getClass() == Random.class) {
            try {
                AtomicLong seed = (AtomicLong) SeedFieldHolder.FIELD.get(random);
                return ofInternalSeed(seed.get());
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new IllegalArgumentException("Don't know how to Rand.copyOf() an instance of " + random.getClass().getName() + ", it may not even be an LCG!");
        }
    }

    public long getSeed() {
        return this.seed;
    }

    public void setInternalSeed(long seed) {
        this.seed = lcg.mod(seed);
    }

    public void setSeedScrambled(long seed) {
        setInternalSeed(seed ^ LCG.JAVA.multiplier);
    }

    public int next(int bits) {
        this.seed = lcg.nextSeed(this.seed);
        return (int) (this.seed >>> (48 - bits));
    }

    public void advance(long calls) {
        this.advance(lcg.combine(calls));
    }

    public void advance(LCG skip) {
        this.seed = skip.nextSeed(this.seed);
    }

    public boolean nextBoolean() {
        return this.next(1) == 1;
    }

    public int nextInt() {
        return this.next(32);
    }

    public int nextInt(int bound) {
        if (bound <= 0) {
            throw new IllegalArgumentException("bound must be positive");
        }

        if ((bound & -bound) == bound) {
            return (int) ((bound * (long) this.next(31)) >> 31);
        }

        int bits, value;

        do {
            bits = this.next(31);
            value = bits % bound;
        } while (bits - value + (bound - 1) < 0);

        return value;
    }

    public float nextFloat() {
        return this.next(24) / ((float) (1 << 24));
    }

    public long nextLong() {
        return ((long) (this.next(32)) << 32) + this.next(32);
    }

    public double nextDouble() {
        return (((long) this.next(26) << 27) + this.next(27)) / (double) (1L << 53);
    }

    public Random asRandomView() {
        return new RandomWrapper(this);
    }

    public Random copyToRandom() {
        return copyOf(this).asRandomView();
    }

    public Random copyToThreadSafeRandom() {
        if (!lcg.equals(LCG.JAVA)) {
            throw new UnsupportedOperationException("Rand.copyToThreadSafeRandom() only works for LCG.JAVA");
        }
        return new Random(seed ^ lcg.multiplier);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Rand)) return false;
        Rand rand = (Rand) obj;
        return rand.getSeed() == this.getSeed();
    }

    @Override
    public String toString() {
        return "Rand{" + "seed=" + this.seed + '}';
    }

    private static final class RandomWrapper extends Random {
        private final Rand delegate;

        private RandomWrapper(Rand delegate) {
            this.delegate = delegate;
        }

        @Override
        protected int next(int bits) {
            return delegate.next(bits);
        }

        @Override
        public void setSeed(long seed) {
            delegate.setSeedScrambled(seed);
        }

        @Override
        public double nextGaussian() {
            throw new UnsupportedOperationException("Rand.asRandomView() and Rand.copyToRandom() do not support nextGaussian()! Use Rand.copyToThreadSafeRandom() instead.");
        }
    }

    private static class SeedFieldHolder {
        static final Field FIELD;

        static {
            try {
                FIELD = Random.class.getDeclaredField("seed");
                FIELD.setAccessible(true);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }
    }

}

