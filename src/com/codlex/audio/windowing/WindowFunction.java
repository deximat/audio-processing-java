package com.codlex.audio.windowing;

import java.util.ArrayList;
import java.util.List;

public enum WindowFunction {
	None, 
	Hamming {
		@Override
		protected Double transform(double signal, int n, int N) {
			double w = 0.54 - 0.46*Math.cos((2 * Math.PI*n) / (N - 1));
			return w * signal;
		}
	}, 
	Hanning {
		@Override
		protected Double transform(double signal, int n, int N) {
			double sin = Math.sin((Math.PI * n) / (N - 1));
			double w = sin * sin;
			return w * signal;
		}
	};
	
	
	protected Double transform(double signal, int n, int N) {
		return signal;
	}
	
	public List<Double> apply(final List<Double> window) {
		List<Double> transformed = new ArrayList<>();
		for (int i = 0; i < window.size(); i++) {
			transformed.add(transform(window.get(i), i, window.size()));
		}
		return transformed;
	}
}
