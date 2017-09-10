package com.codlex.audio.projekat;

import java.util.ArrayList;
import java.util.List;

import com.codlex.audio.windowing.WindowFunction;

public class AudioConstants {
	
	public static class WordDetection {
		public static double silenceDurationSmooth = 0.12;
		public static double nonSilenceSmoothDuration = 0.1;
	}
	
	public static long windowDurationMs = 10;
	
	// used for endpointing
	public static long silenceDurationMs = 500;
	
	public static double dtwTreshold = 320;

	public static int lpcCoeficients = 12;
	public static final WindowFunction lpcWindowFunction = WindowFunction.Hanning;

	
	public static class Test1 {
		static List<String> words;
		static {
			words = new ArrayList<>();
			
			//words.add("cokolada");
//			words.add("dva");
//			words.add("tri");
//			words.add("cetiri");
//			words.add("pet");
//			words.add("sest");
//			words.add("sedam");
//			words.add("osam");
//			words.add("devet");
//			words.add("nula");

			
//			words.add("klima");
//			words.add("divljac");
//			words.add("ronaldo");
//			words.add("teologija");
//			words.add("veronauka");
//			words.add("idioti");
//			
//			
//			words.add("pas");
//			words.add("droga");
//			words.add("teletabisi");
//			words.add("kokoska");
//			words.add("jaje");
//			words.add("amazon");
//			words.add("apple");
//			words.add("pistolj");
//			words.add("gumica");
//			words.add("asteroid");
//			
//			words.add("kravica");
//			words.add("java");
			words.add("govor");
			words.add("ples");
			words.add("pokemon");
			words.add("wizard");
			words.add("asistent");
			words.add("projekat");
			words.add("cokolada");
//			words.add("fakultet");
		}
	}
	
}
