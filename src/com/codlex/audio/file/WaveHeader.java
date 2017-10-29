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
	
	public WaveHeader() {
		// for generated only
		this.subchunk1Id = "fmt ";
		this.subchunk1Size = 16;
		this.audioFormat = 1;
		this.numChannels = 1;
		this.sampleRate = 44100;
		this.bitsPerSample = 16;
		this.byteRate = this.sampleRate * this.numChannels * this.bitsPerSample / 8;
		this.blockAlign = (short) (this.numChannels * this.bitsPerSample/8);
	}
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

	public void write(ByteBuffer data) {
		for (char character : this.subchunk1Id.toCharArray()) {
			data.put((byte) character);
		}
		data.putInt(this.subchunk1Size);
		data.putShort(this.audioFormat);
		data.putShort(this.numChannels);
		data.putInt(this.sampleRate);
		data.putInt(this.byteRate);
		data.putShort(this.blockAlign);
		data.putShort(this.bitsPerSample);
	}
	
}
