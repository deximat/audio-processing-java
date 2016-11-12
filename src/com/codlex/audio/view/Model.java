package com.codlex.audio.view;

import java.util.List;

import com.codlex.audio.file.WavFile;
import com.codlex.audio.transform.FastFourierTransform;
import com.codlex.audio.transform.Frequency;
import com.codlex.audio.windowing.WindowFunction;

public class Model {
	
	private List<Double> signal;
	private double sampleDuration;
	private WindowFunction windowFunction = WindowFunction.None;
	private int windowSize = 1024;
	private int numberOfWindows;
	private int activeWindow = 1;
	private double amplitudeFilter;

	public Model() {
	}
	
	public void init(List<Double> signal) {
		this.signal = signal;
		this.sampleDuration = 1.0 / 1024;
	}
	
	public void init(WavFile file) {
		this.signal = file.getSamples(0);
		this.sampleDuration = 1 / file.getSamplingRate();
	}
	
	public List<Frequency> calculateFrequencyDomain() {
		// TODO: fix this frequency
		return new FastFourierTransform(this.sampleDuration, this.windowFunction.apply(getActiveWindowSignal())).getFrequencies();
	}

	public List<Double> getSignal() {
		return this.signal;
	}

	public void setWindowFunction(WindowFunction windowFunction) {
		this.windowFunction = windowFunction;
	}

	public void setWindowSize(final int windowSize) {
		this.windowSize = windowSize;
		this.numberOfWindows = this.signal.size() / this.windowSize;
		System.out.println(this.numberOfWindows);
		if (this.signal.size() % this.windowSize != 0) {
			this.numberOfWindows++;
		}
	}

	public int getNumberOfWindows() {
		return this.numberOfWindows;
	}

	public void setActiveWindow(int activeWindow) {
		this.activeWindow = activeWindow;
	}
	
	private List<Double> getActiveWindowSignal() {
		int window = this.activeWindow;
		// turn to zero based
		window--;
		int fromIndex = this.windowSize * window;
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
}
