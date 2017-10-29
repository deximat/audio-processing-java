package com.codlex.audio.file;

import java.nio.ByteBuffer;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class DataHeader {

	private final String subchunk2Id;
	private final int subchunk2Size;
	
	public DataHeader(int subchunkSize) {
		this.subchunk2Id = "data";
		this.subchunk2Size = subchunkSize;
	}
	
	public DataHeader(ByteBuffer data) {
		this.subchunk2Id = ByteBufferUtils.getString(data, 4);
		this.subchunk2Size = data.getInt();
	}

	public void write(ByteBuffer data) {
		for (char character : this.subchunk2Id.toCharArray()) {
			data.put((byte) character);
		}
		data.putInt(this.subchunk2Size);
	}

}
