package randomreverser.reversal;

import org.junit.Test;
import randomreverser.reversal.asm.ReversalASM;
import randomreverser.reversal.calltype.JavaCalls;
import randomreverser.util.LCG;

import java.util.Arrays;
import java.util.stream.LongStream;

import static org.junit.Assert.*;

public class DynamicProgramTest {

    @Test
    public void testNoCalls() {
        // mainly just to check it doesn't crash on this case
        assertTrue(DynamicProgram.create(LCG.JAVA).reverse().findAny().isPresent());
    }

    @Test
    public void testInitialSkip() {
        assertStreamEquals(
                DynamicProgram.create(LCG.JAVA)
                        .skip(1)
                        .add(JavaCalls.nextFloat(), 0f)
                        .add(JavaCalls.nextFloat(), 0f)
                        .reverse(),
                120305458776662L,
                189728072343791L,
                259150685910920L
        );
    }

    @Test
    public void testNextFloatsZero() {
        assertStreamEquals(
                DynamicProgram.create(LCG.JAVA)
                        .add(JavaCalls.nextFloat(), 0f)
                        .add(JavaCalls.nextFloat(), 0f)
                        .reverse(),
                107048004364969L,
                97890873098190L,
                88733741831411L
        );
        System.out.println(ReversalASM.toAsm(Program.builder(LCG.JAVA)
                .add(JavaCalls.nextFloat())
                .add(JavaCalls.nextFloat())
                .build(), false));
    }

    @Test
    public void testNextFloatsGap() {
        assertStreamEquals(
                DynamicProgram.create(LCG.JAVA)
                        .add(JavaCalls.nextFloat(), 0f)
                        .skip(1)
                        .add(JavaCalls.nextFloat(), 0f)
                        .reverse(),
                237852402139752L
        );
    }

    @Test
    public void test16NextInt8() {
        assertStreamEquals(
                DynamicProgram.create(LCG.JAVA)
                        .add(JavaCalls.nextInt(8), 0)
                        .add(JavaCalls.nextInt(8), 0)
                        .add(JavaCalls.nextInt(8), 0)
                        .add(JavaCalls.nextInt(8), 0)
                        .add(JavaCalls.nextInt(8), 0)
                        .add(JavaCalls.nextInt(8), 0)
                        .add(JavaCalls.nextInt(8), 0)
                        .add(JavaCalls.nextInt(8), 0)
                        .add(JavaCalls.nextInt(8), 0)
                        .add(JavaCalls.nextInt(8), 0)
                        .add(JavaCalls.nextInt(8), 0)
                        .add(JavaCalls.nextInt(8), 0)
                        .add(JavaCalls.nextInt(8), 0)
                        .add(JavaCalls.nextInt(8), 0)
                        .add(JavaCalls.nextInt(8), 0)
                        .add(JavaCalls.nextInt(8), 0)
                        .reverse(),
                211804640834172L
        );
    }

    @Test
    public void testNextInt8UpperBound() {
        assertStreamEquals(
                DynamicProgram.create(LCG.JAVA)
                        .add(JavaCalls.nextInt(8), 7)
                        .add(JavaCalls.nextInt(8), 7)
                        .add(JavaCalls.nextInt(8), 7)
                        .add(JavaCalls.nextInt(8), 7)
                        .add(JavaCalls.nextInt(8), 7)
                        .add(JavaCalls.nextInt(8), 7)
                        .add(JavaCalls.nextInt(8), 7)
                        .add(JavaCalls.nextInt(8), 7)
                        .add(JavaCalls.nextInt(8), 7)
                        .add(JavaCalls.nextInt(8), 7)
                        .add(JavaCalls.nextInt(8), 7)
                        .add(JavaCalls.nextInt(8), 7)
                        .add(JavaCalls.nextInt(8), 7)
                        .add(JavaCalls.nextInt(8), 7)
                        .add(JavaCalls.nextInt(8), 7)
                        .add(JavaCalls.nextInt(8), 7)
                        .reverse(),
                224925009941018L
        );
    }

    private void assertStreamEquals(LongStream stream, long... expected) {
        long[] actual = stream.toArray();
        Arrays.sort(expected);
        Arrays.sort(actual);
        assertArrayEquals(expected, actual);
    }

}
