package com.seedfinding.latticg.math.component;

import com.seedfinding.latticg.math.lattice.optimization.BKZ;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BigFractionTest {

    @Test
    public void testConstructDivideZero() {
        assertThrows(ArithmeticException.class, () -> new BigFraction(1, 0));

    }

    @Test
    public void testConstructZero() {
        BigFraction zero = new BigFraction(0, 47);
        assertEquals(BigInteger.ZERO, zero.getNumerator());
        assertEquals(BigInteger.ONE, zero.getDenominator());
    }

    @Test
    public void testConstructNegativeDenominator1() {
        BigFraction minusHalf = new BigFraction(1, -2);
        assertEquals(BigInteger.valueOf(-1), minusHalf.getNumerator());
        assertEquals(BigInteger.valueOf(2), minusHalf.getDenominator());
    }

    @Test
    public void testConstructNegativeDenominator2() {
        BigFraction half = new BigFraction(-1, -2);
        assertEquals(BigInteger.ONE, half.getNumerator());
        assertEquals(BigInteger.valueOf(2), half.getDenominator());
    }

    @Test
    public void testConstructSimplify() {
        BigFraction half = new BigFraction(2, 4);
        assertEquals(BigInteger.ONE, half.getNumerator());
        assertEquals(BigInteger.valueOf(2), half.getDenominator());
    }

    @Test
    public void testToBigDecimal() {
        assertEquals(BigDecimal.valueOf(0.5), BigFraction.HALF.toBigDecimal(MathContext.UNLIMITED));
    }

    @Test
    public void testAddFractionWithFraction() {
        BigFraction a = new BigFraction(7, 11);
        BigFraction b = new BigFraction(13, 17);
        assertEquals(new BigFraction(262, 187), a.add(b));
    }

    @Test
    public void testAddFractionWithInteger() {
        BigFraction a = new BigFraction(7, 11);
        assertEquals(new BigFraction(150, 11), a.add(13));
    }

    @Test
    public void testSubtractFractionWithFraction() {
        BigFraction a = new BigFraction(7, 11);
        BigFraction b = new BigFraction(13, 17);
        assertEquals(new BigFraction(-24, 187), a.subtract(b));
    }

    @Test
    public void testSubtractFractionWithInteger() {
        BigFraction a = new BigFraction(7, 11);
        assertEquals(new BigFraction(-136, 11), a.subtract(13));
    }

    @Test
    public void testMultiplyFractionWithFraction() {
        BigFraction a = new BigFraction(7, 11);
        BigFraction b = new BigFraction(13, 17);
        assertEquals(new BigFraction(91, 187), a.multiply(b));
    }

    @Test
    public void testMultiplyFractionWithInteger() {
        BigFraction a = new BigFraction(7, 11);
        assertEquals(new BigFraction(91, 11), a.multiply(13));
    }

    @Test
    public void testDivisionFractionWithFraction() {
        BigFraction a = new BigFraction(7, 11);
        BigFraction b = new BigFraction(13, 17);
        assertEquals(new BigFraction(119, 143), a.divide(b));
    }

    @Test
    public void testDivisionFractionWithInteger() {
        BigFraction a = new BigFraction(7, 11);
        assertEquals(new BigFraction(7, 143), a.divide(13));
    }

    @Test
    public void testDivisionByZero() {
        assertThrows(ArithmeticException.class, () -> new BigFraction(1).divide(0));
    }

    @Test
    public void testNegateZero() {
        assertEquals(BigFraction.ZERO, BigFraction.ZERO.negate());
    }

    @Test
    public void testNegatePositive() {
        assertEquals(BigFraction.MINUS_ONE, BigFraction.ONE.negate());
    }

    @Test
    public void testNegateNegative() {
        assertEquals(BigFraction.ONE, BigFraction.MINUS_ONE.negate());
    }

    @Test
    public void testReciprocal() {
        assertEquals(BigFraction.HALF, new BigFraction(2).reciprocal());
    }

    @Test
    public void testReciprocalZero() {
        assertThrows(ArithmeticException.class, BigFraction.ZERO::reciprocal);
    }

    @Test
    public void testFloorZero() {
        assertEquals(BigInteger.ZERO, BigFraction.ZERO.floor());
    }

    @Test
    public void testFloorPositiveInteger() {
        assertEquals(BigInteger.ONE, BigFraction.ONE.floor());
    }

    @Test
    public void testFloorNegativeInteger() {
        assertEquals(BigInteger.valueOf(-1), BigFraction.MINUS_ONE.floor());
    }

    @Test
    public void testFloorPositive() {
        assertEquals(BigInteger.ONE, new BigFraction(3, 2).floor());
    }

    @Test
    public void testFloorNegative() {
        assertEquals(BigInteger.valueOf(-2), new BigFraction(-3, 2).floor());
    }

    @Test
    public void testCeilZero() {
        assertEquals(BigInteger.ZERO, BigFraction.ZERO.ceil());
    }

    @Test
    public void testCeilPositiveInteger() {
        assertEquals(BigInteger.ONE, BigFraction.ONE.ceil());
    }

    @Test
    public void testCeilNegativeInteger() {
        assertEquals(BigInteger.valueOf(-1), BigFraction.MINUS_ONE.ceil());
    }

    @Test
    public void testCeilPositive() {
        assertEquals(BigInteger.valueOf(2), new BigFraction(3, 2).ceil());
    }

    @Test
    public void testCeilNegative() {
        assertEquals(BigInteger.valueOf(-1), new BigFraction(-3, 2).ceil());
    }

    @Test
    public void testRoundZero() {
        assertEquals(BigInteger.ZERO, BigFraction.ZERO.round());
    }

    @Test
    public void testRoundPositiveInteger() {
        assertEquals(BigInteger.ONE, BigFraction.ONE.round());
    }

    @Test
    public void testRoundNegativeInteger() {
        assertEquals(BigInteger.valueOf(-1), BigFraction.MINUS_ONE.round());
    }

    @Test
    public void testRoundHalf() {
        assertEquals(BigInteger.ONE, BigFraction.HALF.round());
    }

    @Test
    public void testRoundMinusHalf() {
        assertEquals(BigInteger.ZERO, new BigFraction(-1, 2).round());
    }

    @Test
    public void testRoundingLessThanHalfPositive() {
        assertEquals(BigInteger.ONE, new BigFraction(4, 3).round());
    }

    @Test
    public void testRoundingMoreThanHalfPositive() {
        assertEquals(BigInteger.valueOf(2), new BigFraction(5, 3).round());
    }

    @Test
    public void testRoundingLessThanHalfNegative() {
        assertEquals(BigInteger.valueOf(-2), new BigFraction(-5, 3).round());
    }

    @Test
    public void testRoundingMoreThanHalfNegative() {
        assertEquals(BigInteger.valueOf(-1), new BigFraction(-4, 3).round());
    }

    @Test
    public void testSignumZero() {
        assertEquals(0, BigFraction.ZERO.signum());
    }

    @Test
    public void testSignumPositive() {
        assertEquals(1, BigFraction.HALF.signum());
    }

    @Test
    public void testSignumNegative() {
        assertEquals(-1, new BigFraction(-47).signum());
    }

    @Test
    public void testAbsZero() {
        assertEquals(BigFraction.ZERO, BigFraction.ZERO.abs());
    }

    @Test
    public void testAbsPositive() {
        assertEquals(BigFraction.ONE, BigFraction.ONE.abs());
    }

    @Test
    public void testAbsNegative() {
        assertEquals(BigFraction.ONE, BigFraction.MINUS_ONE.abs());
    }

    @Test
    public void testCompareEqual() {
        assertEquals(0, BigFraction.ONE.compareTo(new BigFraction(1)));
    }

    @Test
    public void testCompare() {
        BigFraction a = new BigFraction(7, 11);
        BigFraction b = new BigFraction(13, 17);
        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
    }

    @Test
    public void testExpSmall() {
        BigFraction a = new BigFraction(42, 4242);
        BigFraction res_a = BigFractionUtil.fromString("40082549934887931699451/39687651175170088375488");
        BigFraction a_exp = a.exp();
        assertEquals(res_a, a_exp);
        assertEquals(1.009950167067708, a_exp.toDouble(), Double.MIN_VALUE);
    }

    @Test
    public void testExpBig() {
        BigFraction a = new BigFraction(42424, 1);
        BigFraction res_a = BigFractionUtil.fromString("3478457471619510415813461337374760474907/2835");
        BigFraction a_exp = a.exp();
        assertEquals(res_a, a_exp);
        assertEquals(1.226969125791714E36, a_exp.toDouble(), Double.MIN_VALUE);
    }


    @Test
    public void testLogSmall() {
        BigFraction a = new BigFraction(42, 4242);
        BigFraction res_a = BigFractionUtil.fromString("-98601131932888888988908832771394766433560218412208808338046748055037946149167894609927563519053019056126210720049172602234973434886222741255178199107540894181380226253624378900279434769637430980329694869410203657773574432626502988978359799395741486671546787980708109622793364844762101229804397243490265248765031199931171097475265077549241074932584257154200611699556902594130337502933587983096623717134645496037198055580508573367237106452586488955595863346095661547863996687147749100/21598424499781852644676311232204087656365441235431832054362300070047924348721950840422895372496181317612755052248477680079467777102298116811857981189267618091190819804427458075058634438324217648497573507123384254542011381514583622410678158408607023567917820877608079720122425195853219176786609335216939255182194601974283591509617037999332434785232060391993647607130790391561913742205643247783787868202874028354473239816012006384521735246852688393910303903210952704441588025362484541");
        BigFraction a_log = a.log();
        assertEquals(res_a, a_log);
        assertEquals(-4.565200204018806, a_log.toDouble(), Double.MIN_VALUE); // -4.61512051684126
    }

    @Test
    public void testLogBig() {
        BigFraction a = new BigFraction(42424, 1);
        BigFraction res_a = BigFractionUtil.fromString("7093284441181875284251919338357006426816644803547720717661955576932066481114845908456578246993517780947935717689273130557308741601793255315685460782503308609110170401515385771879543980451299802275098529585336364202505360572602442739172960549018500573276499149324593914691078024279116336600679510003136317176619126362917543915851630812751960465173228645473058551304389814199836739297522806635418944867177252358448628752936213274960383690791080190350283439652763048869928825416300929877459457998621589425337300800652049828776736243462991753035364411475316426520839349031363022093379195170579230485748418971366683248619786645864644584585301575282477319675271040410408702116016051782561298018848388474554074864500505788115706725071308816669577165695462956772413300879971715034670910032115671424059642118701821121740098158490528703534384363444143604824541260157949802606067720713171621770000475661287878188092756967243/665694217289583521772725912731305942096172327753012523065256555169762699727112967973454185265967566881224259145418562868429830468025169533180873566869825580135862492003119392762642767922696639187729960441385409711043432819533226435125263359791740324968442522092689942668383578229219305316367825367856167449524589289329882326035480017696674682266571746913131041167343822566736475547218993906355631846999829976354153222190690245398648609127613294336686217938548491063157806161415530610270252509508281946182250976562500000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
        BigFraction a_log = a.log();
        assertEquals(res_a, a_log);
        assertEquals(10.65546951881697, a_log.toDouble(), Double.MIN_VALUE);
    }

    @Test
    public void testLog1() {
        BigFraction a = new BigFraction(1, 1);
        BigFraction res_a = BigFractionUtil.fromString("0/1");
        BigFraction a_log = a.log();
        assertEquals(res_a, a_log);
        assertEquals(0, a_log.toDouble(), Double.MIN_VALUE);
    }

}
