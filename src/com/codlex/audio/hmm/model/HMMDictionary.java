package com.codlex.audio.hmm.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.codlex.audio.enpointing.Word;
import com.codlex.audio.hmm.Sequence;
import com.codlex.audio.hmm.model.HiddenMarkovModel.FindResults;

public class HMMDictionary {
	private final Map<String, HiddenMarkovModel> dictionary = new HashMap<>();
	
	public void initFromDir(final String dicitonaryDirectory) {
		final File[] files = new File(dicitonaryDirectory).listFiles();
		for (File file : files) {
			String name = file.getName();
			
			// skip hidden files
			if (name.startsWith(".")) {
				continue;
			}			
			addWord(file);
		}
	}
	
	
	public String findWord(final Word word) {
		double bestScore = Double.MAX_VALUE;
		String bestWord = "NO_WORD";
		
		for (Entry<String, HiddenMarkovModel> entry : this.dictionary.entrySet()) {
			FindResults result = entry.getValue().find(new Sequence(word.getSimpleName(), word));
			result.print(entry.getKey(), word.getSimpleName());
			
			double score = result.cost().getCost();
			if (score < bestScore) {
				bestScore = score;
				bestWord = entry.getKey();
			}
			
			System.out.println(entry.getKey() + " cost: " + score);
		}
		
		return bestWord;
	}
	
	public void addWord(final File file) {
		final List<Sequence> sequences = new ArrayList<>();

		for (final File sample : file.listFiles()) {
			final Word word = Word.loadSingle(sample.getAbsolutePath());
			sequences.add(new Sequence(file.getName(), word));
		}
		
		HiddenMarkovModel model = new HiddenMarkovModel(10);
		model.train(sequences);
		
		this.dictionary.put(file.getName(), model);
	}

}
