package com.codlex.audio.hmm.model;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import com.codlex.audio.hmm.Sequence;
import com.codlex.audio.hmm.SimilarVectorsGenerator;

public class HiddenMarkovModel {
//
//	public class FindResults {
//		private final ScoreAndState[][] results;
//		private final int[] stateSequence;
//		private final ScoreAndState score;
//
//		public FindResults(final ScoreAndState[][] results, final int[] stateSequence) {
//			this.results = results;
//			this.stateSequence = stateSequence;
//			this.score = results[stateSequence[stateSequence.length - 1]][stateSequence.length - 1];
//		}
//
//		public ScoreAndState[][] getResults() {
//			return this.results;
//		}
//
//		public int[] getStateSequence() {
//			return this.stateSequence;
//		}
//	}
//
//	private double[] stateProbability;
//
//	public class ScoreAndState {
//		double score;
//		int state;
//
//		public ScoreAndState(double score, int state) {
//			this.score = score;
//			this.state = state;
//		}
//
//		public double getScore() {
//			return this.score;
//		}
//	}
//
//	private int numberOfStates;
//	private double[][] transitiveProbability;
//	private List<State> states;
//
//	public HiddenMarkovModel(int numberOfStates) {
//		this.numberOfStates = numberOfStates;
//		this.transitiveProbability = new double[numberOfStates][numberOfStates];
//		this.stateProbability = generateOnlyFirstStateAllowed();
//
//		// if no training present
//		initializeWithRandom();
//	}
//
//	private void initializeWithRandom() {
//		double minProbability = 0.0000001;
//		for (int i = 0; i < this.transitiveProbability.length; i++) {
//			for (int j = 0; j < this.transitiveProbability[i].length; j++) {
//				this.transitiveProbability[i][j] = minProbability;
//			}
//		}
//		
//		for (int i = 0; i < this.numberOfStates - 1; i++) {
//			this.transitiveProbability[i][i] = Math.random();
//			this.transitiveProbability[i][i+1] = 1 - this.transitiveProbability[i][i];
//		}
//	}
//
//	private double[] generateOnlyFirstStateAllowed() {
//		double[] stateProbabilities = new double[this.numberOfStates];
//		stateProbabilities[0] = 1;
//		double minProbability = 0.000000001;
//		for (int i = 1; i < this.numberOfStates; i++) {
//			stateProbabilities[i] = minProbability;
//		}
//		return stateProbabilities;
//	}
//
//	public FindResults find(Sequence sequence) {
//		int sequenceLength = sequence.getVectors().size();
//		ScoreAndState[][] results = new ScoreAndState[this.numberOfStates][sequenceLength];
//
//		// init first column
//		for (int i = 0; i < this.numberOfStates; i++) {
//			results[i][0] = new ScoreAndState(-Math.log(this.stateProbability[i])
//					+ -Math.log(calculateEmmisionProbability(i, sequence.getVectors().get(0))), 0);
//		}
//
//		// calculate rest of matrix
//		for (int t = 1; t < sequenceLength; t++) {
//			for (int i = 0; i < this.numberOfStates; i++) {
//				ScoreAndState result = findMaxScore(results, t - 1, i);
//				// add emission probability
//				result.score += -Math.log(calculateEmmisionProbability(i, sequence.getVectors().get(t)));
//				results[i][t] = result;
//			}
//		}
//
//		int[] stateSequence = calculateStateSequence(sequenceLength, results);
//
//		System.out.println(stateSequence);
//		return new FindResults(results, stateSequence);
//	}
//
//	private double calculateEmmisionProbability(int state, final List<Double> vector) {
//		
//		if (this.states == null) {
//			System.out.println("Model is not trained, using random values");
//			return Math.random();
//		}
//		
//		return this.states.get(state).calculateEmisionProbabilityFor(vector);
//	}
//
//	private int[] calculateStateSequence(int sequenceLength, ScoreAndState[][] results) {
//		int[] stateSequence = new int[sequenceLength];
//
//		// init first
//		ScoreAndState bestResult = results[0][sequenceLength - 1];
//		int bestState = 0;
//		for (int i = 1; i < this.numberOfStates; i++) {
//			if (results[i][sequenceLength - 1].score > bestResult.score) {
//				bestResult = results[i][sequenceLength - 1];
//				bestState = i;
//			}
//		}
//		System.out.println("Best result: " + bestResult.score + " state: " + bestResult.state);
//
//		stateSequence[sequenceLength - 1] = bestState;
//		for (int t = sequenceLength - 2; t >= 0; t--) {
//			stateSequence[t] = results[stateSequence[t + 1]][t + 1].state;
//		}
//
//		for (int i = 0; i < results.length; i++) {
//			for (int j = 0; j < results[i].length; j++) {
//				System.out.print(results[i][j].state + " ");
//			}
//			System.out.println();
//		}
//		return stateSequence;
//	}
//
//	private ScoreAndState findMaxScore(ScoreAndState[][] results, int t, int toState) {
//		double initialScore = results[0][t].score + -Math.log(this.transitiveProbability[0][toState]);
//		ScoreAndState bestResult = new ScoreAndState(initialScore, 0);
//
//		for (int i = 1; i < this.numberOfStates; i++) {
//			double potentialMax = results[i][t].score + -Math.log(this.transitiveProbability[i][toState]);
//			if (potentialMax > bestResult.score) {
//				bestResult.score = potentialMax;
//				bestResult.state = i;
//			}
//		}
//
//		return bestResult;
//	}
//
//	public void train(List<Sequence> sequences) {
//		this.states = new KMeans(this.numberOfStates, sequences).getStates();
//		printStates();
//		for (int i = 0; i < this.numberOfStates - 1; i++) {
//			double totalInState = calculateInState(i, sequences);
//
//			// stay in state i
//			this.transitiveProbability[i][i] = countInStateAndStaying(i, sequences) / totalInState;
//
//			// go to next state, we assume it is everything else
//			this.transitiveProbability[i][i + 1] = 1 - this.transitiveProbability[i][i];
//		}
//		
//		printTransitiveProbability();
//	}
//
//	private void printStates() {
//		System.out.println("##### STATES #####");
//		for (int i = 0; i < this.states.size(); i++) {
//			System.out.println(this.states.get(i));
//		} 
//	}
//
//	private void printTransitiveProbability() {
//		System.out.println("#### Transitive probability ####");
//		// debug method
//		for (int i = 0; i < this.transitiveProbability.length; i++) {
//			for (int j = 0; j < this.transitiveProbability[i].length; j++) {
//				System.out.print(this.transitiveProbability[i][j] + " \t\t");
//			}
//			System.out.println();
//		}
//		
//	}
//
//	private double countInStateAndStaying(final int id, List<Sequence> sequences) {
//		int count = 0;
//		for (Sequence sequence : sequences) {
//			List<Integer> states = sequence.getVectors().stream()
//					.map((vector) -> KMeans.findState(this.states, vector).getId())
//					.collect(Collectors.toList());
//			for (int i = 0; i < states.size() - 1; i++) {
//				if (states.get(i) == states.get(i + 1)) {
//					count++;
//				}
//			}
//		}
//		
//		return count;
//	}
//
//	private double calculateInState(int id, List<Sequence> sequences) {
//		int count = 0;
//		for (Sequence sequence : sequences) {
//			int sequenceCount = (int) sequence.getVectors().stream()
//					.map((vector) -> KMeans.findState(this.states, vector))
//					.filter((state) -> state.getId() == id)
//					.count();
//			count += sequenceCount;
//		}
//		
//		return count;
//	}
//	
//	public static void main(String[] args) {
//		
//		// TEST 
//		HiddenMarkovModel model = new HiddenMarkovModel(3);
//		
//		List<Sequence> sequences = new ArrayList<>();
//		int vectorSize = 10;
//		Sequence sequence = new Sequence("bla", vectorSize);
//		sequence.getVectors().clear();
//		sequence.getVectors().add(dummyVector(0, 10));
//		sequence.getVectors().add(dummyVector(0, 10));
//		sequence.getVectors().add(dummyVector(1, 10));
//		sequence.getVectors().add(dummyVector(1, 10));
//		sequence.getVectors().add(dummyVector(2, 10));
//		sequence.getVectors().add(dummyVector(2, 10));
//		sequence.getVectors().add(dummyVector(2, 10));
//		sequence.getVectors().add(dummyVector(2, 10));
//		sequence.getVectors().add(dummyVector(2, 10));
//		sequence.getVectors().add(dummyVector(2, 10));
//
//		sequences.add(sequence);
//		
////		Sequence sequence2 = new Sequence("bla", vectorSize);
////		sequence2.getVectors().clear();
////
////		sequence.getVectors().add(dummyVector(0, 10));
////		sequence.getVectors().add(dummyVector(0, 10));
////		sequence.getVectors().add(dummyVector(0, 10));
////		sequence.getVectors().add(dummyVector(1, 10));
////		sequence.getVectors().add(dummyVector(1, 10));
////		sequence.getVectors().add(dummyVector(2, 10));
////		sequence.getVectors().add(dummyVector(2, 10));
////		sequence.getVectors().add(dummyVector(2, 10));
////		sequences.add(sequence2);
////
////		Sequence sequence3 = new Sequence("bla", vectorSize);
////		sequence3.getVectors().clear();
////
////		sequence.getVectors().add(dummyVector(0, 10));
////		sequence.getVectors().add(dummyVector(0, 10));
////		sequence.getVectors().add(dummyVector(1, 10));		
////		sequence.getVectors().add(dummyVector(1, 10));
////		sequence.getVectors().add(dummyVector(1, 10));
////		sequence.getVectors().add(dummyVector(2, 10));
////		sequence.getVectors().add(dummyVector(2, 10));
////		sequences.add(sequence3);
//		
//		
//		model.train(sequences);
//		
//		
//	}
//	
//	static List<Double> dummyVector(int pos, double value) {
//		List<Double> vector = new ArrayList<>();
//		for (int i = 0; i < 10; i++) {
//			if (pos == i) {
//				vector.add(value);
//			} else {
//				vector.add(1.0);
//			}
//		}
//		return vector;
//	}
//	
//	HMM hmm = new HMM(3, 4);


}
