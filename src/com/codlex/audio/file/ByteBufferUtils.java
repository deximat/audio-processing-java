package com.codlex.audio.file;

import java.nio.ByteBuffer;

public class ByteBufferUtils {
	
	public static String getString(final ByteBuffer data, final int length) {
		final StringBuilder builder = new StringBuilder();

		for (int i = 0; i < length; i++) {
			builder.append((char) data.get());
		}
		
		return builder.toString();
	}

	public static int readVariableInt(ByteBuffer data, int bytes) {
		switch (bytes) {
			case 1:
				return data.get();
			case 2:
				return data.getShort();
			case 4:
				return data.getInt();
			default:
				throw new RuntimeException("Unsupported sample size.");
		}
	}

}
