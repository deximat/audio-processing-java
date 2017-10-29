package com.codlex.audio.hmm;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import org.apache.commons.math3.distribution.MultivariateNormalDistribution;

public class SimilarVectorsGenerator {

	public SimilarVectorsGenerator(int vectorSize) {
		this.covariance = generateIdentityMatrix(vectorSize);
	}

	private double[][] covariance;

	private double[][] generateIdentityMatrix(int vectorSize) {
		double[][] identity = new double[vectorSize][vectorSize];
		for (int i = 0; i < vectorSize; i++) {
			for (int j = 0; j < vectorSize; j++) {
				if (i == j) {
					identity[i][j] = 1;
				}
			}
		}
		return identity;
	}

	private List<List<Double>> similarVectors = new ArrayList<>();

	public void generate(final List<Double> vector) {
		this.similarVectors.clear();
		MultivariateNormalDistribution generator = new MultivariateNormalDistribution(
				vector.stream().mapToDouble(x -> x).toArray(), this.covariance);
		int SIMILAR_SIZE = 10;
		for (int i = 0; i < SIMILAR_SIZE; i++) {
			this.similarVectors.add(DoubleStream.of(generator.sample()).boxed().collect(Collectors.toList()));
		}
	}

	public List<List<Double>> getSimilarVectors() {
		return this.similarVectors;
	}

	public double[][] getCovariance() {
		return this.covariance;
	}
}
