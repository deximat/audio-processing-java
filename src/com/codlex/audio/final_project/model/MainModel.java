package com.codlex.audio.final_project.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class MainModel {	
	
	final File dictionaryLocation = new File("data/dictionary/");
	
	@Getter
	private List<DictionaryModel> dictionaries = new ArrayList<>();
	
	@Getter
	private List<TestModel> tests = new ArrayList<>();
	
	public void load() {
		System.out.println("Loading library...");
		this.dictionaries = DictionaryModel.loadAll(this.dictionaryLocation);
		// this.tests = TestModel.loadAll("data/test/");
	}

	public DictionaryModel newDictionary(String name) {
		final DictionaryModel dictionary = new DictionaryModel(new File(this.dictionaryLocation, name));
		this.dictionaries.add(dictionary);
		return dictionary;
	}

	public DictionaryModel findDictionaryByName(String name) {
		return this.dictionaries.get(findDictionaryIndexByName(name));
	}
	
	private int findDictionaryIndexByName(String name) {
		for (int i = 0; i < this.dictionaries.size(); i++) {
			DictionaryModel dictionary = this.dictionaries.get(i);
			if (dictionary.getName().equals(name)) {
				return i;
			}
		}
		
		return -1;
	}
	
	public void removeDictionary(String name) {
		int index = findDictionaryIndexByName(name);
		if (index == -1) {
			System.out.println("Tried to remove dictionary which does not exist.");
			return;
		}
		
		DictionaryModel dictionary = this.dictionaries.remove(index);
		dictionary.remove();
	}
}
