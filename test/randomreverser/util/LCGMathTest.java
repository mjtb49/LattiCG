package randomreverser.util;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class LCGMathTest {

    @Test
    public void testGcdBigDecimal() {
        assertEquals(BigDecimal.valueOf(1), LCGMath.gcd(BigDecimal.valueOf(999836362321L), BigDecimal.valueOf(999836358007L)));
    }

    @Test
    public void testGcdDouble1() {
        assertEquals(1, LCGMath.gcd(1, 1), 0);
    }

    @Test
    public void testGcdDouble2() {
        assertEquals(23, LCGMath.gcd(69, 230), 0);
    }

    @Test
    public void testGcdDouble3() {
        assertEquals(23, LCGMath.gcd(230, 69), 0);
    }

    @Test
    public void testGcdDouble4() {
        assertEquals(1, LCGMath.gcd(123136693, 234799), 0);
    }

    @Test
    public void testGcdDouble5() {
        assertEquals(0.25, LCGMath.gcd(0.75, 1), 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testModInverse0() {
        //noinspection ResultOfMethodCallIgnored
        LCGMath.modInverse(0, 16);
    }

    @Test
    public void testModInverse1() {
        assertEquals(1, LCGMath.modInverse(1, 16));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testModInverse2() {
        //noinspection ResultOfMethodCallIgnored
        LCGMath.modInverse(2, 16);
    }

    @Test
    public void testModInverse5deece66d() {
        assertEquals(0xdfe05bcb1365L, LCGMath.modInverse(0x5deece66dL, 48));
    }

    @Test
    public void testModInverseDfe05bcb1365() {
        assertEquals(0x5deece66dL, LCGMath.modInverse(0xdfe05bcb1365L, 48));
    }

}
