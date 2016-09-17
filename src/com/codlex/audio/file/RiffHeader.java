package com.codlex.audio.file;

import java.nio.ByteBuffer;

import java.util.Objects;

import lombok.ToString;

@ToString(of={"riffTag", "chunkSize", "format"})
public class RiffHeader {
	
	private static final String WAVE_FORMAT = "WAVE";
	
	private final String riffTag;
	private int chunkSize;
	private final String format;
		
	public RiffHeader(final ByteBuffer data) {
		this.riffTag = readRiffTag(data);
		this.chunkSize = data.getInt();
		this.format = readFormatTag(data);
	}
	
	private String readFormatTag(ByteBuffer data) {
		final StringBuilder builder = new StringBuilder();
		
		final int size = 4;
		for (int i = 0; i < size; i++) {
			builder.append((char) data.get());
		}
		
		return builder.toString();
	}

	private String readRiffTag(ByteBuffer data) {
		final StringBuilder builder = new StringBuilder();
		
		final int size = 4;
		for (int i = 0; i < size; i++) {
			builder.append((char) data.get()); 
		}
		
		return builder.toString();
	}

	public boolean isWave() {
		return Objects.equals(WAVE_FORMAT, this.format);
	}

}
