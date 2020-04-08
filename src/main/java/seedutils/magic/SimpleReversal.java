package main.java.seedutils.magic;

import main.java.seedutils.Rand;
import main.java.seedutils.math.LCG;

/**
 * All credits to Matthew... again. :P
 * */
public class SimpleReversal {

	private static final LCG INVERSE_LCG = Rand.JAVA_LCG.combine(-1);

	public static boolean isRandomSeed(long worldSeed) {
		long potentialSeed = fromNextLong(worldSeed);
		return new Rand(potentialSeed, false).nextLong() == worldSeed;
	}

	public static long fromNextInts(int nextInt1, int nextInt2) {
		long a = (24667315 * (long)nextInt1 + 18218081 * (long)nextInt2 + 67552711) >> 32;
		long b = (-4824621 * (long)nextInt1 + 7847617 * (long)nextInt2 + 7847617) >> 32;
		return INVERSE_LCG.nextSeed(7847617 * a - 18218081 * b);
	}

	public static long fromNextLong(long nextLong) {
		return fromNextInts((int)(nextLong >>> 32), (int)(nextLong & MagicMath.MASK_32));
	}

}
