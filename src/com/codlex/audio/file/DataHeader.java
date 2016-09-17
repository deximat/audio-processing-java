package com.codlex.audio.file;

import java.nio.ByteBuffer;

import lombok.ToString;

@ToString
public class DataHeader {

	private final String subchunk2Id;
	private final int subchunk2Size;
	
	public DataHeader(ByteBuffer data) {
		this.subchunk2Id = ByteBufferUtils.getString(data, 4);
		this.subchunk2Size = data.getInt();
	}

}
