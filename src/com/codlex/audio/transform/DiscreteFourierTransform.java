package com.codlex.audio.transform;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class DiscreteFourierTransform implements FourierTransform {

	
	private final int n;
	private List<Double> samples;
	
	@Getter
	private List<Frequency> frequencies;
	private double windowLengthSeconds;
	
	
	public DiscreteFourierTransform(final double windowLengthSeconds, final List<Double> windowToTransform) {
		this.windowLengthSeconds = windowLengthSeconds;
		this.n = windowToTransform.size();
		this.samples = windowToTransform;
		this.frequencies = new ArrayList<>();
		
		for (int i = 0; i < this.n; i++) {
			this.frequencies.add(calculateFrequency(i));
		}
		
		this.frequencies = this.frequencies.subList(0, this.n / 2 + 1);
	}


	private Frequency calculateFrequency(final int frequencyIndex) {
		double frequency = frequencyIndex / (double) this.n;
		
		ComplexNumber result = new ComplexNumber(0);
		
		for (int k = 0; k < this.n; k++) {
			double tetha = - k * 2 * Math.PI * frequency;
			ComplexNumber toAdd = new ComplexNumber(this.samples.get(k) * Math.cos(tetha), this.samples.get(k) * Math.sin(tetha));
			result = result.plus(toAdd);
		}
		
		double amplitude = result.abs();
		amplitude /= this.n / 2;
		
		return new Frequency(frequencyIndex / this.windowLengthSeconds, amplitude);
	}

}
