package com.codlex.audio.pg.domaci3;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

/**
 * A sample program is to demonstrate how to record sound in Java
 * author: www.codejava.net
 */
public class JavaSoundRecorderCLI {
	
	private static final int SILENCE_TIME = 1000;

	private String name;

	public JavaSoundRecorderCLI(String name) {
		this.name = name;
		this.wavFile = new File(this.name);
	}
	
	// record duration, in milliseconds
	static final long RECORD_TIME = 4000;	

	// path of the wav file
	File wavFile;

	// format of audio file
	AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;

	// the line from which audio data is captured
	TargetDataLine line;

	/**
	 * Defines an audio format
	 */
	AudioFormat getAudioFormat() {
		float sampleRate = 48000;
		int sampleSizeInBits = 16;
		int channels = 1;
		boolean signed = true;
		boolean bigEndian = true;
		AudioFormat format = new AudioFormat(sampleRate, sampleSizeInBits,
											 channels, signed, bigEndian);
		return format;
	}

	/**
	 * Captures the sound and record into a WAV file
	 */
	void start() {
		try {
			AudioFormat format = getAudioFormat();
			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

			// checks if system supports the data line
			if (!AudioSystem.isLineSupported(info)) {
				System.out.println("Line not supported");
				System.exit(0);
			}
			line = (TargetDataLine) AudioSystem.getLine(info);
			line.open(format);
			line.start();	// start capturing
			System.out.println("Silence!");
			// System.out.println("Start capturing...");

			AudioInputStream ais = new AudioInputStream(line);

			// System.out.println("Start recording...");

			// start recording
			AudioSystem.write(ais, fileType, wavFile);

		} catch (LineUnavailableException ex) {
			ex.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	/**
	 * Closes the target data line to finish capturing and recording
	 */
	void finish() {
		line.stop();
		line.close();
		System.out.println("Finished");
	}


	public static File recordSample(String name) {		
		final JavaSoundRecorder recorder = new JavaSoundRecorder(name);

		final Semaphore semaphore = new Semaphore(0);
		// creates a new thread that waits for a specified
		// of time before stopping
		Thread stopper = new Thread(new Runnable() {
			public void run() {
				try {
					Thread.sleep(SILENCE_TIME);
					System.out.println("Talk:");
					Thread.sleep(RECORD_TIME - SILENCE_TIME);
					System.out.println("Finished");
				} catch (InterruptedException ex) {
					ex.printStackTrace();
				}
				recorder.finish();
				semaphore.release();
			}
		});
		stopper.start();

		// start recording
		recorder.start();
		
		try {
			semaphore.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return recorder.wavFile;
		
	}
	
	
}
