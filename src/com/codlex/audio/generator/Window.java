package com.codlex.audio.generator;

import java.util.ArrayList;
import java.util.List;

import com.codlex.audio.transform.FastFourierTransform;
import com.codlex.audio.transform.Frequency;


public class Window {

	private final List<Double> samples;
	private final double length;
	
	private Window(List<Double> samples, double length) {
		this.samples = samples;
		this.length = length;
	}
	
	public List<Frequency> doFFT() {
		final FastFourierTransform transform = new FastFourierTransform(this.length, this.samples);
		return transform.getFrequencies();
	}
	
	public static List<Window> generate(final List<Double> samples, int size, int overlap, double length) {
		final List<Window> windows = new ArrayList<>();
		
		for (int start = 0; start + size <= samples.size(); start += overlap) { 
			windows.add(new Window(samples.subList(start, start + size), length));
		}
		
		return windows;
	}
}
