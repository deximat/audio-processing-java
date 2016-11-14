package com.codlex.audio.enpointing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.codlex.audio.generator.Window;

import lombok.Getter;


public class WordDetection {
	
	private double silenceEnergy;
	
	@Getter
	private final List<Word> words;

	private double silenceZct;

	public WordDetection(List<Double> signal, double sampleDuration, long windowDurationMs, long silenceDurationMs) {
		double silenceDurationSeconds = silenceDurationMs / 1000.0;
		int samplesToGet = (int) (silenceDurationSeconds / sampleDuration);
		
		double windowDurationSeconds = windowDurationMs / 1000.0;
		int samplesInWindow = (int)(windowDurationSeconds / sampleDuration);
		
		this.silenceEnergy = calculateSilenceEnergy(signal, samplesToGet);
		this.silenceZct = calculateZCT(signal, samplesToGet, samplesInWindow);
		this.words = calculateWords(signal, sampleDuration, samplesInWindow, 20);
	}

	private double calculateZCT(final List<Double> signal, int samplesToGet, final int windowSize) {
		List<Window> silenceWindows = Window.generate(signal.subList(0, samplesToGet), windowSize);
		
		final double average = silenceWindows.stream().mapToInt(Window::calculateZCR).average().getAsDouble();
		double variance = silenceWindows.stream().mapToDouble((element) -> {
			double diff = element.calculateZCR() - average;
			return diff * diff;
		}).average().getAsDouble();
		double deviation = Math.sqrt(variance);
		double zct =  average + 2 * deviation;
		// zct for silence capped to 25
		// zct = Math.min(zct, 25);
		System.out.println("Average ZCT: " + average + " variance: " + variance + " deviation: " + deviation + " total: " + zct);
		return zct;
	}

	private List<Word> calculateWords(List<Double> signal, double sampleDuration, int size, int epsilonWindows) {
		List<Window> windows = Window.generate(signal, size);
		
		List<Boolean> enoughEnergy = new ArrayList<>();
		for (int i = 0; i < windows.size(); i++) {
			
			List<Window> windowsToConsider = windows.subList(Math.max(i -  epsilonWindows / 2, 0), Math.min(i + epsilonWindows / 2 + 1, windows.size()));
			
			int validCount = (int) windowsToConsider.stream().filter((window) -> window.getAverageEnergy() > this.silenceEnergy).count();
			if (windows.get(i).getAverageEnergy() > this.silenceEnergy && validCount > epsilonWindows / 2) {
			// double average = windowsToConsider.stream().mapToDouble(Window::getAverageEnergy).average().getAsDouble();
			//if (average > this.silenceEnergy) {
				enoughEnergy.add(true);
			} else {
				enoughEnergy.add(false);
			}
		}
	
		double silenceDurationSmooth = 0.1;
		double nonSilenceSmoothDuration = 0.3;
		
		int maxSilenceLengthSmooth = (int) (silenceDurationSmooth / sampleDuration);
		int minNonSilenceLengthSmooth =  (int) (nonSilenceSmoothDuration / sampleDuration);
		smooth(enoughEnergy, maxSilenceLengthSmooth / size, minNonSilenceLengthSmooth / size);
		processZCRExtension(windows, enoughEnergy, (int) ((250 / 1000.0) / sampleDuration));
		
		return makeWords(enoughEnergy, size, sampleDuration);
	}

	private void processZCRExtension(final List<Window> windows, final List<Boolean> enoughEnergy, final int maxSamplesToExtend) {
		
		boolean lastEnergy = !enoughEnergy.isEmpty() ? enoughEnergy.get(0) : false;		
		for (int i = 1; i < enoughEnergy.size(); i++) {
			if (!lastEnergy && enoughEnergy.get(i)) {
				// beginning of word
				for (int j = 1; j <= maxSamplesToExtend && i - j > 0; j++) {
					int index = i - j; 
					Window window = windows.get(index);
					if (window.calculateZCR() > this.silenceZct) {
						enoughEnergy.set(index, true);
					} else {
						break;
					}
				}
			}
			lastEnergy = enoughEnergy.get(i);
		}
		
		lastEnergy = !enoughEnergy.isEmpty() ? enoughEnergy.get(enoughEnergy.size() - 1) : false;	
		for (int i = enoughEnergy.size() - 2; i >= 0; i--) {
			if (!lastEnergy && enoughEnergy.get(i)) {
				// end of word
				for (int j = 1; j <= maxSamplesToExtend && i + j < enoughEnergy.size(); j++) {
					int index = i + j; 
					Window window = windows.get(index);
					if (window.calculateZCR() > this.silenceZct) {
						enoughEnergy.set(index, true);
					} else {
						break;
					}
				}
			}
			lastEnergy = enoughEnergy.get(i);
		}
		
	}

	private List<Word> makeWords(List<Boolean> enoughEnergy, int windowSize, double sampleDuration) {
		int i = 0; 
		
		while (i < enoughEnergy.size() && !enoughEnergy.get(i)) {
			// skipping silence
			i++;
		}
		
		List<Word> words = new ArrayList<>();
		
		while (i < enoughEnergy.size()) {
			int wordStart = i;
			while (i < enoughEnergy.size() && enoughEnergy.get(i)) {	
				i++;
				// skip word samples
			}
			
			words.add(new Word(sampleDuration, wordStart * windowSize, i * windowSize));
			
			while (i < enoughEnergy.size() && !enoughEnergy.get(i)) {	
				i++;
				// silence
			}
		}
		
		return words;
	}

	private void smooth(List<Boolean> enoughEnergy, int maxSilenceLengthSmooth,
			int minNonSilenceLengthSmooth) {
		// skip initial silence 
		int i = 0; 
		int lastSilenceStart = 0;
		int lastSilenceLength = 0;
		
		while (i < enoughEnergy.size() && !enoughEnergy.get(i)) {
			i++;
			lastSilenceLength++;
		}
		System.out.println("Initial silence skipped." + i);
		

		int lastNonSilenceLength = 0;
		while (i < enoughEnergy.size()) {
			int nonSilenceLength = 0;
			while (i < enoughEnergy.size() && enoughEnergy.get(i)) {
				i++;
				nonSilenceLength++;
			}
			
			System.out.println("Last silence length: " + lastSilenceLength);

			if (lastSilenceLength < maxSilenceLengthSmooth
					&& lastNonSilenceLength + nonSilenceLength + lastSilenceLength > minNonSilenceLengthSmooth) {
				System.out.println("Merging some silence to neighbouring sound.");
				int j = lastSilenceStart;
				while (j < enoughEnergy.size() && !enoughEnergy.get(j)) {
					enoughEnergy.set(j, true);
					j++;
				}
			}
			
			lastNonSilenceLength = nonSilenceLength;
			lastSilenceLength = 0;
			lastSilenceStart = i;
			while (i < enoughEnergy.size() && !enoughEnergy.get(i)) {
				i++;
				lastSilenceLength++;
			}
		}
		
	}

	private double calculateSilenceEnergy(List<Double> signal,
			int samplesToGet) {
		

		System.out.println("samples" + samplesToGet);
		List<Double> mesuringWindow = signal.subList(0, Math.min(samplesToGet, signal.size()));
		final double average = mesuringWindow.stream().mapToDouble(Math::abs).average().getAsDouble();
		double variance = mesuringWindow.stream().mapToDouble((element) -> {
			double diff = Math.abs(element) - average;
			return diff * diff;
		}).average().getAsDouble();
		double deviation = Math.sqrt(variance);
		double energy =  average + 2 * deviation;
		System.out.println("Average energy: " + average + " variance: " + variance + " deviation: " + deviation + " energy: " + energy);
		return energy;
	}
}
