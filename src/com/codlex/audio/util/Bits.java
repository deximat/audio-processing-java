package com.codlex.audio.util;

public class Bits {
	public static final boolean isPowerOfTwo(final Integer number) {
		return Integer.bitCount(number) == 1;
	}
}
