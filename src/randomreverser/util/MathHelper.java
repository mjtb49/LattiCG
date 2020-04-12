package randomreverser.util;

import java.math.BigDecimal;

public class MathHelper {
    public static BigDecimal gcd(BigDecimal a, BigDecimal b) {
        if (a.compareTo(BigDecimal.ZERO) == 0)
            return b;
        if (b.compareTo(BigDecimal.ZERO) == 0)
            return a;
        if (a.compareTo(b) == 0)
            return a;
        if (a.compareTo(b) > 0)
            return gcd(a.remainder(b), b);
        return gcd(a,  b.remainder(a));
    }

    public static double gcd(double a, double b) {
        while(b != 0) {
            a %= b;
            double temp = a;
            a = b;
            b = temp;
        }

        return a;
    }

    public static int countTrailingZeroes(long v) {
        int c;  // output: c will count v's trailing zero bits,
        // so if v is 1101000 (base 2), then c will be 3
        v = (v ^ (v - 1)) >> 1;  // Set v's trailing 0s to 1s and zero rest

        for(c = 0; v != 0; c++)  {
            v >>>= 1;
        }

        return c;
    }

    public static long modInverse(long x, int mod) { //Fast method for modular inverse mod powers of 2
        long inv = 0;
        long b = 1;

        for(int i = 0; i < mod; i++) {
            if((b & 1) == 1) {
                inv |= 1L << i;
                b = (b - x) >> 1;
            } else {
                b >>= 1;
            }
        }

        return inv;
    }

}
