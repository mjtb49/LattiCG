package test;

import randomreverser.RandomReverser;
import java.util.ArrayList;

public class RandomReverserTest {
    public static void main(String[] args) {

        RandomReverser device = new RandomReverser();
        for (int i = 0; i < 3; i++) {
            device.addNextFloatCall(0.85f,1.0f);
            device.addNextFloatCall(0.175f,0.225f);
            device.addNextFloatCall(0.85f,1.0f);
            device.addNextFloatCall(0.675f,0.725f);
        }
        device.setVerbose(true);
        double time = System.currentTimeMillis();
        ArrayList<Long> results = device.findAllValidSeeds();
        System.out.println(results);
        System.out.println("Took "+ ((System.currentTimeMillis() - time) /1000) + " seconds");
    }
}
