package com.codlex.audio.transform;

import java.util.List;

import com.codlex.audio.generator.Wave;

public class FourierTransformTest {

	public static void main(String[] args) {
		List<Double> list = Wave.sine(1, 30, 1024);
		
		// FourierTransform transform = new FastFourierTransform(1, list);
		FourierTransform transform = new DiscreteFourierTransform(1, list);

		for (Frequency frequency : transform.getFrequencies()) {
			if (!frequency.isSilence()) {
				// System.out.println(frequency);
			}
		}
	}
	
	
}
