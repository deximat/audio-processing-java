package com.codlex.audio.final_project.model;

import java.util.Scanner;

import com.codlex.audio.enpointing.Word;
import com.codlex.audio.projekat.JavaSoundRecorder;

public class ModelTest {
	
	private static final MainModel model = new MainModel();
	
	
	private static void printLibrary() {
		System.out.println("##############     Library   ###################");

		System.out.println("- Dictionaries: ");
		if (model.getDictionaries().isEmpty()) {
			System.out.println("No dictionaries defined.");
		}
		for (DictionaryModel dictionary : model.getDictionaries()) {
			System.out.println(dictionary);
		}
		System.out.println("");
		
		System.out.println("- Test suites: ");
		if (model.getDictionaries().isEmpty()) {
			System.out.println("No test suites defined.");
		}
		for (TestModel test : model.getTests()) {
			System.out.println(test);
		}
		
		System.out.println("################################################");
		System.out.println("");

	}
	
	public static void main(String[] arfs) {
		model.load();
		
		printLibrary();
		
		final Scanner in = new Scanner(System.in);
		
		
		while (true) {
			System.out.println("Choose command: ");
			System.out.println("newdict - creates new dictionary");
			System.out.println("rmdict - creates new test suite");
			System.out.println("newtest - creates new test suite");
			System.out.println("selectdict - creates new test suite");
			System.out.println("ls - prints whole libarry");
			
			String command = in.next();
			switch (command) {
			case "newdict":
				System.out.println("Enter dictionary name:");
				String name = in.next();
				DictionaryModel dictionary = model.newDictionary(name);
				System.out.println(dictionary + " created.");
				break;
			case "rmdict":
				System.out.println("Enter dictionary name:");
				String rmName = in.next();
				model.removeDictionary(rmName);
				break;
			case "selectdict":
				System.out.println("Enter dicitonary name:");
				processDictionaryCommans(in, in.next());
				break;
			case "newtest":
				break;
			case "ls":
				printLibrary();
				break;
			}
		}
	}

	private static void processDictionaryCommans(Scanner in, String name) {
		final DictionaryModel dictionary = model.findDictionaryByName(name);
		System.out.println("Selected " + dictionary);
		printDictionary(dictionary);
		while (true) {
			System.out.println("newword - adds new word to dicitonary");
			System.out.println("rmword - removes word from dictionary");
			System.out.println("ls - prints dicitonary");
			System.out.println("exit - returns to library");
			System.out.println("selectword - selects word operations");
			System.out.println("test - tests sample to dicitonary");
			
			String command = in.next();
			
			switch (command) {
			case "newword":
				System.out.println("Enter word name:");
				WordModel newWord = dictionary.newWord(in.next());
				System.out.println(newWord + " added to " + dictionary);
				break;
			case "rmword":
				System.out.println("Enter word name:");
				WordModel removedWord = dictionary.removeWord(in.next());
				System.out.println(removedWord + " removed from " + dictionary);
				break;
			case "selectword":
				System.out.println("Enter word name:");
				processWord(in, dictionary, in.next());
				break;
			case "ls":
				printDictionary(dictionary);
				break;
			case "exit":
				return;
			}
		}
		
	}

	private static void processWord(Scanner in, DictionaryModel dictionary, final String name) {
		final WordModel word = dictionary.findWordByName(name);
		System.out.println("Selected " + word);
		printWord(word);
		
		while (true) {
			System.out.println("newsample - records new sample to word");
			System.out.println("rmsample - removes sample from word");
			System.out.println("ls - prints word");
			System.out.println("exit - returns to dictionary");
			
			String command = in.next();
			
			switch (command) {
			case "newsample":
				System.out.println("Enter sample name:");
				word.newSample(in.next());
				System.out.println("Added new sample");
				break;
			case "rmsample":
				System.out.println("Enter sample name:");
				word.removeSample(in.next());				
				System.out.println("Removed sample");
				break;
			case "ls":
				printWord(word);
				break;
			case "exit":
				return;
			}
		}		
	}

	private static void printWord(WordModel word) {
		if (word.getSamples().isEmpty()) {
			System.out.println("No samples in this word.");
		}
		
		for (SampleModel sample : word.getSamples()) {
			System.out.println(sample);
		}
	}

	private static void printDictionary(DictionaryModel dictionary) {
		if (dictionary.getWords().isEmpty()) {
			System.out.println("No words in this dictionary.");
		}
		for (WordModel word : dictionary.getWords()) {
			System.out.println(word);
		}
		System.out.println();
	}
}