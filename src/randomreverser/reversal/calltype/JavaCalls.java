package randomreverser.reversal.calltype;

import randomreverser.reversal.asm.ParseException;
import randomreverser.reversal.asm.StringParser;
import randomreverser.reversal.asm.Token;
import randomreverser.util.LCG;
import randomreverser.util.Pair;

import java.math.BigInteger;

public final class JavaCalls {

    private JavaCalls() {
    }

    public static RangeCallType<Boolean> nextBoolean() {
        return new NextBooleanCallType();
    }

    public static RangeCallType<Float> nextFloat() {
        return new NextFloatCallType();
    }

    public static RangeCallType<Integer> nextInt(int bound) {
        if ((bound & -bound) != bound) {
            // TODO: support non-powers-of-2
            throw new UnsupportedOperationException("Only powers of 2 are currently supported!");
        }

        return new NextIntPowerOf2CallType(bound);
    }

    static class NextBooleanCallType extends RangeCallType<Boolean> {
        public NextBooleanCallType() {
            super(Boolean.class, LCG.JAVA, 1, false, true, 1L << 47);
        }

        @Override
        protected long minSeedFor(Boolean thing) {
            return thing ? 1L << 47 : 0;
        }

        @Override
        public void writeOperands(StringBuilder output, boolean verbose) {
        }

        @Override
        public void readOperands(StringParser parser) {
        }
    }

    static class NextFloatCallType extends RangeCallType<Float> {
        public NextFloatCallType() {
            super(Float.class, LCG.JAVA, 1, 0f, 1f - 0x1.0p-24f, 1 << 24);
        }

        @Override
        protected long minSeedFor(Float thing) {
            return (long) (thing * (1 << 24));
        }

        @Override
        public void writeOperands(StringBuilder output, boolean verbose) {
        }

        @Override
        public void readOperands(StringParser parser) {
        }
    }

    static class NextIntPowerOf2CallType extends RangeCallType<Integer> {
        private int bound;

        public NextIntPowerOf2CallType() {
            super(Integer.class, LCG.JAVA, 1, 0, 1, 1L << 48);
        }

        public NextIntPowerOf2CallType(int bound) {
            super(Integer.class, LCG.JAVA, 1, 0, bound - 1, (1L << 48) / bound);
            this.bound = bound;
        }

        @Override
        protected long minSeedFor(Integer thing) {
            return (1L << 48) / bound * thing;
        }

        @Override
        public void writeOperands(StringBuilder output, boolean verbose) {
            output.append(bound);
        }

        @Override
        public void readOperands(StringParser parser) {
            Pair<BigInteger, Token> boundPair = parser.consumeInteger();
            int bound = boundPair.getFirst().intValue();
            if (bound <= 0 || (bound & -bound) != bound) {
                throw new ParseException("Bound '" + boundPair.getSecond().getText() + "' is not a power of 2", boundPair.getSecond());
            }
            this.bound = bound;
            setMaxPossible(bound - 1);
            setSeedsPerValue((1L << 48) / bound);
        }
    }
}
