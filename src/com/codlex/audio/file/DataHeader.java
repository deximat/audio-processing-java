package com.codlex.audio.file;

import java.nio.ByteBuffer;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class DataHeader {

	private final String subchunk2Id;
	private final int subchunk2Size;
	
	public DataHeader(ByteBuffer data) {
		this.subchunk2Id = ByteBufferUtils.getString(data, 4);
		this.subchunk2Size = data.getInt();
	}

}
