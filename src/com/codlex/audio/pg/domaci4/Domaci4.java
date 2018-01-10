package com.codlex.audio.pg.domaci4;

import java.io.File;
import java.util.Random;
import java.util.Scanner;

import com.codlex.audio.enpointing.Word;
import com.codlex.audio.hmm.model.HMMDictionary;
import com.codlex.audio.pg.domaci3.AudioConstants;
import com.codlex.audio.pg.domaci3.Dictionary;
import com.codlex.audio.pg.domaci3.JavaSoundRecorderCLI;
import com.codlex.audio.windowing.WindowFunction;

public class Domaci4 {
	public static void main(String[] args) {
		final Scanner in = new Scanner(System.in);

		System.out.print("Please enter number of coefficients for MFCC:");
		AudioConstants.mfccCoefficients = in.nextInt();

		System.out.println("Please choose windowing function: None(0), Hamming(1) or Hanning(2):");
		AudioConstants.windowFunction = numberToFunction(in.nextInt());

		HMMDictionary dict = new HMMDictionary();

		while (true) {
			System.out.println("Welcome to speech recognition, choose your option:");
			System.out.println("[1] Run all tests");
			System.out.println("[2] Record new word and add to dictionary");
			System.out.println("[3] Record new word and find in dictionary");
			System.out.println("[4] Add folder with wav files as new word");
			System.out.println("[5] Find wav file in dictionary");


			switch (in.nextInt()) {
			case 1:
				runAllTests();
				break;
			 case 2:
				 recordAndAddToDictionary(dict, in);
				 break;
			 case 3:
				 recordAndFindInDictionary(dict);
				 break;
			 case 4:
				 System.out.println("Enter apsolute path to word directory.");
				 addWordToDictionary(dict, in.next());
				 break;
			 case 5:
				 System.out.println("Enter absolute path to wav file of word.");
				 findWordInDictionary(dict, in.next());
				 break;
			}
		}

	}

	private static void findWordInDictionary(HMMDictionary dictionary, String wavFile) {
		String name = dictionary.findWord(Word.loadSingle(wavFile));
		System.out.println("Succesfully found word: " + name);
	}

	private static void addWordToDictionary(HMMDictionary dictionary, String wordDirectory) {
		dictionary.addWord(new File(wordDirectory));
		System.out.println("Succesfully added word to dictionary.");
	}

	private static void runAllTests() {

		final HMMDictionary dictionary = new HMMDictionary();
		dictionary.initFromDir("d4-tests/recnik/");

		int testsRun = 0;
		int testsCorrect = 0;
		File testLocation = new File("d4-tests/tests");
		for (final File sample : testLocation.listFiles()) {
			testsRun++;

			final Word word = Word.loadSingle(sample.getAbsolutePath());
			final String bestWord = dictionary.findWord(word);

			if (word.getName().contains(bestWord)) {
				testsCorrect++;
			}

			System.out.println("Found best word for " + word + " is: " + bestWord);
		}

		System.out.println("Test results: HMM(" + testsCorrect + "/" + testsRun + ")");

	}

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
	
	private static void recordAndAddToDictionary(final HMMDictionary dictionary, Scanner in) {
		System.out.println("Name of word (unique), please?");
		String name = in.next();
		
		System.out.println("Enter number of samples to record:");
		int numberOfSamples = in.nextInt();
		File wordDirectory = new File("mainDictionary/" + name + "/");
		wordDirectory.mkdir();
		
		for (int i = 0; i < numberOfSamples; i++) {
			String filename = wordDirectory.getAbsolutePath() + "/" + i + ".wav";
			JavaSoundRecorderCLI.recordSample(filename);
			System.out.println("Succesfully added to dictionary.");
		}
		
		dictionary.addWord(wordDirectory);
		
	}
	
	private static void recordAndFindInDictionary(final HMMDictionary dictionary) {
		final String name = new Random().nextInt() + ".wav";
		JavaSoundRecorderCLI.recordSample(name);
		String foundWord = dictionary.findWord(Word.loadSingle(name));
		System.out.println("Succesfully found word: " + foundWord);
	}
}
