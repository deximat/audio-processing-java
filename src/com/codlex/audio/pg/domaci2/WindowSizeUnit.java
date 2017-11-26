package com.codlex.audio.pg.domaci2;

public enum WindowSizeUnit {
	Milliseconds(1), Samples(1);
	
	private int multiplier;
	
	WindowSizeUnit(int multiplier) {
		this.multiplier = multiplier;
	}
	
	public int getMultiplier() {
		return this.multiplier;
	}
}
