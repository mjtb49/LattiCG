package test;

import randomreverser.RandomReverser;
import randomreverser.util.Rand;

import java.util.ArrayList;
import java.util.Random;

public class RandomReverserTest {
    public static void main(String[] args) {

        final int NUM_COBBLE = 16;
        System.out.println();

        RandomReverser device = new RandomReverser();
        /*String pattern = "1101110111110110111111001110101011111110111111111110110100";
        device.addNextIntCall(16,2,2);
        device.addNextIntCall(128, 16, 16);
        device.addNextIntCall(16,6,6);
        device.consumeNextIntCalls(2);
        for (char c: pattern.toCharArray()) {
            if (c == '0') {
                device.addNextIntCall(4,0,0);
            } else {
                device.consumeNextIntCalls(1);
            }
        }*/
        for (int i = 0; i < 3; i++) {
            device.addNextFloatCall(0.85f,1.0f);
            device.addNextFloatCall(0.175f,0.225f);
            device.addNextFloatCall(0.85f,1.0f);
            device.addNextFloatCall(0.675f,0.725f);
        }
        device.setVerbose(true);
        double time = System.currentTimeMillis();
        ArrayList<Long> results = device.findAllValidSeeds();
        System.out.print("Took " + (System.currentTimeMillis() - time) / 1000 + " seconds to find " + results.size() + " seed");
        if (results.size() == 1)
            System.out.println(".");
        else System.out.println("s.");
        for (long seed:results) {
            Random ra = new Random(seed ^ 0x5deece66dL);
            if (ra.nextInt(12) == 11)
                System.out.println(seed);
        }
    }
}
