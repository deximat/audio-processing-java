package com.codlex.audio.hmm.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.print.attribute.IntegerSyntax;

import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.stat.correlation.Covariance;

import com.codlex.audio.hmm.Sequence;

public class KMeans {
//
//	private final List<State> states;
//
//	public KMeans(int numberOfStates, List<Sequence> words) {
//		List<State> states = fixedStates(numberOfStates, words);
//		double oldDistances = calculateDistanceSum(states, words);
//
//		while (true) {
////			Map<State, List<List<Double>>> vectorsPerState = new HashMap<>();
////			for (Sequence word : words) {
////				for (List<Double> vector : word.getVectors()) {
////					State closestState = findState(states, vector);
////					List<List<Double>> stateVectors = vectorsPerState.get(closestState);
////					if (stateVectors == null) {
////						stateVectors = new ArrayList<>();
////						vectorsPerState.put(closestState, stateVectors);
////					}
////					stateVectors.add(vector);
////				}
////			}
//			
//			// only neighbouring states
//			Map<State, List<List<Double>>> vectorsPerState = new HashMap<>();
//			for (int i = 0; i < this.states.size(); i++) {
//				
//			}
//			for (Sequence word : words) {
//				for (List<Double> vector : word.getVectors()) {
//					State closestState = findState(states, vector);
//					List<List<Double>> stateVectors = vectorsPerState.get(closestState);
//					if (stateVectors == null) {
//						stateVectors = new ArrayList<>();
//						vectorsPerState.put(closestState, stateVectors);
//					}
//					stateVectors.add(vector);
//				}
//			}
//			
//
//			states = recalculateStates(vectorsPerState);
//
//			final double epsilon = 0.000001;
//			double newDistances = calculateDistanceSum(states, words);
//			if (Math.abs(oldDistances - newDistances) < epsilon) {
//				break;
//			} else {
//				oldDistances = newDistances;
//			}
//		}
//		
//		// make sure states are in order
//		Collections.sort(states, new Comparator<State>() {
//			@Override
//			public int compare(State state1, State state2) {
//				return Integer.compare(state1.getId(), state2.getId());
//			}
//		});
//		
//		this.states = states;
//	}
//
//	private List<State> recalculateStates(final Map<State, List<List<Double>>> vectorsPerState) {
//		final List<State> states = new ArrayList<>();
//		System.out.println("Recalculate.");
//		for (Entry<State, List<List<Double>>> stateAndVectors : vectorsPerState.entrySet()) {
//			int stateId = stateAndVectors.getKey().getId();
//			List<List<Double>> vectors = stateAndVectors.getValue();
//			State state = new State(stateId, vectors);
//			states.add(state);
//			System.out.println("Dynamic: " + state);
//		}
//		return states;
//	}
//
//	private double calculateDistanceSum(List<State> states, List<Sequence> words) {
//		double distance = 0;
//		for (Sequence word : words) {
//			for (List<Double> vector : word.getVectors()) {
//				State vectorState = findState(states, vector);
//				distance += Vectors.distance(vectorState.getMeanVector(), vector);
//			}
//		}
//		return distance;
//	}
//
//	public static State findState(List<State> states, List<Double> vector) {
//		State bestState = states.get(0);
//		double bestProbability = bestState.calculateEmisionProbabilityFor(vector);
//
//		for (State state : states) {
//			double potentialProbability = state.calculateEmisionProbabilityFor(vector);
//			if (potentialProbability > bestProbability) {
//				bestProbability = potentialProbability;
//				bestState = state;
//			}
//		}
//
//		return bestState;
//	}
//
//	private List<State> fixedStates(int numberOfStates, List<Sequence> words) {
//		List<State> states = new ArrayList<>();
//		for (int i = 0; i < numberOfStates; i++) {
//			List<List<Double>> vectors = getVectors(words, i, numberOfStates);
//			states.add(new State(i, vectors));
//			System.out.println("Fixed:" + states.get(i));
//		}
//		return states;
//	}
//
//	private List<List<Double>> getVectors(List<Sequence> words, int pack, int numberOfStates) {
//		final List<List<Double>> vectors = new ArrayList<>();
//		
//		for (Sequence word : words) {
//			List<List<Double>> wordVectors = word.getVectors();
//			int wordLength = wordVectors.size();
//			int packSize = wordLength / numberOfStates;
//			int chunkStart = pack * packSize;
//			boolean isLastChunk = pack + 1 == numberOfStates;
//			int chunkEnd = isLastChunk ? wordVectors.size() : (pack + 1) * packSize;
//			vectors.addAll(wordVectors.subList(chunkStart, chunkEnd));
//		}
//
//		return vectors;
//	}
//	
//	public List<State> getStates() {
//		return this.states;
//	}
}
