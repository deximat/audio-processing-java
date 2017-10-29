package com.codlex.audio.hmm.model;

import java.util.ArrayList;
import java.util.List;

public class Vectors {
	
	public static List<Double> calculateAverage(List<List<Double>> vectors) {
		
		// init with zeros
		List<Double> averageVector = new ArrayList<>();
		for (int i = 0; i < vectors.get(0).size(); i++) {
			averageVector.add(0.0);
		}
		
		for (int i = 0; i < averageVector.size(); i++) {
			// sum all vectors on this coordinate
			for (List<Double> vector : vectors) {
				averageVector.set(i, averageVector.get(i) + vector.get(i));
			}
			
			// calculate average
			averageVector.set(i, averageVector.get(i) / vectors.size());
		}

		return averageVector;
	}

	public static double[] toArray(List<Double> meanVector) {
		double[] vectorArray = new double[meanVector.size()];
		for (int i = 0; i < meanVector.size(); i++) {
			vectorArray[i] = meanVector.get(i);
		}
		return vectorArray;
	}
	
	public static double[][] toMatrix(List<List<Double>> vectors) {
		
		int vectorDimensions = vectors.get(0).size();
		double[][] vectorsArray = new double[vectors.size()][vectorDimensions];
		
		for (int i = 0; i < vectors.size(); i++) {
			for (int j = 0; j < vectorDimensions; j++) {
				vectorsArray[i][j] = vectors.get(i).get(j);
			}
		}
		
		return vectorsArray;
	}

	public static double distance(final List<Double> vector1, final List<Double> vector2) {
		double distance = 0;
		for (int i = 0; i < vector1.size(); i++) {
			distance += Math.sqrt(Math.pow(vector1.get(i) - vector2.get(i), 2));
		}
		return distance;
	}
	
}
