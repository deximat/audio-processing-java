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

}
