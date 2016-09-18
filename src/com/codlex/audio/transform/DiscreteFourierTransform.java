package com.codlex.audio.transform;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

public class DiscreteFourierTransform {
	
	@Data
	@AllArgsConstructor
	private class Frequency {
		private double frequency;
		private double amplitude;
		
		
		public boolean isSilence() {
			final double zeroEpsilon = 1e-5;
			return Math.abs(this.amplitude) <= zeroEpsilon;
		}
		
		public String toString() {
			return String.format("Freq:%fHZ, Amplitude: %.5f", this.frequency, this.amplitude);
		}
	}
	
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
		
		double real = 0;
		double immaginery = 0;
		
		for (int k = 0; k < this.n; k++) {
			double tetha = - k * 2 * Math.PI * frequency;
			real += this.samples.get(k) * Math.cos(tetha);
			immaginery += this.samples.get(k) * Math.sin(tetha);
		}
		
		double amplitude = real*real + immaginery * immaginery;
		amplitude = Math.sqrt(amplitude);
		amplitude /= this.n / 2;
		
		return new Frequency(frequencyIndex / this.windowLengthSeconds, amplitude);
	}
	
	public static void main(String[] args) {
		List<Double> list = new ArrayList<>();
		list.add(0d);
		list.add(0.707d);
		list.add(1d);
		list.add(0.707d);
		list.add(0d);
		list.add(-0.707d);
		list.add(-1d);
		list.add(-0.707d);
		
		DiscreteFourierTransform transform = new DiscreteFourierTransform(1, list);
	
		for (Frequency frequency : transform.getFrequencies()) {
			if (true || !frequency.isSilence()) {
				System.out.println(frequency);
			}
		}
	}
}
