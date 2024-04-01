package com.seedfinding.latticg;

import com.seedfinding.latticg.util.LCG;
import com.seedfinding.latticg.util.Rand;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class RandomReverserTest {
    public static void main(String[] args) {
        JavaRandomReverser device = new JavaRandomReverser(new ArrayList<>());
        final int k = 5;
        for (int i =0; i < k; i++)
            device.addNextIntCall(20000,20000-10,10);

        device.setVerbose(true);
        long start = System.nanoTime();
        AtomicInteger count = new AtomicInteger(0);
        device.findAllValidSeeds().forEach(s -> {
            count.incrementAndGet();
            String string = s + "";
            Random r = new Random(s ^ 0x5deece66dL);
            for (int i =0; i < k; i++)
                string += " " + r.nextInt(20000);
            System.out.println(string);
        });
        if (count.get() == 1)
            System.out.println("Found " + count + " seed.");
        else System.out.println("Found " + count + " seeds.");

        long end = System.nanoTime();

        System.out.printf("elapsed: %.2fs%n", (end - start) * 1e-9);
    }
}
