package com.codlex.audio.transform;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import com.codlex.audio.util.Bits;

public class FastFourierTransform implements FourierTransform {

	private final double windowLengthSeconds;
	private final int n;
	private final List<Double> samples;
	private List<Frequency> frequencies;

	@Override
	public List<Frequency> getFrequencies() {
		return this.frequencies;
	}

	public FastFourierTransform(final double sampleDuration, final List<Double> windowToTransform) {		
		this.samples = new ArrayList<>(windowToTransform);
		
		if (!Bits.isPowerOfTwo(this.samples.size())) {
			int numberOfZerosNeeded = Bits.nextPowerOfTwo(this.samples.size()) - this.samples.size();
//			System.out.println("zero: " + numberOfZerosNeeded);
//			System.out.println(Bits.nextPowerOfTwo(this.samples.size()));
//			System.out.println(this.samples.size());
			
			this.samples.addAll(Collections.nCopies(numberOfZerosNeeded, 0.0));
		}
		
		this.n = this.samples.size();
		this.windowLengthSeconds = sampleDuration * this.n;
		
		this.frequencies = calculateFrequencies();
		
		this.frequencies = this.frequencies.subList(0, this.n / 2 + 1);
	}

	private List<Frequency> calculateFrequencies() {
		
		List<ComplexNumber> complexSamples = this.samples.stream()
				.map((sample) -> new ComplexNumber(sample))
				.collect(Collectors.toList());
		
		List<ComplexNumber> ffsResult = ffs(complexSamples);
		
		final AtomicInteger counter = new AtomicInteger();
		return ffsResult.stream().map((result) -> {
			double frequency = counter.getAndIncrement() / this.windowLengthSeconds;
			double amplitude = result.abs() / (this.n / 2);
			return new Frequency(frequency, amplitude);
		}).collect(Collectors.toList());
		
	}
	
	private List<ComplexNumber> ffs(List<ComplexNumber> samples) {
		
		if (samples.size() == 1) {	// base case
			return samples;
		}
		
		List<ComplexNumber> oddSamples = new ArrayList<>();
		List<ComplexNumber> evenSamples = new ArrayList<>();
		
		for (int i = 0; i < samples.size(); i++) {
			if (i % 2 == 0) {
				evenSamples.add(samples.get(i));
			} else {
				oddSamples.add(samples.get(i));
			}
		}
		
		List<ComplexNumber> ffsOdd = ffs(oddSamples);
		List<ComplexNumber> ffsEven = ffs(evenSamples);
		
		List<ComplexNumber> combination = new ArrayList<>(Collections.nCopies(samples.size(), null));
		for (int k = 0; k < samples.size() / 2; k++) {
			double tetha = -2 * k * Math.PI / samples.size();
			ComplexNumber wk = new ComplexNumber(Math.cos(tetha), Math.sin(tetha));
			combination.set(k, ffsEven.get(k).plus(wk.multiply(ffsOdd.get(k))));
			combination.set(k + samples.size() / 2, ffsEven.get(k).minus(wk.multiply(ffsOdd.get(k))));
		}
		
		return combination;
	}
	
}
