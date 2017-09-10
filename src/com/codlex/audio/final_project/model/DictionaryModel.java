package com.codlex.audio.final_project.model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import lombok.Getter;

public class DictionaryModel {
	
	private final File location;
	
	@Getter
	private final List<WordModel> words = new ArrayList<>();
	
	public DictionaryModel(final File location) {
		this.location = location;
		if (location.exists()) {
			load(location);
		} else {
			if (!location.mkdir()) {
				System.out.println("Failed to create directory: " + location);
			}
		}
	}
	
	private void load(final File data) {
		for (File word : data.listFiles()) {
			if (!word.isDirectory()) {
				throw new RuntimeException("Word must be directory containing samples.");
			}
			this.words.add(new WordModel(word));
		}		
	}

	public static List<DictionaryModel> loadAll(final File location) {
		System.out.println("Loading dictionaries...");
		final List<DictionaryModel> dictionaries = new ArrayList<>();
		for (final File file : location.listFiles()) {
			if (!file.isDirectory()) {
				throw new RuntimeException("Dictionary must be directory containing word directories.");
			}
			DictionaryModel dictionary = new DictionaryModel(file);
			dictionaries.add(dictionary);
			System.out.println(dictionary + " loaded.");
		}
		
		return dictionaries;
	}
	
	public String toString() {
		return "Dictionary(name="+ this.location.getName() + ", words=" + this.words.size() + ")";
	}

	public String getName() {
		return this.location.getName();
	}
	
	public void delete() {
		try {
			FileUtils.deleteDirectory(this.location);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public WordModel newWord(final String name) {
		final WordModel word = new WordModel(new File(this.location, name));
		this.words.add(word);
		return word;
	}
	
	public WordModel removeWord(final String name) {
		int index = findWordIndexByName(name);
		if (index == -1) {
			System.out.println("Tried to remove word which doesn't exist: " + name);
			return null;
		}
		
		final WordModel word = this.words.remove(index);
		word.delete();
		return word;
	}	
	
	private int findWordIndexByName(String name) {
		for (int i = 0; i < this.words.size(); i++) {
			final WordModel word = this.words.get(i);
			if (word.getName().equals(name)) {
				return i;
			}
		}
		
		return -1;
	}
	
	public WordModel findWordByName(String name) {
		return this.words.get(findWordIndexByName(name));
	} 
}

