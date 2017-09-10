package com.codlex.audio.projekat;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;

public class Util {

	public static class Matrix {
		public static RealMatrix inverse(RealMatrix matrix) {
			return new LUDecomposition(matrix).getSolver().getInverse();
		}
	}
	
	public static class Array {
		
		public static List<Double> toList(double[] array) {
			List<Double> list = new ArrayList<>();
			for (double value : array) {
				list.add(value);
			}
			return list;
		}

		public static short[] converToShort(List<Double> samples) {
			short[] array = new short[samples.size()];
			for (int i = 0; i < samples.size(); i++) {
				array[i] = (short) (samples.get(i).doubleValue() * 10000);
			}
			
			return array;
		}

		public static float[] fromList(List<Double> samples) {
			float[] array = new float[samples.size()];
			for (int i = 0; i < samples.size(); i++) {
				array[i] = samples.get(i).floatValue();
			}
			return array;
		}

		public static List<Double> toList(float[] array) {
			List<Double> list = new ArrayList<>();
			for (double value : array) {
				list.add(value);
			}
			return list;
		}
		
	}
	
	public static class Euclid {
		
		public static double distance (List<Double> vector1, List<Double> vector2) {
			double distance = 0; 
			for (int i = 0; i < vector1.size(); i++) {
				double diff = vector1.get(i) - vector2.get(i);
				distance += diff * diff;
			}
			return Math.sqrt(distance);
		}
	}

	public static void printList(List<Boolean> enoughEnergy) {
		boolean last = false;
		int start = 0;
		for (int i = 0; i < enoughEnergy.size(); i++) {
			if (enoughEnergy.get(i) != last) {
				System.out.println(last + " from " + start + " to "  + (i - 1));
				last = enoughEnergy.get(i);
				start = i;
			}
		}
		System.out.println(last + " from " + start + " to "  + (enoughEnergy.size() - 1));		
	}
	
	
}
