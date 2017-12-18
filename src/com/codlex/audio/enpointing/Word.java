package com.codlex.audio.enpointing;

import java.util.ArrayList;
import java.util.List;

import com.codlex.audio.file.WavFile;
import com.codlex.audio.generator.Window;
import com.codlex.audio.pg.domaci3.AudioConstants;
import com.codlex.audio.pg.domaci3.DTW;
import com.codlex.audio.pg.domaci3.LPC;
import com.codlex.audio.pg.domaci3.MFCC;
import com.codlex.audio.pg.domaci3.Util;

import lombok.Data;

@Data
public class Word {

	private double sampleDuration;
	private int start;
	private int end;
	private List<Window> windows;
	private List<List<Double>> coeficients;
	private List<List<Double>> mfccCoeficients;
	private int vectorToAnalyzeStart;

	private String name;
	private List<Double> signal;

	@Override
	public String toString() {
		return this.name;
		// return String.format("Word(%.2f, %.2f, %s)", this.start * sampleDuration,
		// this.end * sampleDuration, this.name);
	}

	public boolean isWholeSignal() {
		return false;
	}

	public List<Double> distancePerCoeff(Word otherWord, int vectorsToAnalyze) {
		final List<Double> distances = new ArrayList<>();
		for (int i = 0; i < vectorsToAnalyze; i++) {
			double distance = Util.Euclid.distance(getCoefficientToAnalyze(i), otherWord.getCoefficientToAnalyze(i));

			if (i >= this.coeficients.size() || i >= otherWord.coeficients.size()) {
				distance = -distance;
			}

			distances.add(distance);
		}

		return distances;
	}

	private List<Double> getCoefficientToAnalyze(final int index) {
		return this.coeficients.get(this.vectorToAnalyzeStart + index);
	}

	public double distanceTo(final Word word) {
		return new DTW(this.coeficients, word.coeficients).getDistance();
	}

	public Word(double sampleDuration, int start, int end, List<Window> windows, List<Double> signal, String name) {
		this.sampleDuration = sampleDuration;
		this.start = start;
		this.end = end;
		this.windows = windows;
		this.signal = signal;
		// this.coeficients = new LPC(AudioConstants.lpcCoeficients, signal, sampleDuration).getCoeficients();
		this.coeficients = calculateMFCC(48000);
		
		// this.mfccCoeficients;
		// for (int i = 0; i < this.coeficients.size(); i++) {
		// System.out.print(distance(this.coeficients.get(i)) + " ");
		// }
		// System.out.println();
		this.name = name + "(" + (start * sampleDuration) + ", " + (end * sampleDuration) + ")";

		System.out.println("Word - " + this.start + " - " + this.end + " - " + sampleDuration + " w: "
				+ this.windows.size() + "-" + this.signal.size());
	}

	private List<List<Double>> calculateMFCC(int samplingRate) {
		List<List<Double>> coefficients = new ArrayList<>();
		for (Window window : windows) {
			coefficients.add(new MFCC(window.getSamples().size(), samplingRate, AudioConstants.mfccCoefficients).calculateCoefficients(window.getSamples()));
		}
		return coefficients;
	}

	public static Word loadSingle(final String fileName) {
		System.out.println("name" + fileName); 
		WavFile file = WavFile.load(fileName);
		WordDetection wordDetection = new WordDetection(file, AudioConstants.windowDurationMs,
				AudioConstants.silenceDurationMs, fileName);
		List<Word> words = wordDetection.getWords();
		if (words.size() != 1) {
			System.out.println("More than one word in sample: " + words.size() + " in file: " + fileName);
		}
		return words.get(0);
	}

	public static List<Word> loadAll(final String fileName) {
		WavFile file = WavFile.load(fileName);
		WordDetection wordDetection = new WordDetection(file, AudioConstants.windowDurationMs,
				AudioConstants.silenceDurationMs, fileName);
		return wordDetection.getWords();
	}

	public void recalculateLPC() {
		this.coeficients = new LPC(AudioConstants.lpcCoeficients, this.signal, this.sampleDuration).getCoeficients();
	}
}
