package com.codlex.audio.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.codlex.audio.enpointing.Word;
import com.codlex.audio.enpointing.WordDetection;
import com.codlex.audio.file.WavFile;
import com.codlex.audio.projekat.AudioConstants;
import com.codlex.audio.transform.FastFourierTransform;
import com.codlex.audio.transform.Frequency;
import com.codlex.audio.windowing.WindowFunction;
import com.google.common.primitives.Doubles;

import lombok.Getter;

public class Model {

	private List<Double> signal;
	@Getter
	private double sampleDuration;
	private WindowFunction windowFunction = WindowFunction.None;
	@Getter
	private int windowSize = 1024;
	@Getter
	private int windowSizeMs;
	
	@Getter
	private int activeWindow = 1;
	
	private double amplitudeFilter;
	@Getter
	private List<Word> words = new ArrayList<>();
	@Getter
	private Word activeWord;
	private int frequencyCount = -1;

	public Model() {
	}
	

	public void init(WavFile file) {
		this.frequencyCount = -1;
		this.signal = file.getSamples(0);
		this.sampleDuration = 1 / file.getSamplingRate();
		this.activeWindow = 0;
		setWindowSize(10);
		setAmplitudeFilter(0);
		// TODO: be careful I put null for windows
		Word wholeSingnal = new Word(this.sampleDuration, 0, this.signal.size(), new ArrayList<>(), null) {
			@Override
			public String toString() {
				return "Whole signal";
			}

			@Override
			public boolean isWholeSignal() {
				return true;
			}
		};
		this.activeWord = wholeSingnal;
		this.words.clear();
		this.words.add(wholeSingnal);
//		this.words.addAll(new WordDetection(getSignal(), this.sampleDuration, AudioConstants.windowDurationMs,
//				AudioConstants.silenceDurationMs, null).getWords());
		for (Word word : words) {
			System.out.println("word: " + word);
		}
	}

	public List<List<Frequency>> calculateFullFrequencyDomain() {
		List<List<Frequency>> whole = new ArrayList<>();
		for (int i = 0; i < calculateNumberOfWindows(); i++) {
			whole.add(calculateFrequencyDomainForWindow(i));
		}
		return whole;
	}
	
	public List<Frequency> calculateFrequencyDomainForWindow(int windowIndex) {
		return new FastFourierTransform(this.sampleDuration, this.windowFunction.apply(getWindowSignal(windowIndex)))
		.getFrequencies();
	}
	
	
	public List<Frequency> calculateFrequencyDomain() {
		int activeWindow = this.activeWindow - 1;
		if (activeWindow == -1) {
			// should work since frequencies are gathered by same division
			Map<Double, Double> freqAmplitude = new HashMap<>();

			for (int i = 0; i < calculateNumberOfWindows(); i++) {
				List<Frequency> frequencies = new FastFourierTransform(this.sampleDuration,
						this.windowFunction.apply(getWindowSignal(i))).getFrequencies();
				for (Frequency frequency : frequencies) {
					Double oldAmp = freqAmplitude.get(frequency.getFrequency());
					if (oldAmp == null) {
						oldAmp = 0.0;
					}
					freqAmplitude.put(frequency.getFrequency(), oldAmp + frequency.getAmplitude());
				}
			}

			List<Frequency> frequencies = new ArrayList<>();
			for (Entry<Double, Double> entry : freqAmplitude.entrySet().stream().sorted((c1, c2) -> Doubles.compare(c1.getKey(), c2.getKey())).collect(Collectors.toList())) {
				frequencies.add(new Frequency(entry.getKey(), entry.getValue() / calculateNumberOfWindows()));
			}

			return frequencies;
		}

		return calculateFrequencyDomainForWindow(activeWindow);

	}

	public List<Double> getSignal() {
		if (this.activeWord != null) {
			return this.signal.subList(this.activeWord.getStart(), this.activeWord.getEnd());
		} else {
			return this.signal;
		}
	}

	public void setWindowFunction(WindowFunction windowFunction) {
		this.windowFunction = windowFunction;
	}

	public void setWindowSize(final int windowSizeMS) {
		this.windowSizeMs = windowSizeMS;
		this.windowSize = (int) (windowSizeMS / 1000.0 / this.sampleDuration);

	}

	public int calculateNumberOfWindows() {
		int numberOfWindows = getSignal().size() / this.windowSize;
		if (getSignal().size() % this.windowSize != 0) {
			numberOfWindows++;
		}
		return numberOfWindows;
	}

	public void setActiveWindow(int activeWindow) {
		this.activeWindow = activeWindow;
	}

	private List<Double> getActiveWindowSignal() {
		int window = this.activeWindow;
		// turn to zero based
		window--;
		return getWindowSignal(window);
	}

	private List<Double> getWindowSignal(int windowZeroBased) {
		List<Double> signal = getSignal();
		int fromIndex = this.windowSize * windowZeroBased;
		int toIndex = Math.min(fromIndex + this.windowSize, signal.size());
		List<Double> windowSignal = signal.subList(fromIndex, toIndex);
		return windowSignal;
	}

	public double getAmplitudeFilter() {
		return this.amplitudeFilter;
	}

	public void setAmplitudeFilter(double newValue) {
		this.amplitudeFilter = newValue;
	}


	public int getActiveWindowIndex() {
		return (this.activeWindow - 1) * this.windowSize;
	}

	public void setActiveWord(Word word) {
		this.activeWord = word;
	}

	public int getDuration() {
		return (int) (getSampleDuration() * getSignal().size() * 1000);
	}


	public int getFrequencyCount() {
		if (frequencyCount == -1) {
			this.frequencyCount = (int) calculateFrequencyDomain().stream().count();
		} 
		return this.frequencyCount;
	}
	
}
