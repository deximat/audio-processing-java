package com.codlex.audio.hmm.model;

import java.util.List;

import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.stat.correlation.Covariance;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import lombok.Getter;

public class State {

	private List<Double> meanVector;
	private final int id;

	@Getter
	private final List<List<Double>> vectors;
	private MultivariateNormalDistribution distribution;

	public State(int id, List<List<Double>> vectors) {
		this.id = id;
		this.vectors = vectors;
		this.meanVector = Vectors.calculateAverage(vectors);

		double[][] covarianceMatrix = new Covariance(Vectors.toMatrix(vectors)).getCovarianceMatrix().getData();
		this.distribution = new MultivariateNormalDistribution(Vectors.toArray(this.meanVector), covarianceMatrix);

	}

	public double calculateEmisionProbabilityFor(final List<Double> vector) {
		
		// MY VARIANT:
		double bestDistance = Double.MAX_VALUE;

		for (List<Double> vector1 : getVectors()) {
			double distance = Vectors.distance(vector, vector1);
			if (distance < bestDistance) {
				bestDistance = distance;
			}
		}

		return 1 / bestDistance;

//		// GAUS:
//		 double density = this.distribution.density(Vectors.toArray(vector));
//		 return density;
	}

	public List<Double> getMeanVector() {
		return this.meanVector;
	}

	public int getId() {
		return this.id;
	}

	@Override
	public String toString() {
		return "State(id=" + this.id + ", meanVector=" + this.meanVector + ")";
	}

	public double calculateDistanceFromMean() {
		double distance = 0;
		for (List<Double> vector : getVectors()) {
			distance += Vectors.distance(getMeanVector(), vector);
		}
		return distance;
	}

}
