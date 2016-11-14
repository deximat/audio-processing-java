package com.codlex.audio.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.codlex.audio.enpointing.Word;
import com.codlex.audio.enpointing.WordDetection;
import com.codlex.audio.file.WavFile;
import com.codlex.audio.transform.FastFourierTransform;
import com.codlex.audio.transform.Frequency;
import com.codlex.audio.windowing.WindowFunction;

import lombok.Getter;

public class Model {
	
	private List<Double> signal;
	@Getter
	private double sampleDuration;
	private WindowFunction windowFunction = WindowFunction.None;
	@Getter
	private int windowSize = 1024;
	private int activeWindow = 1;
	private double amplitudeFilter;
	@Getter
	private List<Word> words = new ArrayList<>();
	@Getter
	private Word activeWord;

	public Model() {
	}
	
	public void init(WavFile file) {
		this.signal = file.getSamples(0);
		this.sampleDuration = 1 / file.getSamplingRate();
		this.activeWindow = 1;
		setWindowSize(10);
		setAmplitudeFilter(0);
		Word wholeSingnal = new Word(this.sampleDuration, 0, this.signal.size()) {
			@Override
			public String toString() {
				return "Whole signal";
			}
			
			public boolean isWholeSignal() {
				return true;
			}
		};
		this.activeWord = wholeSingnal;
		this.words.clear();
		this.words.add(wholeSingnal);
		this.words.addAll(new WordDetection(getSignal(), getSampleDuration(), 10, 1000).getWords());
		for (Word word : words) {
			System.out.println("word: " + word);
		}
	}
	
	public List<Frequency> calculateFrequencyDomain() {
		int activeWindow = this.activeWindow - 1;
		if (activeWindow == -1) {
			// should work since frequencies are gathered by same division
			Map<Double, Double> freqAmplitude = new HashMap<>();
			
			for (int i = 0; i < calculateNumberOfWindows(); i++) {
				List<Frequency> frequencies = new FastFourierTransform(this.sampleDuration, this.windowFunction.apply(getWindowSignal(i))).getFrequencies();
				for (Frequency frequency : frequencies) {
					Double oldAmp = freqAmplitude.get(frequency.getFrequency());
					if (oldAmp == null) {
						oldAmp = 0.0;
					}
					freqAmplitude.put(frequency.getFrequency(), oldAmp + frequency.getAmplitude()); 
				}
			}
			
			List<Frequency> frequencies = new ArrayList<>();
			for (Entry<Double, Double> entry : freqAmplitude.entrySet()) {
				frequencies.add(new Frequency(entry.getKey(), entry.getValue() / calculateNumberOfWindows()));
			}
			
			return frequencies;
		}
		
		return new FastFourierTransform(this.sampleDuration, this.windowFunction.apply(getActiveWindowSignal())).getFrequencies();

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
		int fromIndex = this.windowSize * windowZeroBased;
		int toIndex = Math.min(fromIndex + this.windowSize, this.signal.size());
		List<Double> windowSignal = this.signal.subList(fromIndex, toIndex);
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
}
