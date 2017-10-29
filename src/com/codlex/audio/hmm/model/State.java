package com.codlex.audio.hmm.model;

import java.util.List;

import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.stat.correlation.Covariance;

public class State {

	private final List<Double> meanVector;
	// private final MultivariateNormalDistribution distribution;
	private final int id;

	public State(int id, List<List<Double>> vectors) {
		this.id = id;
		this.meanVector = Vectors.calculateAverage(vectors);
//		this.distribution = new MultivariateNormalDistribution(Vectors.toArray(meanVector),
//				new Covariance(Vectors.toMatrix(vectors)).getCovarianceMatrix().getData());
	}

	public double calculateEmisionProbabilityFor(final List<Double> vector) {
		return 1 / Vectors.distance(this.meanVector, vector);
		// return 0.0;
		// return this.distribution.density(Vectors.toArray(vector));
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

}
