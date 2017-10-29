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
		
	public RiffHeader(int subchunk2Size) {
		this.riffTag = "RIFF";
		this.format = WAVE_FORMAT;
		this.chunkSize = 36 + subchunk2Size;
	}
	
	public RiffHeader(final ByteBuffer data) {
		this.riffTag = ByteBufferUtils.getString(data, 4);
		this.chunkSize = data.getInt();
		this.format = ByteBufferUtils.getString(data, 4);
	}

	public boolean isWave() {
		return Objects.equals(WAVE_FORMAT, this.format);
	}

	public void write(ByteBuffer data) {
		
		for (char character : this.riffTag.toCharArray()) {
			data.put((byte) character);
		}
		
		data.putInt(this.chunkSize);
		
		for (char character : this.format.toCharArray()) {
			data.put((byte) character);
		}
		
	}
	


}
