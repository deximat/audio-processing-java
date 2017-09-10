package com.codlex.audio.final_project.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.codlex.audio.enpointing.Word;
import com.codlex.audio.projekat.JavaSoundRecorder;

import lombok.Getter;

public class WordModel {
	
	@Getter
	private final List<SampleModel> samples = new ArrayList<>();
	
	private final File location;
	
	public WordModel(final File word) {
		this.location = word;
		if (word.exists()) {
			load(word);
		} else {
			word.mkdir();
		}
	}

	private void load(final File word) {
		for (final File sample : word.listFiles()) {
			if (sample.isDirectory()) {
				throw new RuntimeException();
			}
			addSample(sample);
		}
	}
	
	private void addSample(final File sample) {
		this.samples.add(new SampleModel(sample));
		// train();
	}

	@Override
	public String toString() {
		return "Word(name=" + getName() + ", samples=" + this.samples.size() + ")";
	}

	public void delete() {
		try {
			FileUtils.deleteDirectory(this.location);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getName() {
		return this.location.getName();
	}
	
	private int findSampleIndexByName(String name) {
		
		for (int i = 0; i < this.samples.size(); i++) {
			final SampleModel sample = this.samples.get(i);
			if (sample.getName().equals(name)) {
				return i;
			}
		}
		
		return -1;
	}
	public void newSample(String name) {
		name = name + ".wav";
		if (findSampleIndexByName(name) != -1) {
			throw new RuntimeException("Can't have two samples with same name.");
		}
		
		final File file = new File(this.location, name);
		JavaSoundRecorder.recordSample(file.getAbsolutePath());
		addSample(file);
	}

	public void removeSample(String name) {
		name = name + ".wav";
		final int index = findSampleIndexByName(name);
		if (index == -1) {
			throw new RuntimeException("Tried to remove sample that doesn't exist.");
		}
		
		SampleModel sample = this.samples.remove(index);
		sample.delete();
	}


}
