package com.codlex.audio.enpointing;

import java.util.List;

import com.codlex.audio.file.WavFile;
import com.codlex.audio.generator.Window;
import com.codlex.audio.projekat.AudioConstants;
import com.codlex.audio.projekat.DTW;
import com.codlex.audio.projekat.LPC;

import lombok.Data;

@Data
public class Word {
	
	private double sampleDuration;
	private int start;
	private int end;
	private List<Window> windows;
	private List<List<Double>> coeficients;
	private String name;
	
	
	@Override
	public String toString() {
		return this.name;
		//return String.format("Word(%.2f, %.2f, %s)", this.start * sampleDuration, this.end * sampleDuration, this.name);
	}
	
	public boolean isWholeSignal() {
		return false;
	}

	public double distanceTo(final Word word) {
		return new DTW(word.coeficients, this.coeficients).getDistance();
	}

	public Word(double sampleDuration, int start, int end, List<Window> windows, String name) {
		this.sampleDuration = sampleDuration;
		this.start = start;
		this.end = end;
		this.windows = windows;
		this.coeficients = new LPC(AudioConstants.lpcCoeficients, windows).getCoeficients();
//		for (int i = 0; i < this.coeficients.size(); i++) {
//			System.out.print(distance(this.coeficients.get(i)) + " ");
//		}
//		System.out.println();
		this.name = name;
	}

	public static Word loadSingle(final String fileName) {
		WavFile file = WavFile.load(fileName);
		WordDetection wordDetection = new WordDetection(file, AudioConstants.windowDurationMs,
				AudioConstants.silenceDurationMs, fileName);
		List<Word> words = wordDetection.getWords();
		if (words.size() != 1) {
			 System.out.println("More than one word in sample: " + words.size() + " in file: " + fileName);
		}
		return words.get(0);
	}
}
