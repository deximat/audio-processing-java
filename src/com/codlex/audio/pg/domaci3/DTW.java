package com.codlex.audio.pg.domaci3;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.codlex.audio.hmm.model.Vectors;
import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;

public class DTW {

	private enum Ancestor {
		Horizontal(0, -1), 
		Diagonal(-1, -1), Diagonal2(-2, -1);
		
		private int iMove;
		private int jMove;

		Ancestor(int iMove, int jMove) {
			this.iMove = iMove;
			this.jMove = jMove;
		}
		
		public boolean isValid(int i, int j) {
			return i + iMove >= 0 && j + jMove >= 0;
		}
		
		public int applyI(int i) {
			return this.iMove + i;
		}
		
		public int applyJ(int j) {
			return this.jMove + j;
		}
		
	}
	
	private final double distance;

	public DTW(List<List<Double>> base, List<List<Double>> sample) {
		
//		System.out.println("base");
//		for (List<Double> vector : base) {
//			System.out.print(Util.Euclid.distanceFromZero(vector) + ", ");
//		}
//		System.out.println();
//		
//		System.out.println("sample:");
//		for (List<Double> vector : sample) {
//			System.out.print(Util.Euclid.distanceFromZero(vector) + ", ");
//		}
//		System.out.println();

		double[][] distances = new double[base.size()][sample.size()];
		
		for (int i = 0; i < distances.length; i++) {
			Arrays.fill(distances[i], Double.POSITIVE_INFINITY);
		}
		
		distances[0][0] = Util.Euclid.distance(base.get(0), sample.get(0));
		for (int j = 1; j < sample.size(); j++) {
			for (int i = 0; i < base.size(); i++) {
				distances[i][j] = Util.Euclid.distance(base.get(i), sample.get(j));
				
				double minAncestor = Double.POSITIVE_INFINITY;
				for (Ancestor ancestor : Ancestor.values()) {
					if (ancestor.isValid(i, j)) {
						double ancestorValue = distances[ancestor.applyI(i)][ancestor.applyJ(j)];
						if (ancestorValue < minAncestor) {
							minAncestor = ancestorValue;
						}
					}
				}
				
				distances[i][j] += minAncestor;
			}
		}
		
		// toCSV(distances);
		this.distance = distances[base.size() - 1][sample.size() - 1];
		// System.out.println("Distance: " + this.distance);
	}
	
	private static final AtomicInteger idGen = new AtomicInteger();
	
	private void toCSV(double[][] matrix) {
		
		System.out.println("CSV GENERATING id: " + (idGen.get() + 1));
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				builder.append(matrix[i][j]);
				if (j + 1 != matrix[i].length) {
					builder.append(",");
				} else {
					builder.append("\n");
				}
			}
		}
		try {
			Files.write(builder.toString(), new File("csv/" + idGen.incrementAndGet() + ".csv"), Charsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public double getDistance() {
		return this.distance;
	}
	
	
	public static void main(String[] args) {
		int vectorSize = 5;
		List<List<Double>> base = Vectors.generateMatrixWithVectors(vectorSize, ImmutableList.of(0, 1, 1, 1, 1, 1, 2, 3, 4));		
		List<List<Double>> vect1 = Vectors.generateMatrixWithVectors(vectorSize,ImmutableList.of(0, 1, 1, 2, 2, 2, 2, 2, 2, 3, 4));
		List<List<Double>> vect2 = Vectors.generateMatrixWithVectors(vectorSize, ImmutableList.of(0, 1, 4, 4, 5));
		
		System.out.println("Distance(vect1): " + new DTW(base, vect1).getDistance());
		System.out.println("Distance(vect2): " + new DTW(base, vect2).getDistance());		

	}
}
