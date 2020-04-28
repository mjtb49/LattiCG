package randomreverser;

import java.util.ArrayList;

public class RandomReverserTest {
    public static void main(String[] args) {

        RandomReverser device = new RandomReverser();
        device.addNextIntCall(16,11,11);
        device.addNextIntCall(256, 20, 20);
        device.addNextIntCall(16,11,11);
        for (int i = 0; i < 18; i++) {
            device.addNextIntCall(4,8,8);
        }
        device.setVerbose(true);
        double time = System.currentTimeMillis();
        ArrayList<Long> results = device.findAllValidSeeds();
        System.out.println(results);
        System.out.println("Took "+ ((System.currentTimeMillis() - time) /1000) + " seconds to find "+results.size()+" seeds.");
    }
}