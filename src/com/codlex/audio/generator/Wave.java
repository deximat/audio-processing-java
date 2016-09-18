package com.codlex.audio.generator;

import java.util.ArrayList;
import java.util.List;

public class Wave {
	
	
	
	public static List<Double> sine(double amplitude, double freq, int length) {
		List<Double> result = new ArrayList<>();
		
		double period = 2 * Math.PI;
		for (int i = 0; i < length; i++) {
			result.add(amplitude * Math.sin(period * i / (length / freq)));
		}
		
		return result;
	}
	
	public static List<Double> add(List<Double> wave1, List<Double> wave2) {
		if (wave1.size() != wave2.size()) {
			throw new RuntimeException("Cannot add two waves of different sizes.");
		}
		
		List<Double> sum = new ArrayList<>();
		
		for (int i = 0; i < wave1.size(); i++) {
			sum.add(wave1.get(i) + wave2.get(i));
		}
		
		return sum;
	}
	
	
}
