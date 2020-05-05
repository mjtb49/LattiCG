package randomreverser.reversal.calltype;

import randomreverser.util.LCG;

public final class JavaCalls {

    private JavaCalls() {
    }

    public static RangeCallType<Boolean> nextBoolean() {
        return new RangeCallType<>(Boolean.class, LCG.JAVA, 1, false, true, 1L << 47) {
            @Override
            protected long minSeedFor(Boolean thing) {
                return thing ? 1L << 47 : 0;
            }
        };
    }

    public static RangeCallType<Float> nextFloat() {
        return new RangeCallType<>(Float.class, LCG.JAVA, 1, 0f, 1f - 0x1.0p-24f, 1 << 24) {
            @Override
            protected long minSeedFor(Float thing) {
                return (long) (thing * (1 << 24));
            }
        };
    }

    public static RangeCallType<Integer> nextInt(int bound) {
        if ((bound & -bound) != bound) {
            // TODO: support non-powers-of-2
            throw new UnsupportedOperationException("Only powers of 2 are currently supported!");
        }

        final long seedsPerValue = (1L << 48) / bound;
        return new RangeCallType<>(Integer.class, LCG.JAVA, 1, 0, bound - 1, seedsPerValue) {
            @Override
            protected long minSeedFor(Integer thing) {
                return seedsPerValue * thing;
            }
        };
    }

}
