package com.codlex.audio.final_project.model;

import java.io.File;

import com.codlex.audio.enpointing.Word;

public class SampleModel {
	
	private final File location;

	// old word is used for now as sample initialization
	private final Word word;

	public SampleModel(final File location) {
		this.location = location;
		
		this.word = Word.loadSingle(this.location.getAbsolutePath());
	}
	
	public String getName() {
		return this.location.getName();
	}

	public void delete() {
		this.location.delete();
	}
	
	public String toString() {
		return "Sample(name=" + this.location.getName() + ")";
	}

}
