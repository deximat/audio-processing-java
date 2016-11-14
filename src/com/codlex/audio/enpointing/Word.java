package com.codlex.audio.enpointing;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Word {
	
	private double sampleDuration;
	private int start;
	private int end;
	
	@Override
	public String toString() {
		return String.format("Word(%.2f, %.2f)", this.start * sampleDuration, this.end * sampleDuration);
	}
	
	public boolean isWholeSignal() {
		return false;
	}
}
