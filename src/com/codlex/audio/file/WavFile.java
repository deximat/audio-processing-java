package com.codlex.audio.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;

import lombok.extern.log4j.Log4j;

@Log4j
public class WavFile {

	private final File file;

	private RiffHeader riffHeader;
	private WaveHeader waveHeader;

	private WavFile(String file) {
		this.file = new File(file);
	}

	public boolean load() {
		try {
			final ByteBuffer data = ByteBuffer
					.wrap(Files.readAllBytes(this.file.toPath()));
			data.order(ByteOrder.LITTLE_ENDIAN);
			this.riffHeader = new RiffHeader(data);
			if (!this.riffHeader.isWave()) {
				throw new RuntimeException("Unsupported format.");
			}
			this.waveHeader = new WaveHeader(data);
			// this.data = new DataHeader();

			return true;

		} catch (FileNotFoundException e) {
			log.error("Provided file couldn't be found: ", e);
			return false;
		} catch (IOException e) {
			log.error("Couldn't read file content: ", e);
			return false;
		}
	}

	public static final WavFile load(String file) {
		final WavFile wavFile = new WavFile(file);
		boolean success = wavFile.load();
		if (!success) {
			throw new RuntimeException("Loading of wave file failed.");
		}
		return wavFile;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("WaveFile(file=");
		builder.append(this.file);
		builder.append(",\n");
		builder.append("\t");
		builder.append(this.riffHeader);
		builder.append(",\n");
		builder.append("\t");
		builder.append(this.waveHeader);
		builder.append("\n)");
		return builder.toString();
	}
	

	public static void main(String[] args) {
		WavFile wavFile = WavFile.load("primer.wav");
		System.out.println(wavFile);
	}
}
