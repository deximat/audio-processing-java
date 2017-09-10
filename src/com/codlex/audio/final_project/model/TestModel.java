package com.codlex.audio.final_project.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.codlex.audio.projekat.JavaSoundRecorder;

import lombok.Getter;

public class TestModel {
	
	private static final File TMP_TEST = new File("data/");
	
	@Getter
	private List<TestSampleModel> samples = new ArrayList<>();
	
	private File location;

	public TestModel(final File location) {
		this.location = location;
		if (location.exists()) {
			load(location);
		} else {
			if (!location.mkdir()) {
				System.out.println("Failed to test directory: " + location);
			}
		}
	}

	private void load(final File location) {
		for (final File sample : location.listFiles()) {
			if (sample.isDirectory()) {
				throw new RuntimeException("Found directory in test folder, where only samples can be.");
			}
			this.samples.add(new TestSampleModel(sample));
		}
	}
	
	public String getName() {
		return this.location.getName();
	}
	
	@Override
	public String toString() {
		return "Test(name=" + getName() +", samples=" + this.samples.size() + ")";
	}
	
	private int findSampleIndexByName(String name) {
		
		for (int i = 0; i < this.samples.size(); i++) {
			final TestSampleModel sample = this.samples.get(i);
			if (sample.getName().equals(name)) {
				return i;
			}
		}
		
		return -1;
	}
	
	
	private static String makeName(String name, String description) {
		return name + "-" + description + ".wav";
	}
	
	public static TestSampleModel newSample() {
		return newSample(TMP_TEST, "TMP", "TMP");
	}
	
	private static TestSampleModel newSample(final File location, String word, String description) {
		final String name = makeName(word, description);
		final File file = new File(location, name);
		JavaSoundRecorder.recordSample(file.getAbsolutePath());
		return new TestSampleModel(file);
	} 
	
	public void newSample(String name, String description) {
		this.samples.add(newSample(this.location, name, description));
	}

	public void removeSample(String name, String description) {
		name = makeName(name, description);
		final int index = findSampleIndexByName(name);
		if (index == -1) {
			throw new RuntimeException("Tried to remove sample that doesn't exist.");
		}
		
		TestSampleModel sample = this.samples.remove(index);
		sample.delete();
	}

	public void delete() {
		try {
			FileUtils.deleteDirectory(this.location);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static List<TestModel> loadAll(File testLocation) {
		System.out.println("Loading tests...");
		final List<TestModel> tests = new ArrayList<>();
		for (final File file : testLocation.listFiles()) {
			if (!file.isDirectory()) {
				throw new RuntimeException("Test suite must be directory containing samples.");
			}
			TestModel test = new TestModel(file);
			tests.add(test);
			System.out.println(test + " loaded.");
		}
		
		return tests;
	}

}
