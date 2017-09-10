package com.codlex.audio.transform;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Frequency {

	private double frequency;
	private double amplitude;

	
	
	public boolean isSilence() {
		final double zeroEpsilon = 1e-2;
		return Math.abs(this.amplitude) <= zeroEpsilon;
	}

	@Override
	public String toString() {
		return String.format("Freq:%fHZ, Amplitude: %.5f", this.frequency,
				this.amplitude);
	}

}
