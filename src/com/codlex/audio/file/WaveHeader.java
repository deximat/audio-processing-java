package com.codlex.audio.file;

import java.nio.ByteBuffer;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class WaveHeader {

	private final String subchunk1Id;
	private final int subchunk1Size;
	private final short audioFormat;
	private final short numChannels;
	private final int sampleRate;
	private final int byteRate;
	private final short blockAlign;
	private final short bitsPerSample;
	
	public WaveHeader(final ByteBuffer data) {
		this.subchunk1Id = ByteBufferUtils.getString(data, 4);
		this.subchunk1Size = data.getInt();
		this.audioFormat = data.getShort();
		this.numChannels = data.getShort();
		this.sampleRate = data.getInt();
		this.byteRate = data.getInt();
		this.blockAlign = data.getShort();
		this.bitsPerSample = data.getShort();
	}
	
}
