package randomreverser;

import java.util.List;
import java.util.stream.Collectors;

public class RandomReverserTest {
    public static void main(String[] args) {
        RandomReverser device = new RandomReverser();

        for (int i = 0; i < 14; ++i) {
            device.addNextDoubleCall(0.1, 1.0);
        }

        device.setVerbose(true);

        long start = System.nanoTime();
        // List<Long> results = device.findAllValidSeeds().boxed().collect(Collectors.toList());
        long result = device.findAllValidSeeds().findAny().getAsLong();
        long end = System.nanoTime();

//        System.out.printf("results: %s%n", results);
//        System.out.printf("count:   %s%n", results.size());
        System.out.printf("result: %d%n", result);
        System.out.printf("elapsed: %.2fs%n", (end - start) * 1e-9);
    }
}
