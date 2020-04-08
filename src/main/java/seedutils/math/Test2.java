package main.java.seedutils.math;

import main.java.seedutils.Rand;

import java.lang.reflect.Field;
import java.util.Random;

public class Test2 {

	static LCG INVERSE_LCG = Rand.JAVA_LCG.combine(-1);

	public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
		Field field = Random.class.getDeclaredField("seed");
		field.setAccessible(true);

		long tries = 100000000;

		Random random = new Random(1234L);

		long start = System.nanoTime();

		for(long i = 0; i < tries; i++) {
			field.get(random);
		}

		long total = System.nanoTime() - start;
		System.out.format("Reflection took %d micros for %d tries.\n", total / 1000, tries);

		random = new Random(1234L);

		start = System.nanoTime();

		for(long i = 0; i < tries; i++) {
			long nextLong = random.nextLong();
			int nextInt1 = (int)(nextLong >>> 32);
			int nextInt2 = (int)(nextLong & ((1L << 32) - 1));

			//Those are some magic lattice numbers.
			long a = (24667315 * (long) nextInt1 + 18218081 * (long) nextInt2 + 67552711) >> 32;
			long b = (-4824621 * (long) nextInt1 + 7847617 * (long) nextInt2 + 7847617) >> 32;
			long seed = INVERSE_LCG.nextSeed(7847617 * a - 18218081 * b);
		}

		total = System.nanoTime() - start;
		System.out.format("Math took %d micros for %d tries.\n", total / 1000, tries);
	}

}
