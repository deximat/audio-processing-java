package com.codlex.audio.pg.domaci3;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.codlex.audio.transform.FastFourierTransform;
import com.codlex.audio.transform.Frequency;

public class MFCC {
	
	private double lowestFrequency;
	private double highestFrequency;

	private int numberOfMelFilters = 30;
	private int numberOfCoefficients;

	private double samplingRate;
	private int samplePerFrame;

	public MFCC(final int samplesPerFrame, final int samplingRate, final int numberOfCoefficients) {
		this.samplePerFrame = samplesPerFrame;
		this.samplingRate = samplingRate;
		this.numberOfCoefficients = numberOfCoefficients;

		this.lowestFrequency = 80.0;
		this.highestFrequency = samplingRate / 2.0;
	}

	public List<Double> calculateCoefficients(final List<Double> framedSignal) {
		// Računanje DFT za svaki prozor
		final List<Frequency> frequencyDomain = doFFT(framedSignal);

		// Primena mel banke filtera; sumiranje energije
		final List<Double> melFiltered = applyMelFilter(frequencyDomain);

		// ● Računa se logaritam za svaki koeficijent
		final List<Double> logaritmedSignal = toLogScale(melFiltered);

		// ● Računa se DCT za svaki koeficijent
		return performDCT(logaritmedSignal);
	}

	public List<Double> performDCT(final List<Double> signal) {
		final List<Double> coefficients = new ArrayList<>(this.numberOfCoefficients);
		for (int n = 1; n <= this.numberOfCoefficients; n++) {
			double coefficient = 0;
			for (int i = 1; i <= this.numberOfMelFilters; i++) {
				coefficient += signal.get(i - 1) * Math.cos(Math.PI * (n - 1) / this.numberOfMelFilters * (i - 0.5));
			}
			coefficients.add(coefficient);
		}
		return coefficients;
	}

	private List<Frequency> doFFT(final List<Double> frame) {
		return new FastFourierTransform((1 / this.samplingRate) * this.samplePerFrame, frame).getFrequencies();
	}

	private List<Double> toLogScale(List<Double> linearSignal) {
		return linearSignal.stream().map(signal -> Math.log(signal)).collect(Collectors.toList());
	}

	private List<Double> applyMelFilter(final List<Frequency> frequencyDomain) {

		int bank[] = new int[numberOfMelFilters + 2];

		// lowest filter
		bank[0] = (int) Math.round(lowestFrequency / samplingRate * samplePerFrame);

		// highest filter
		bank[bank.length - 1] = (samplePerFrame / 2);

		for (int i = 1; i <= numberOfMelFilters; i++) {
			double middleFrequency = findLinearMiddleFrequency(i);
			bank[i] = (int) Math.round(middleFrequency / samplingRate * samplePerFrame);
		}

		final List<Double> filtered = new ArrayList<>();
		for (int k = 1; k <= numberOfMelFilters; k++) {
			double sum = 0.0;

			for (int i = bank[k - 1]; i <= bank[k]; i++) {
				sum += ((i - bank[k - 1] + 1) / (bank[k] - bank[k - 1] + 1)) * frequencyDomain.get(i).getAmplitude();
			}

			for (int i = bank[k] + 1; i <= bank[k + 1]; i++) {
				sum += (1 - ((i - bank[k]) / (bank[k + 1] - bank[k] + 1))) * frequencyDomain.get(i).getAmplitude();
			}

			filtered.add(sum);
		}

		return filtered;
	}

	private double findLinearMiddleFrequency(final int i) {
		double lowestMelFrequency = toMel(lowestFrequency);
		double highestMelFrequency = toMel(highestFrequency);
		return inverseMel(
				lowestMelFrequency + ((highestMelFrequency - lowestMelFrequency) / (numberOfMelFilters + 1)) * i);
	}

	private double inverseMel(final double x) {
		return 700 * (Math.pow(10, x / 2595) - 1);
	}

	protected double toMel(final double freq) {
		return 2595 * Math.log(1 + freq / 700) / Math.log(10);
	}
}