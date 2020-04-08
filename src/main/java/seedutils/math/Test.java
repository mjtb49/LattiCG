package main.java.seedutils.math;

import main.java.seedutils.Rand;

import java.util.Random;

import static main.java.seedutils.math.MathHelper.gcd;

public class Test {

	public static void main(String[] args) {
		System.out.println(gcd(4, 6));

		Random random = new Random(1234L);
		Rand rand = new Rand(1234L);

		nextInt(random, rand);
		nextIntNonPowerOf2(random, rand);
		nextLong(random, rand);
		nextFloat(random, rand);
	}

	private static void nextInt(Random random, Rand rand) {
		long tries = 1_000_000_000;
		long start;
		long total;

		start = System.nanoTime();

		for(int i = 0; i < tries; i++) {
			random.nextInt();
		}

		total = System.nanoTime() - start;
		System.out.format("Java Random took %d micros for %d tries of nextInt.\n", total / 1000, tries);

		start = System.nanoTime();

		for(int i = 0; i < tries; i++) {
			rand.nextInt();
		}

		long ntotal = System.nanoTime() - start;
		System.out.format("Fast Rand took %d micros for %d tries of nextInt.\n", ntotal / 1000, tries);
		System.out.format("Fast Rand was %f times faster.\n\n", total / (double)ntotal);
	}

	private static void nextIntNonPowerOf2(Random random, Rand rand) {
		long tries = 1_000_000_000;
		long start;
		long total;

		start = System.nanoTime();

		for(int i = 0; i < tries; i++) {
			random.nextInt(5);
		}

		total = System.nanoTime() - start;
		System.out.format("Java Random took %d micros for %d tries of nextIntNonPowerOf2.\n", total / 1000, tries);

		start = System.nanoTime();

		for(int i = 0; i < tries; i++) {
			rand.nextInt(5);
		}

		long ntotal = System.nanoTime() - start;
		System.out.format("Fast Rand took %d micros for %d tries of nextIntNonPowerOf2.\n", ntotal / 1000, tries);
		System.out.format("Fast Rand was %f times faster.\n\n", total / (double)ntotal);
	}

	private static void nextLong(Random random, Rand rand) {
		long tries = 1_000_000_000;
		long start;
		long total;

		start = System.nanoTime();

		for(int i = 0; i < tries; i++) {
			random.nextLong();
		}

		total = System.nanoTime() - start;
		System.out.format("Java Random took %d micros for %d tries of nextLong.\n", total / 1000, tries);

		start = System.nanoTime();

		for(int i = 0; i < tries; i++) {
			rand.nextLong();
		}

		long ntotal = System.nanoTime() - start;
		System.out.format("Fast Rand took %d micros for %d tries of nextLong.\n", total / 1000, tries);
		System.out.format("Fast Rand was %f times faster.\n\n", total / (double)ntotal);
	}

	private static void nextFloat(Random random, Rand rand) {
		long tries = 1_000_000_000;
		long start;
		long total;

		start = System.nanoTime();

		for(int i = 0; i < tries; i++) {
			random.nextFloat();
		}

		total = System.nanoTime() - start;
		System.out.format("Java Random took %d micros for %d tries of nextFloat.\n", total / 1000, tries);

		start = System.nanoTime();

		for(int i = 0; i < tries; i++) {
			rand.nextFloat();
		}

		long ntotal = System.nanoTime() - start;
		System.out.format("Fast Rand took %d micros for %d tries of nextFloat.\n", total / 1000, tries);
		System.out.format("Fast Rand was %f times faster.\n\n", total / (double)ntotal);
	}

}
