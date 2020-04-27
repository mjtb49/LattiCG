package randomreverser;

import java.util.ArrayList;

public class RandomReverserTest {
    public static void main(String[] args) {

        RandomReverser device = new RandomReverser();
        for (int i = 0; i < 12; i++) {
            device.addNextFloatCall(0.9f,1.0f);
        }
        device.setVerbose(true);
        double time = System.currentTimeMillis();
        ArrayList<Long> results = device.findAllValidSeeds();
        System.out.println(results);
        System.out.println("Took "+ ((System.currentTimeMillis() - time) /1000) + " seconds");
    }
}