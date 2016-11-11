package com.codlex.audio.view;

import java.util.List;

import com.codlex.audio.file.WavFile;
import com.codlex.audio.transform.FastFourierTransform;
import com.codlex.audio.transform.Frequency;
import com.codlex.audio.windowing.WindowFunction;

public class Model {
	
	private List<Double> signal;
	private double sampleFrequency;
	private WindowFunction windowFunction = WindowFunction.None;
	private int windowSize;

	public Model() {
	}
	
	public void init(List<Double> signal) {
		this.signal = signal;
		this.sampleFrequency = 1;
	}
	
	public void init(WavFile file) {
		this.signal = file.getSamples(0).subList(0, 1024);
		this.sampleFrequency = file.getSamplingRate();
	}
	
	public List<Frequency> calculateFrequencyDomain() {
		// TODO: fix this frequency
		return new FastFourierTransform(this.sampleFrequency, this.signal).getFrequencies();
	}

	public List<Double> getSignal() {
		return this.signal;
	}

	public void setWindowFunction(WindowFunction windowFunction) {
		System.out.println("changed window function to: " + windowFunction);
		this.windowFunction = windowFunction;
	}

	public void setWindowSize(final int windowSize) {
		this.windowSize = windowSize;
	}
}
