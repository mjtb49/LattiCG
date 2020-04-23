package randomreverser.util;

import org.junit.Assert;

import java.math.BigDecimal;

public class MoreAssert {

    public static void assertBigDecimalEquals(BigDecimal expected, BigDecimal actual) {
        assertBigDecimalEquals(expected, actual, BigDecimal.ZERO);
    }

    public static void assertBigDecimalEquals(BigDecimal expected, BigDecimal actual, BigDecimal tolerance) {
        Assert.assertTrue(expected.subtract(actual).abs().compareTo(tolerance) <= 0);
    }

}
