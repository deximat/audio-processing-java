package com.codlex.audio.final_project.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class MainModel {	
	
	final File dictionaryLocation = new File("data/dictionary/");
	final File testLocation = new File("data/test/");

	@Getter
	private List<DictionaryModel> dictionaries = new ArrayList<>();
	
	@Getter
	private List<TestModel> tests = new ArrayList<>();
	
	public void load() {
		System.out.println("Loading library...");
		this.dictionaries = DictionaryModel.loadAll(this.dictionaryLocation);
		this.tests = TestModel.loadAll(this.testLocation);
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
	
	private int findTestIndexByName(String name) {
		for (int i = 0; i < this.tests.size(); i++) {
			TestModel test = this.tests.get(i);
			if (test.getName().equals(name)) {
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
		dictionary.delete();
	}

	public TestModel newTest(final String name) {
		final TestModel test = new TestModel(new File(this.testLocation, name));
		this.tests.add(test);
		return test;
	}

	public TestModel removeTest(String name) {
		int index = findTestIndexByName(name);
		if (index == -1) {
			throw new RuntimeException("Tried to remove test suite which does not exist.");
		}
		
		final TestModel test = this.tests.remove(index);
		test.delete();
		return null;
	}

	public TestModel findTestByName(final String name) {
		return this.tests.get(findTestIndexByName(name));
	}
}
