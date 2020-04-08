package main.java.seedutils.magic;

import main.java.seedutils.Rand;
import main.java.seedutils.Seeds;
import main.java.seedutils.math.LCG;

import java.util.ArrayList;
import java.util.List;

/**
 * Incredible algorithm for population seed reversal. Check Seeds#setPopulationSeeds().
 * All credits to Matthew. (The man doesn't have a GitHub, shame!)
 * */
public class PopulationReversal {

	private static final LCG SKIP_2 = Rand.JAVA_LCG.combine(2);
	private static final LCG SKIP_4 = Rand.JAVA_LCG.combine(4);

	public static ArrayList<Long> getWorldSeeds(long populationSeed, int x, int z) {
		populationSeed &= MagicMath.MASK_48;
		ArrayList<Long> worldSeeds = new ArrayList<>();

		if (x == 0 && z == 0) {
			worldSeeds.add(populationSeed);
			return worldSeeds;
		}

		long c; //a is upper 16 bits, b middle 16 bits, c lower 16 bits of worldseed.
		long e = populationSeed & MagicMath.MASK_32; //The algorithm proceeds by solving for worldseed in 16 bit groups
		long f = populationSeed & MagicMath.MASK_16; //as such, we need the 16 bit groups of chunkseed for later eqns.

		boolean xEven = (x & 1) == 0;
		boolean zEven = (z & 1) == 0;

		long firstMultiplier = (SKIP_2.multiplier * x + SKIP_4.multiplier * z) & MagicMath.MASK_16;
		int multTrailingZeroes = MagicMath.countTrailingZeroes(firstMultiplier); //TODO currently code blows up if this is 16, but you can use it to get bits of seed anyway if it is non zero
		long firstMultInv = MagicMath.modInverse(firstMultiplier >> multTrailingZeroes,16);

		//TODO We can recover more initial bits when x + z is divisible by a power of 2
		if (xEven ^ zEven) { //bottom bit of x*a + z*b is odd so we xor by 1 to get bottom bit of worldseed.
			c = (populationSeed & 1) ^ 1;
		} else { //bottom bit of x*a + z*b is even so we xor by 0 to get bottom bit of worldseed.
			c = (populationSeed & 1);
		}

		for (; c < (1L << 16); c += 2) { //iterate through all possible lower 16 bits of worldseed.
			long target = (c ^ f) & MagicMath.MASK_16; //now that we've guessed 16 bits of worldseed we can undo the mask

			//We need to handle the four different cases of the effect the two | 1s have on the seed
			long magic = x * ((SKIP_2.multiplier * ((c ^ Rand.JAVA_LCG.multiplier) & MagicMath.MASK_16) + SKIP_2.addend) >>> 16) + z * ((SKIP_4.multiplier * ((c ^ Rand.JAVA_LCG.multiplier) & MagicMath.MASK_16) + SKIP_4.addend) >>> 16);

			addWorldSeed(worldSeeds, target - (magic & MagicMath.MASK_16), multTrailingZeroes, firstMultInv, c, e, x, z, populationSeed); //case both nextLongs were odd
			addWorldSeed(worldSeeds, target - ((magic + x) & MagicMath.MASK_16), multTrailingZeroes, firstMultInv, c, e, x, z, populationSeed); //case where x nextLong even
			addWorldSeed(worldSeeds, target - ((magic + z) & MagicMath.MASK_16), multTrailingZeroes, firstMultInv, c, e, x, z, populationSeed); //case where z nextLong even
			addWorldSeed(worldSeeds, target - ((magic + x + z) & MagicMath.MASK_16), multTrailingZeroes, firstMultInv, c, e, x, z, populationSeed); //case where both nextLongs even
		}

		return worldSeeds;
	}

	public static long makeSecondAddend(int x, long k, int z) {
		return ((x*((((SKIP_2.multiplier * ((k ^ Rand.JAVA_LCG.multiplier) & MagicMath.MASK_32) + SKIP_2.addend) & MagicMath.MASK_48) >>> 16) | 1L) +
				z*((((SKIP_4.multiplier * ((k ^ Rand.JAVA_LCG.multiplier) & MagicMath.MASK_32) + SKIP_4.addend) & MagicMath.MASK_48) >>> 16) | 1L)) >>> 16) & MagicMath.MASK_16;
	}

	public static void addWorldSeed(List<Long> worldSeeds, long firstAddend, int multTrailingZeroes, long firstMultInv, long c, long e, int x, int z, long populationSeed) {
		if(MagicMath.countTrailingZeroes(firstAddend) >= multTrailingZeroes) { //Does there exist a set of 16 bits which work for bits 17-32
			long b = ((((firstMultInv * firstAddend)>>> multTrailingZeroes) ^ (Rand.JAVA_LCG.multiplier >> 16)) & ((1L << (16 - multTrailingZeroes)) - 1));

			for(; b < (1L << 16); b += (1L << (16 - multTrailingZeroes))) { //if the previous multiplier had a power of 2 divisor, we get multiple solutions for b
				long k = (b << 16) + c;
				long target2 = (k ^ e) >> 16; //now that we know b, we can undo more of the mask
				long secondAddend = makeSecondAddend(x, k, z);

				if (MagicMath.countTrailingZeroes(target2 - secondAddend) >= multTrailingZeroes) { //Does there exist a set of 16 bits which work for bits 33-48
					long a = ((((firstMultInv * (target2 - secondAddend)) >>> multTrailingZeroes) ^ (Rand.JAVA_LCG.multiplier >> 32)) & ((1L << (16-multTrailingZeroes)) - 1));

					for(; a < (1L << 16); a += (1L << (16 - multTrailingZeroes))) { //if the previous multiplier had a power of 2 divisor, we get multiple solutions for a
						if((Seeds.setPopulationSeed(null, (a << 32) + k, x, z) & MagicMath.MASK_48) == populationSeed) { //lazy check if the test has succeeded
							worldSeeds.add((a << 32) + k);
						}
					}
				}
			}
		}
	}

}
