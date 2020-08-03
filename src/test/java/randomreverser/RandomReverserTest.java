package randomreverser;

import randomreverser.util.Rand;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class RandomReverserTest {
    public static void main(String[] args) {
        RandomReverser device = new RandomReverser();
        Random r = new Random();

        long seed = r.nextLong()  & ((1L << 48) - 1);
        System.out.println("Seed: " +seed);

        r.setSeed(seed ^ 0x5deece66dL);
        long structureSeed = r.nextLong() & ((1L << 48) - 1);

        //Dark arts to make the first entry a valid seed. The consume nextFloat calls are entirely to go back in time. Not sponsored usage.
        device.consumeNextFloatCalls(1);
        device.addModConstraint((structureSeed & 0xffff_ffffL) << 16, (((structureSeed+1) & 0xffff_ffffL) << 16) - 1, (1L << 48));
        device.consumeNextFloatCalls(-2);
        if ((structureSeed & 0x8000_0000L) == 0) { //Was the lower half negative
            device.addModConstraint((structureSeed >> 32) << 16, ((1 + (structureSeed >> 32)) << 16) - 1, (1L << 32));
        } else {
            device.addModConstraint((1 + (structureSeed >> 32)) << 16, ((2 + (structureSeed >> 32)) << 16) - 1, (1L << 32));
        }

       // device.setVerbose(true);
        long start = System.nanoTime();

        AtomicInteger count = new AtomicInteger(0);
        device.findAllValidSeeds().forEach(s -> {
            count.incrementAndGet();
            System.out.println(s);
        });
        if (count.get() == 1)
            System.out.println("Found " + count + " seed.");
        else System.out.println("Found " + count + " seeds.");

        long end = System.nanoTime();

        System.out.printf("elapsed: %.2fs%n", (end - start) * 1e-9);
    }
}
