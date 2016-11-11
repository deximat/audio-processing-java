package com.codlex.audio.reductors;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.codlex.audio.transform.Frequency;
import com.codlex.audio.transform.FrequencyRange;
import com.google.common.util.concurrent.AtomicDouble;

public class BandReductor {
	
	public static final List<FrequencyRange> toBand(final List<Frequency> frequencies, final int numberOfBands) {
		final int frequenciesInBand = frequencies.size() / numberOfBands;
		final AtomicInteger currentFrequencies = new AtomicInteger();
		final AtomicDouble minFrequency = new AtomicDouble();
		final AtomicDouble maxFrequency = new AtomicDouble();
		final AtomicDouble amplitude = new AtomicDouble();
		
		List<FrequencyRange> bands = new ArrayList<>();
		Runnable bandCreator = () -> {
			if (currentFrequencies.get() == frequenciesInBand) {
				bands.add(new FrequencyRange(minFrequency.get(), maxFrequency.get(), amplitude.get() / currentFrequencies.get()));
				minFrequency.set(maxFrequency.get());
				currentFrequencies.set(0);
			}
		};
		
		for (int i = 0; i < frequencies.size(); i++) {
			Frequency frequency = frequencies.get(i);
			maxFrequency.set(frequency.getFrequency());
			amplitude.addAndGet(frequency.getAmplitude());
			currentFrequencies.incrementAndGet();
			bandCreator.run();
		}
		
		bandCreator.run();
		
		
		
		return bands;
	} 
}
