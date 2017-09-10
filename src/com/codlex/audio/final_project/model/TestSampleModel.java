package com.codlex.audio.final_project.model;

import java.io.File;

import org.apache.log4j.chainsaw.Main;

import com.codlex.audio.enpointing.Word;

public class TestSampleModel {
	
	private static final int WORD_INDEX = 0;
	private static final int DESCRIPITON_INDEX = 1;
	private static final int TEST_TYPE_INDEX = 2;
	private static final String POSITIVE_TEST = "p";
	
	private File location;
	
	private Word word;

	public TestSampleModel(final File location) {
		this.location = location;
		this.word = Word.loadSingle(this.location.getAbsolutePath());
	}
	
	public String getExpectedWord() {
		return this.location.getName().split("-")[WORD_INDEX];
	}
	
	public String getDescription() {
		return this.location.getName().split("-|\\.")[DESCRIPITON_INDEX];
	}
	
	public String toString() {
		return "TestSample(word=" + getExpectedWord() + ", description=" + getDescription() + ")";
	}

	public void delete() {
		this.location.delete();
	}

	public String getName() {
		return this.location.getName();
	}
	
	public boolean shouldFind() {
		return this.location.getName().split("-|\\.")[TEST_TYPE_INDEX].equals(POSITIVE_TEST);
	}

}
