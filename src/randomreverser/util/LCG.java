package randomreverser.util;

import java.util.Objects;

public class LCG {

    public static final LCG JAVA = new LCG(0x5DEECE66DL, 0xBL, 1L << 48);

    public final long multiplier;
    public final long addend;
    public final long modulus;

    private final boolean canMask;

    public LCG(long multiplier, long addend, long modulus) {
        this.multiplier = multiplier;
        this.addend = addend;
        this.modulus = modulus;

        this.canMask = (this.modulus & -this.modulus) == this.modulus;
    }

    public long nextSeed(long seed) {
        return mod(seed * multiplier + addend);
    }

    public LCG combine(long steps) {
        long multiplier = 1;
        long addend = 0;

        long intermediateMultiplier = this.multiplier;
        long intermediateAddend = this.addend;

        for(long k = steps; k != 0; k >>>= 1) {
            if((k & 1) != 0) {
                multiplier *= intermediateMultiplier;
                addend = intermediateMultiplier * addend + intermediateAddend;
            }

            intermediateAddend = (intermediateMultiplier + 1) * intermediateAddend;
            intermediateMultiplier *= intermediateMultiplier;
        }

        multiplier = mod(multiplier);
        addend = mod(addend);

        return new LCG(multiplier, addend, this.modulus);
    }

    public LCG invert() {
        return combine(-1);
    }

    public long mod(long n) {
        if (canMask) {
            return n & (modulus - 1);
        } else {
            return n % modulus; // TODO: does not work for modulus > 2^32
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this)return true;
        if(!(obj instanceof LCG))return false;
        LCG lcg = (LCG)obj;
        return this.multiplier == lcg.multiplier &&
                this.addend == lcg.addend &&
                this.modulus == lcg.modulus;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.multiplier, this.addend, this.modulus);
    }

    @Override
    public String toString() {
        return "LCG{" + "multiplier=" + this.multiplier +
                ", addend=" + this.addend + ", modulo=" + this.modulus + '}';
    }

}
