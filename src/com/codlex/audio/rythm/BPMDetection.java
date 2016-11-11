package com.codlex.audio.rythm;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.codlex.audio.file.WavFile;
import com.codlex.audio.generator.Window;
import com.codlex.audio.reductors.BandReductor;
import com.codlex.audio.transform.FrequencyRange;

public class BPMDetection {
	
	
	public static void main(String[] args) throws IOException {
		final WavFile file = WavFile.load("song2.wav");
		double samplingRate = file.getSamplingRate();
		
		final int windowSize = 1 << 13;
		final int overlap = windowSize >> 1; // 50%
		List<Window> windows = Window.generate(file.getSamples(0), windowSize, overlap, windowSize / samplingRate);
		List<List<FrequencyRange>> ranges = new ArrayList<>();
		int windowCount = 0;
		for (Window window : windows) {
			ranges.add(BandReductor.toBand(window.doFFT(), 21));
			System.out.println("Processing window " + (windowCount++) + " of " + windows.size());
		}
		
		FileWriter writer = new FileWriter(new File("output.csv"));
		
		for (List<FrequencyRange> oneWindowRange : ranges) {
			StringBuilder builder = new StringBuilder();
			for (FrequencyRange oneRange : oneWindowRange) {
				builder.append(oneRange.getAmplitude());
				builder.append(",");
			}
			builder.deleteCharAt(builder.length() - 1);
			builder.append("\n");
			writer.write(builder.toString());
		}
	}

}
