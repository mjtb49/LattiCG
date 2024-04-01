package com.seedfinding.latticg;

import com.seedfinding.latticg.util.LCG;
import com.seedfinding.latticg.util.Rand;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class RandomReverserTest {
    public static void main(String[] args) {
        LCG lcg = new LCG(7567025607324980273L, 5279421, 0);
        RandomReverser device = new RandomReverser(lcg, new ArrayList<>());
        final int k = 2;
        for (int i =0; i < k; i++)
            device.addMeasuredSeed(0, 20000000000L);

        device.setVerbose(true);
        long start = System.nanoTime();
        AtomicInteger count = new AtomicInteger(0);
        device.findAllValidSeeds().forEach(s -> {
            count.incrementAndGet();
            String string = s + "";
            //Random r = new Random(s ^ 0x5deece66dL);
            for (int i =0; i < k; i++) {
                s = lcg.nextSeed(s);
                string += " " + s;
            }
            System.out.println(string);
        });
        if (count.get() == 1)
            System.out.println("Found " + count + " seed.");
        else System.out.println("Found " + count + " seeds.");

        long end = System.nanoTime();

        System.out.printf("elapsed: %.2fs%n", (end - start) * 1e-9);
    }
}
