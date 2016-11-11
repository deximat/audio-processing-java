package com.codlex.audio.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.codlex.audio.transform.DiscreteFourierTransform;
import com.codlex.audio.transform.Frequency;

import lombok.extern.log4j.Log4j;

@Log4j
public class WavFile {

	private final File file;

	private RiffHeader riffHeader;
	private WaveHeader waveHeader;
	private DataHeader dataHeader;

	private Map<Integer, List<Double>> samplesPerChannel;
	
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
			this.dataHeader = new DataHeader(data);
			
			this.samplesPerChannel = readSamples(data);
			
			return true;

		} catch (FileNotFoundException e) {
			log.error("Provided file couldn't be found: ", e);
			return false;
		} catch (IOException e) {
			log.error("Couldn't read file content: ", e);
			return false;
		}
	}

	private Map<Integer, List<Double>> readSamples(ByteBuffer data) {
		final Map<Integer, List<Double>> samples = new HashMap<>();
		
		final int numberOfBlocks = this.dataHeader.getSubchunk2Size() / this.waveHeader.getBlockAlign();
		final int numberOfChannels = this.waveHeader.getNumChannels();
		final int bytesPerSample = this.waveHeader.getBitsPerSample() / 8;
		final double sampleMaxValue = bytesPerSample == 1 ? 255 : 1 << (this.waveHeader.getBitsPerSample() - 1) - 1;
		for (int block = 0; block < numberOfBlocks; block++) {
			for (int channel = 0; channel < numberOfChannels; channel++) {
				// get samples list for channel
				List<Double> channelSamples = samples.get(channel);
				if (channelSamples == null) {
					channelSamples = new ArrayList<>();
					samples.put(channel, channelSamples);
				}
				
				// calculate real sample value
				int rawValue = ByteBufferUtils.readVariableInt(data, bytesPerSample);
				if (bytesPerSample == 1) { // in this case value is unsigned
					rawValue &= 0xFF; // this will show unsigned value of byte
				}
				double realValue = rawValue / sampleMaxValue;
				
				// save to channel samples
				channelSamples.add(realValue);
			}
		}
		
		return samples;
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

	public List<Double> getSamples(final int channel) {
		return this.samplesPerChannel.get(channel);
	}
	
	public static void main(String[] args) {
		WavFile wavFile = WavFile.load("primer2.wav");
//		for (final Double sample : wavFile.getSamples(0)) {
//			System.out.println(sample);
//		}
		
		
		System.out.println(wavFile.getSamples(0).size());

		DiscreteFourierTransform transform = new DiscreteFourierTransform(0.23, wavFile.getSamples(0).subList(0, 10000));

		for (Frequency frequency : transform.getFrequencies()) {
			if (!frequency.isSilence()) {
				System.out.println(frequency);
			}
		}
	}

	public double getSamplingRate() {
		return this.waveHeader.getSampleRate();
	}


}
