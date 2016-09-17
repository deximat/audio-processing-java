package com.codlex.audio.file;

import java.nio.ByteBuffer;

import java.util.Objects;

import lombok.ToString;

@ToString
public class RiffHeader {
	
	private static final String WAVE_FORMAT = "WAVE";
	
	private final String riffTag;
	private int chunkSize;
	private final String format;
		
	public RiffHeader(final ByteBuffer data) {
		this.riffTag = ByteBufferUtils.getString(data, 4);
		this.chunkSize = data.getInt();
		this.format = ByteBufferUtils.getString(data, 4);
	}

	public boolean isWave() {
		return Objects.equals(WAVE_FORMAT, this.format);
	}

}
