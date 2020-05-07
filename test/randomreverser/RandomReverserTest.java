package randomreverser;

import java.util.List;
import java.util.stream.Collectors;

public class RandomReverserTest {
    public static void main(String[] args) {
        RandomReverser device = new RandomReverser();

        for (int i = 0; i < 12; i++) {
            device.addNextFloatCall(0.9f,1.0f);
            //device.consumeNextFloatCalls(1);
        }

        device.setVerbose(true);

        long start = System.nanoTime();
        // List<Long> results = device.findAllValidSeeds().boxed().collect(Collectors.toList());
        device.findAllValidSeeds().forEach(s -> {
            System.out.println(s);
        });
        long end = System.nanoTime();

//        System.out.printf("results: %s%n", results);
//        System.out.printf("count:   %s%n", results.size());
        //System.out.printf("result: %d%n", result);
        System.out.printf("elapsed: %.2fs%n", (end - start) * 1e-9);
    }
}
