package com.codlex.audio.projekat;

import java.util.List;

public class DTW {

	private enum Ancestor {
		Horizontal(0, -1), Diagonal(-1, -1), Diagonal2(-2, -1);
		
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
		double[][] distances = new double[base.size()][sample.size()];
		
		for (int i = 0; i < base.size(); i++) {
			int j = 0;
			distances[i][j] = Util.Euclid.distance(base.get(i), sample.get(j));
			
		}
		
		for (int j = 1; j < sample.size(); j++) {
			for (int i = 0; i < base.size(); i++) {
				distances[i][j] = Util.Euclid.distance(base.get(i), sample.get(j));
				
				double minAncestor = Double.MAX_VALUE;
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
		
		this.distance = distances[base.size() - 1][sample.size() - 1];
	}

	public double getDistance() {
		return this.distance;
	}
}
