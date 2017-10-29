package com.codlex.audio.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Wave {
	
	
	
	
	
	public static List<Double> sine(double amplitude, double freq, int length) {
		return sine(amplitude, freq, length, 0);
	}

	public static List<Double> sine(double amplitude, double freq, int length, int incrementFreq) {
		List<Double> result = new ArrayList<>();
		
		double period = 2 * Math.PI;
		for (int i = 0; i < length; i++) {
			result.add(amplitude * Math.sin(period * i / (length / (freq + incrementFreq * i ))));
		}
		
		return result;
	}
	
	public static List<Double> add(List<Double> wave1, List<Double> wave2) {
		List<Double> sum = new ArrayList<>();
		for (int i = 0; i < Math.max(wave1.size(), wave2.size()); i++) {
			double wave1Value = i < wave1.size() ? wave1.get(i) : 0;
			double wave2Value = i < wave2.size() ? wave2.get(i) : 0;
			sum.add(wave1Value + wave2Value);
		}
		return sum;
	}
	
	public static List<Double> shr(List<Double> wave, int move) {
		List<Double> sum = new ArrayList<>();
		for (int i = 0; i < move; i++) {
			sum.add(0.0);
		}
		for (int i = 0; i < wave.size(); i++) {
			sum.add(wave.get(i));
		}
		return sum;
	}

	public static List<Double> parse(String waveExpression) {
		String[] waves = waveExpression.split("\\+");
		final Pattern wavePattern = Pattern.compile(" *[W,w]ave\\(([\\-]*[0-9]*) *, *([\\-]*[0-9]*)\\) *");
		List<Double> sumOfWaves = new ArrayList<Double>();
		sumOfWaves.addAll(Collections.nCopies(1024, 0.0));
		
		for (String wave : waves) {
			// wave should be of this format: Wave(amplitude, frequency)
			Matcher matcher = wavePattern.matcher(wave);
			if (!matcher.find()) {
				throw new RuntimeException("Wrong format given.");
			}
			
			double amplitude = Double.parseDouble(matcher.group(1));
			double frequency = Double.parseDouble(matcher.group(2));
			
			sumOfWaves = add(sumOfWaves, Wave.sine(amplitude, frequency, 1024));
			
		}
		return sumOfWaves;
	}

	public static List<Double> wow() {
			
		return null;
	}
	
	
}
