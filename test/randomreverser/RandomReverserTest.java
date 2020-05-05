package randomreverser;

import java.util.List;
import java.util.stream.Collectors;

public class RandomReverserTest {
    public static void main(String[] args) {
        RandomReverser device = new RandomReverser();

        device.addNextFloatCall(0f,0f,true,true);
        device.consumeNextFloatCalls(1);
        device.addNextFloatCall(0f,0f,true,true);

        //device.setVerbose(true);

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
