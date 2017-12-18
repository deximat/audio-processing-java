package com.codlex.audio.pg.domaci3;


import com.codlex.audio.pg.domaci2.WindowSizeUnit;
import com.codlex.audio.windowing.WindowFunction;

public class AudioConstants {

	public static class WordDetection {
		public static double silenceDurationSmooth = 0.12;
		public static double nonSilenceSmoothDuration = 0.1;
	}

	public static int mfccCoefficients = 13;
	
	public static long windowDurationMs = 10;

	// used for endpointing
	public static long silenceDurationMs = 500;

	public static double dtwTreshold = Double.MAX_VALUE;

	public static int lpcCoeficients = 12;
	public static WindowFunction lpcWindowFunction = WindowFunction.Hamming;
	
	public static WindowFunction windowFunction = WindowFunction.Hamming;

	
	public static int lpcWindowSize = 10;

	public static int lpcWindowShift = 5;

	public static WindowSizeUnit lpcWindowUnit = WindowSizeUnit.Milliseconds;

	public static int getLpcWindowShift(double sampleDuration) {
		switch (lpcWindowUnit) {
		case Milliseconds:
			return (int) ((lpcWindowShift / 1000.0) / sampleDuration);
		case Samples:
			return lpcWindowShift;
		}
		return 0;
	}

	public static int getLpcWindowSize(double sampleDuration) {
		switch (lpcWindowUnit) {
		case Milliseconds:
			return (int) ((lpcWindowSize / 1000.0) / sampleDuration);
		case Samples:
			return lpcWindowSize;
		}
		return 0;
	}

}
