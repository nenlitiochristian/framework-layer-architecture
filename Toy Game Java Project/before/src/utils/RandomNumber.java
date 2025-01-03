package utils;

import java.util.Random;

public class RandomNumber {
	private static final Random rand = new Random();

	public static int generate(int low, int high) {
		if (low > high) {
			throw new IllegalArgumentException("Invalid range: low can't be higher than high");
		}
		int range = high - low + 1;
		return rand.nextInt(range) + low;
	}
}