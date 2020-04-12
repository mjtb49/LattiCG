package test;

import randomreverser.RandomReverser;
import java.util.ArrayList;
import java.util.Random;

public class RandomReverserTest {
    public static void main(String[] args) {

        final int NUM_COBBLE = 16;

        Random r = new Random();
        long s = r.nextLong();
        System.out.println(s & ((1L << 48)-1));
        r.setSeed(s^0x5deece66dL);
        RandomReverser device = new RandomReverser();
       /* int k;
        for (int i = 0; i < NUM_COBBLE; i++ ) {
            k = r.nextInt(8);
            device.addNextIntCall(8,k,k);
        }*/
        device.addNextLongCall(0,0);
        device.setVerbose(true);
        double time = System.currentTimeMillis();
        ArrayList<Long> results = device.findAllValidSeeds();
        System.out.print("Took " + (System.currentTimeMillis() - time) / 1000 + " seconds to find " + results.size() + " seed");
        if (results.size() == 1)
            System.out.println(".");
        else System.out.println("s.");
        for (long seed:results)
            System.out.println(seed);
    }
}
