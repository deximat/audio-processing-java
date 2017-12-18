package com.codlex.audio.pg.domaci3;

import java.util.ArrayList;
import java.util.List;

import com.codlex.audio.enpointing.Word;

public class Dictionary {
	
	final List<Word> words = new ArrayList<>();
	
	public Word findWord(final Word word) {
		Word bestMatch = null;
		double bestDistance = AudioConstants.dtwTreshold;
		
		for (Word dictionaryWord : this.words) {
			System.out.println("Checking with " + dictionaryWord);
			double distance = word.distanceTo(dictionaryWord);
//			if (distance == 0) {
//				return null;
//			}
			// System.out.println(word + " is distanced from " + dictionaryWord + " : " + distance);
			if (distance < bestDistance 
					//&& distance != 0
					) {
				bestMatch = dictionaryWord;
				bestDistance = distance;
			}
		}

		if (bestMatch != null) {
			System.out.println("DST: " + bestDistance);
			// System.out.println("######## Best match: " + word + " is " + bestMatch + " with score: " + bestDistance);
		}
		
		return bestMatch;
	}

	public void addWord(Word word) {
		this.words.add(word);
	}

}
