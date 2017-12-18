package com.codlex.audio.pg.domaci3;

import java.io.File;
import java.util.Scanner;

import com.codlex.audio.enpointing.Word;
import com.codlex.audio.windowing.WindowFunction;

public class Domaci3 {

	private static Dictionary mainDictionary = new Dictionary();

	private static WindowFunction numberToFunction(int number) {
		switch (number) {
		case 0:
			return WindowFunction.None;
		case 1:
			return WindowFunction.Hamming;
		case 2:
			return WindowFunction.Hanning;
		default:
			return WindowFunction.None;
		}
	}
	
	public static void main(String[] args) {
		Scanner in = new Scanner(System.in);
		System.out.print("Please enter number of coefficients for MFCC:");
		AudioConstants.mfccCoefficients = in.nextInt();
		System.out.println("Please enter size of window for MFCC in MS:");
		AudioConstants.windowDurationMs = in.nextInt();
		System.out.println("Please choose windowing function: None(0), Hamming(1) or Hanning(2):");
		AudioConstants.windowFunction = numberToFunction(in.nextInt());
		System.out.println("Loading mainDictionary...");
		// load wav files
		mainDictionary = loadDictionary(new File("mainDictionary"));
		System.out.println("mainDictionary loaded!");

		
		while (true) {
			System.out.println("Welcome to speech recognition, choose your option:");
			System.out.println("[1] Run all tests");
			System.out.println("[2] Record new word and add to dictionary");
			System.out.println("[3] Record new word and find in dictionary");
			System.out.println("To add wave file to dictionary, just paste into dictionary folder.");

			switch (in.nextInt()) {
			case 1:
				runAllTests();
				break;
			case 2:
				System.out.println("Name of word (unique), please?");
				recordAndAddToDictionary(in.next());
				break;
			case 3:
				System.out.println("Name of word (unique), please?");
				recordAndFindInDictionary(in.next());
				break;
			}

		}
	}

	private static void recordAndFindInDictionary(String name) {
		File file = JavaSoundRecorderCLI.recordSample(name);
		Word word = mainDictionary.findWord(Word.loadSingle(name));

		if (word == null) {
			System.out.println("Word not found!");
		} else {
			System.out.println("Succesfully found word: " + word);
		}
	}

	private static void recordAndAddToDictionary(String name) {
		String filename = "mainDictionary/" + name;
		File file = JavaSoundRecorderCLI.recordSample(filename);
		mainDictionary.addWord(Word.loadSingle(filename));
		System.out.println("Succesfully added to dictionary.");
	}

	private static void runAllTests() {

		System.out.println("Running recnik with 10 words test:");

		// create dictionary
		File recnik10ReciFile = new File("testData/recnik10Reci/reci");
		Dictionary recnik10Reci = loadDictionary(recnik10ReciFile);
		File recnik10ReciTestFile = new File("testData/recnik10Reci/pozitivni-testovi");
		doPositiveTests(recnik10Reci, recnik10ReciTestFile, recnik10ReciFile);
		doNegativeTests(recnik10Reci, "testData/recnik10Reci/negativni-testovi");
		System.out.println("Testing unknown speaker:");
		doNegativeTests(recnik10Reci, "testData/recnik10Reci/negativni-testovi-govornik");
		System.out.println();
		System.out.println();
		System.out.println();
		
		System.out.println("Running recnik with 30 words test:");

		File recnik30ReciFile = new File("testData/recnik30Reci/reci");
		Dictionary recnik30Reci = loadDictionary(recnik30ReciFile);
		File recnik30ReciTestFile = new File("testData/recnik30Reci/pozitivni-testovi");
		doPositiveTests(recnik30Reci, recnik30ReciTestFile, recnik30ReciFile);
		doNegativeTests(recnik30Reci, "testData/recnik30Reci/negativni-testovi");
		System.out.println("Testing unknown speaker:");
		doNegativeTests(recnik30Reci, "testData/recnik30Reci/negativni-testovi-govornik");

		System.out.println();
		System.out.println();
		System.out.println();

	}

	private static Dictionary loadDictionary(File base) {
		Dictionary dictionary = new Dictionary();
		for (String name : base.list()) {
			dictionary.addWord(Word.loadSingle(base.getAbsolutePath() + "/" + name));
		}
		return dictionary;
	}

	private static void doNegativeTests(Dictionary dictionary, String directory) {
		int successfulDetection = 0;
		int detectedWrongWord = 0;
		File negativeTestBase = new File(directory);
		for (String name : negativeTestBase.list()) {

			Word wordToCheck = Word.loadSingle(negativeTestBase.getAbsolutePath() + "/" + name);
			Word foundWord = dictionary.findWord(wordToCheck);
			if (foundWord == null) {
				successfulDetection++;
			} else {
				detectedWrongWord++;
//				System.out.println(
//						wordToCheck.getName() + " detected " + foundWord.getName() + " but should be negative.");

			}
		}

		System.out.println("Negative Success: "
				+ ((successfulDetection * 100) / (double) (successfulDetection + detectedWrongWord))+"%") ;
	}

	private static void doPositiveTests(Dictionary dictionary, File testBase, File base) {
		int correct = 0;
		int incorrect = 0;
		int notFound = 0;

		System.out.println("Running positive tests...");
		for (String name : testBase.list()) {
			if (name.contains(".DS_Store")) {
				continue;
			}
			
			Word wordToCheck = Word.loadSingle(testBase.getAbsolutePath() + "/" + name);
			System.out.println("Finding word " + wordToCheck + "...");

			Word foundWord = dictionary.findWord(wordToCheck);
			
			if (foundWord == null) {
				notFound++;
				System.out.println("not found " + wordToCheck.getName());
				continue;
			} else {
				System.out.println("Found word " + foundWord + "...");
			}

			if (wordToCheck.getName()
					.substring(testBase.getAbsolutePath().length(), testBase.getAbsolutePath().length() + 3)
					.equals(foundWord.getName().substring(base.getAbsolutePath().length(),
							base.getAbsolutePath().length() + 3))) {
				
				// System.out.println("Correct: " + name);
				correct++;
			} else {
				System.out.println("Incorrect: " + wordToCheck.getName() + " " + foundWord.getName());
				incorrect++;
			}
		}
		System.out.println("Positive tests success: " + ((correct * 100) / (double) (correct + incorrect))
				+ "%, tests run: " + testBase.list().length + " not found: " + notFound);
	}

}
