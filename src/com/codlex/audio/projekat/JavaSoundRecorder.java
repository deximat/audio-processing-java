package com.codlex.audio.projekat;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Dialog;
import javafx.util.Duration;

/**
 * A sample program is to demonstrate how to record sound in Java
 * author: www.codejava.net
 */
public class JavaSoundRecorder {
	
	private static final int SILENCE_TIME = 1000;

	private String name;

	public JavaSoundRecorder(String name) {
		this.name = name;
		this.wavFile = new File(this.name);
	}
	
	// record duration, in milliseconds
	static final long RECORD_TIME = 20000;	

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

	
	private final static ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor();
	
	public static void recordSample(String name, Consumer<File> onFinish) {
		final JavaSoundRecorder recorder = new JavaSoundRecorder(name);
				
		final Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Recording...");
		alert.setHeaderText("Recording will begin shortly, be quiet.");
		alert.show();
		
		PauseTransition delay = new PauseTransition(Duration.millis(SILENCE_TIME));
		delay.setOnFinished( event -> {
			alert.setHeaderText("Talk!");
			alert.setAlertType(AlertType.WARNING);
			
			PauseTransition delay2 = new PauseTransition(Duration.millis(RECORD_TIME));
			delay2.setOnFinished( event2 -> {
				alert.setHeaderText("Recording finished");
				alert.setAlertType(AlertType.CONFIRMATION);
				recorder.finish();
				onFinish.accept(recorder.wavFile);
			});		
			delay2.play();
		});		
		delay.play();
		
		new Thread(() -> recorder.start()).start();
	}
	
	
}