package main.java.seedutils.math;

import main.java.seedutils.magic.RandomReverser;

public class RandomReverserTest {
    public static void main(String[] args) {
        final int NUM_COBBLE = 14;
        RandomReverser device = new RandomReverser();
        device.addNextIntCall(16, 1, 2);
        device.addNextIntCall(256, 1, 2);
        device.addNextIntCall(16, 1, 2);

        for (int i = 0; i < NUM_COBBLE; i++ )
            device.addNextIntCall(4, 1, 2);

        double time = System.currentTimeMillis();
        device.findAllValidSeeds();
        System.out.println(System.currentTimeMillis() - time);
    }
}
