package randomreverser;

import randomreverser.util.Rand;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RandomReverserTest {
    public static void main(String[] args) {
        RandomReverser device = new RandomReverser();
        Random r = new Random(30109882286122L ^ 0x5deece66dL);

        device.addNextIntCall(420,69,69);
        device.addNextIntCall(420,69,69);
        device.addNextIntCall(420,69,69);
        device.addNextIntCall(420,69,69);
        device.addNextIntCall(420,69,69);

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
