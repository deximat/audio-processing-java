package com.codlex.audio.generator;

import java.util.ArrayList;
import java.util.List;

import com.codlex.audio.transform.FastFourierTransform;
import com.codlex.audio.transform.Frequency;

import lombok.Getter;


public class Window {

	@Getter
	private final List<Double> samples;
	
	private Window(List<Double> samples) {
		this.samples = samples;
	}
	
	public List<Frequency> doFFT() {
		final FastFourierTransform transform = new FastFourierTransform(1.0/1024, this.samples);
		return transform.getFrequencies();
	}
	
	public static List<Window> generateOverlapping(final List<Double> samples, int size, int shift) {
		System.out.println("Generating LPC windows: " + samples.size() + " - " + size + " sh: " + shift);
		final List<Window> windows = new ArrayList<>();
		
		for (int i = 0; i < samples.size() - size; i += shift) {
			windows.add(new Window(samples.subList(i, i + size)));
		}
		
		return windows;
	}
	
	public static List<Window> generate(final List<Double> samples, int size) {
		final List<Window> windows = new ArrayList<>();
		
		for (int start = 0; start + size <= samples.size(); start += size) { 
			windows.add(new Window(samples.subList(start, start + size)));
		}
		
		return windows;
	}
	
	public double getAverageEnergy() {
		return this.samples.stream().mapToDouble(Math::abs).average().getAsDouble();
	}
	
	public int calculateZCR() {
		int counter = 0;
		boolean lastIsPositive = this.samples.isEmpty() ? this.samples.get(0) > 0 : false;
		for (Double sample : samples) {
			if (lastIsPositive && sample <= 0
					|| !lastIsPositive && sample > 0) {
				counter++;
			}
			lastIsPositive = sample > 0;
		}
		return counter;
		
	}

	public static Window empty(int size) {
		List<Double> samples = new ArrayList<>();
		
		for (int i = 0; i < size; i++) {
			samples.add(0.0);
		}
		
		return new Window(samples);
	}
}
