package com.seedfinding.latticg.reversal;

import com.seedfinding.latticg.reversal.calltype.java.JavaCalls;
import com.seedfinding.latticg.util.LCG;

import java.util.Random;

public class DungeonTest {
    public static void main(String[] args) {
        int posX = -275;
        int posY = 24;
        int posZ = 396;
        int floorSizeX = 7;
        int floorSizeZ = 9;
        String sequence = "111111111001011011111011110111100011111111110010111111111111010";
        int offsetX = posX & 15;
        int offsetZ = posZ & 15;

        Integer[] pattern = sequence.chars().mapToObj(c -> c == '0' ? 0 : c == '1' ? 1 : 2).toArray(Integer[]::new);

        DynamicProgram device = DynamicProgram.create(LCG.JAVA);

        device.add(JavaCalls.nextInt(16).equalTo(offsetX));
        device.add(JavaCalls.nextInt(256).equalTo(posY));
        device.add(JavaCalls.nextInt(16).equalTo(offsetZ));
//        device.skip(2);
        device.add(JavaCalls.nextInt(2).equalTo((floorSizeX - 7) / 2));
        device.add(JavaCalls.nextInt(2).equalTo((floorSizeZ - 7) / 2));

        for (Integer integer : pattern) {
            if (integer == 0) {
                device.add(JavaCalls.nextInt(4).equalTo(0));
            } else if (integer == 1) {
                device.filteredSkip(r -> {
                    int l = r.nextInt(4);
                    System.out.println(l);
                    return l != 0;
                }, 1);
            } else {
                device.skip(1);
            }
        }
        System.out.println();
        device.reverse().parallel().boxed().forEach(System.out::println);
        System.out.println("---------");
        Random random = new Random(14749183853953L ^ LCG.JAVA.multiplier);
        System.out.println(random.nextInt(16));
        System.out.println(random.nextInt(256));
        System.out.println(random.nextInt(16));
        System.out.println(random.nextInt(2));
        System.out.println(random.nextInt(2));
        for (Integer integer : pattern) {
            int l = random.nextInt(4);
            if (l != 0) {
                System.out.print(l);
            }

        }
        System.out.println();
    }
}
