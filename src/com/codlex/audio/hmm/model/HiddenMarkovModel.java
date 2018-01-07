package com.codlex.audio.hmm.model;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.codlex.audio.enpointing.Word;
import com.codlex.audio.hmm.Sequence;
import com.codlex.audio.pg.domaci3.Dictionary;
import com.google.common.io.Files;

public class HiddenMarkovModel {

	public class FindResults {
		private final CostAndState[][] results;
		private final CostAndState cost;

		public FindResults(final CostAndState[][] results) {
			this.results = results;
			this.cost = results[results.length - 1][results[0].length - 1];
		}

		public CostAndState[][] getResults() {
			return this.results;
		}

		public void print(String pattern, String sample) {
			final StringBuilder builder = new StringBuilder();
			
			for (int i = 0; i < this.results.length; i++) {
				for (int j = 0; j < this.results[i].length; j++) {
					builder.append(this.results[i][j].getCost());
					builder.append(", ");
				}
				builder.append("\n");
			}
			
			try {
				Files.write(builder.toString(), new File("trelis/" + pattern + "_" + sample + "_ " + ID.incrementAndGet() +".csv"), Charset.defaultCharset());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static AtomicInteger ID = new AtomicInteger();

	private double[] stateProbability;

	public class CostAndState {
		double cost;
		int state;

		public CostAndState(double cost, int state) {
			this.cost = cost;
			this.state = state;
		}

		public double getCost() {
			return this.cost;
		}
	}

	private int numberOfStates;
	private double[][] transitiveProbability;
	private List<State> states;

	public HiddenMarkovModel(int numberOfStates) {
		this.numberOfStates = numberOfStates;
		this.transitiveProbability = new double[numberOfStates][numberOfStates];
		this.stateProbability = generateOnlyFirstStateAllowed();

		// if no training present
		initializeWithRandom();
	}

	private static double minProbability = 0.0000001;

	private void initializeWithRandom() {
		for (int i = 0; i < this.transitiveProbability.length; i++) {
			for (int j = 0; j < this.transitiveProbability[i].length; j++) {
				this.transitiveProbability[i][j] = minProbability;
			}
		}
		
//		for (int i = 0; i < this.numberOfStates - 1; i++) {
//			this.transitiveProbability[i][i] = Math.random();
//			this.transitiveProbability[i][i+1] = 1 - this.transitiveProbability[i][i];
//		}
	}

	private double[] generateOnlyFirstStateAllowed() {
		double[] stateProbabilities = new double[this.numberOfStates];
		stateProbabilities[0] = 1;
		for (int i = 1; i < this.numberOfStates; i++) {
			stateProbabilities[i] = minProbability;
		}
		return stateProbabilities;
	}

	public FindResults find(Sequence sequence) {
		int sequenceLength = sequence.getVectors().size();
		CostAndState[][] results = new CostAndState[this.numberOfStates][sequenceLength];

		// System.out.println("find results:");
		// init first column
		for (int i = 0; i < this.numberOfStates; i++) {
			results[i][0] = new CostAndState(-Math.log(this.stateProbability[i])
					+ -Math.log(calculateEmmisionProbability(i, sequence.getVectors().get(0))), 0);
//			System.out.println(sequence.getVectors().get(0));
//			System.out.println(results[i][0].cost);
		}
//		System.out.println();

		// calculate rest of matrix
		for (int t = 1; t < sequenceLength; t++) {
			for (int i = 0; i < this.numberOfStates; i++) {
				double minCost = findMinCost(results, t - 1, i);
				// add emission probability
				double probabilityCost = -Math.log(calculateEmmisionProbability(i, sequence.getVectors().get(t)));
				
				results[i][t] = new CostAndState(minCost + probabilityCost, i);
			}
		}

		return new FindResults(results);
	}

	private double calculateEmmisionProbability(int state, final List<Double> vector) {
		return this.states.get(state).calculateEmisionProbabilityFor(vector);
	}


	private double findMinCost(CostAndState[][] results, int t, int toState) {
		double minCost = Double.POSITIVE_INFINITY;

		for (int i = 0; i < this.numberOfStates; i++) {
			if (this.transitiveProbability[i][toState] > minProbability) {
				double potentialMin = results[i][t].getCost() + -Math.log(this.transitiveProbability[i][toState]);
				if (potentialMin < minCost) {
					minCost = potentialMin;
				}
			}
		}

		return minCost;
	}

	public void train(List<Sequence> sequences) {
		
		this.states = new KMeans(this.numberOfStates, sequences).getStates();
		
		// printStates(sequences);
		for (int i = 0; i < this.numberOfStates - 1; i++) {
			double totalInState = this.states.get(i).getVectors().size();

			// stay in state i
			this.transitiveProbability[i][i] = countInStateAndStaying(i, sequences) / totalInState;

			// go to next state, we assume it is everything else
			this.transitiveProbability[i][i + 1] = 1 - this.transitiveProbability[i][i];
			
		}
		
		 // printTransitiveProbability();
	}

	private void printStates(List<Sequence> sequences) {
		System.out.println("##### STATES #####");
		
		
		for (Sequence sequence : sequences) {
			System.out.print(sequence.getName() + ", ");
			
			for (State state : sequence.getVectors().stream().map(vector -> KMeans.findState(this.states, vector)).collect(Collectors.toList())) {
				System.out.print(state.getId() + ", ");
			}
			
			System.out.println();
		}
		
		for (int i = 0; i < this.states.size(); i++) {
			System.out.print(this.states.get(i) + ", ");
		} 
	}

	private void printTransitiveProbability() {
		System.out.println("#### Transitive probability ####");
		// debug method
		for (int i = 0; i < this.transitiveProbability.length; i++) {
			for (int j = 0; j < this.transitiveProbability[i].length; j++) {
				System.out.print(this.transitiveProbability[i][j] + " \t\t");
			}
			System.out.println();
		}
		
	}

	private double countInStateAndStaying(final int id, List<Sequence> sequences) {
		int count = 0;
		for (Sequence sequence : sequences) {
			final List<Integer> states = sequence.getVectors().stream()
					.map((vector) -> KMeans.findState(this.states, vector).getId())
					.collect(Collectors.toList());
			
			for (int i = 0; i < states.size() - 1; i++) {
				if (states.get(i) == id && states.get(i).equals(states.get(i + 1))){
					count++;
				}
			}
		}
		
		return count;
	}
	
	public static void main(String[] args) {
		Dictionary dict = new Dictionary();
		
		final Map<String, HiddenMarkovModel> dictionary = new HashMap<>();
		final File[] files = new File("d4-tests/recnik/").listFiles();
		for (File file : files) {
	
			String name = file.getName();
			if (name.startsWith(".")) {
				continue;
			}
			final List<Sequence> sequences = new ArrayList<>();
			final File location = new File("d4-tests/recnik/" + name);
			for (final File sample : location.listFiles()) {
				final Word word = Word.loadSingle(sample.getAbsolutePath());
				dict.addWord(word); // TODO: REMOVE
				sequences.add(new Sequence(name, word));
			}
			
			HiddenMarkovModel model = new HiddenMarkovModel(10);
			model.train(sequences);
			dictionary.put(name, model);	
		}
		
		
		int testsRun = 0;
		int testsCorrect = 0;
		int dtwTestsCorrect = 0;
		File testLocation = new File("d4-tests/tests");
		for (final File sample : testLocation.listFiles()) {
			
//			if (!sample.getName().contains("jedan-z2")) {
//				continue;
//			}
			
			testsRun++;
			double bestScore = Double.MAX_VALUE;
			String bestWord = "NO_WORD";
			
			final Word word = Word.loadSingle(sample.getAbsolutePath());
			for (Entry<String, HiddenMarkovModel> entry : dictionary.entrySet()) {
				FindResults result = entry.getValue().find(new Sequence(sample.getName(), word));
				result.print(entry.getKey(), sample.getName());
				
				double score = result.cost.getCost();
				if (score < bestScore) {
					bestScore = score;
					bestWord = entry.getKey();
				}
				
				System.out.println(entry.getKey() + " cost: " + score);
			}
			
			Word dtw = dict.findWord(word);
			if (word.getName().contains(getSimpleName(dtw.getName()))) {
				dtwTestsCorrect++;
			}
			
			if (word.getName().contains(bestWord)) {
				testsCorrect++;
			}
			
			System.out.println("Found best word for " + word + " is: " + bestWord + " with score: " + bestScore);
			System.out.println("Word found by dtw: " + dtw);
		}
		
		System.out.println("Test results: HMM(" + testsCorrect + "/"+ testsRun + "), DTW(" + dtwTestsCorrect +  "/" + testsRun +")" );
		
	}
	
	private static String getSimpleName(final String name) {
		String[] directories = name.split("/");
		String wordName = directories[directories.length - 2];
		return wordName;
	}
}
