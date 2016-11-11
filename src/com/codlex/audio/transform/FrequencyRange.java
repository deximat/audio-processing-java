package com.codlex.audio.transform;

import com.google.common.collect.Range;

public class FrequencyRange {
	
	private Range<Double> frequency;
	private double amplitude;

	
	public FrequencyRange(double minFrequency, double maxFrequency, double amplitude) {
		this.frequency = Range.closed(minFrequency, maxFrequency);
		this.amplitude = amplitude;
	}
	
	
	public boolean isSilence() {
		final double zeroEpsilon = 1e-2;
		return Math.abs(this.amplitude) <= zeroEpsilon;
	}

	public String toString() {
		return String.format("Freq:%s, Amplitude: %.5f", this.frequency,
				this.amplitude);
	}


	public double getAmplitude() {
		return this.amplitude;
	}

}
