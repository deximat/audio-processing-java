package com.codlex.audio.pg.domaci3;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import com.codlex.audio.generator.Window;

public class LPC {

	private final List<List<Double>> coeficients;

	private final int coeficientCount;

	public LPC(final int coeficientCount, final List<Double> signal, final double sampleDuration) {
		this.coeficientCount = coeficientCount;
		this.coeficients = new ArrayList<>();

		for (Window window : Window.generateOverlapping(signal, AudioConstants.getLpcWindowSize(sampleDuration),
				AudioConstants.getLpcWindowShift(sampleDuration))) {
			this.coeficients.add(calculateCoeficients(window));
		}

	}

	private List<Double> calculateCoeficients(final Window window) {
		List<Double> samples = window.getSamples();
		samples = AudioConstants.lpcWindowFunction.apply(samples);

		List<Double> r = calculateRs(samples);

		// bigMatrix x coef = rMatrix
		RealMatrix bigMatrix = createBigMatrix(r);
		RealMatrix rMatrix = createRMatrix(r);

		// coef = bigMatrix-1 x rMatrix
		RealMatrix bigInverseMatrix = Util.Matrix.inverse(bigMatrix);
		RealMatrix coef = bigInverseMatrix.multiply(rMatrix);

		// return Util.Array.toList(LPC2.getCoefficients(12,
		// Util.Array.converToShort(window.getSamples())));
		return Util.Array.toList(coef.getColumn(0));

		// return
		// Util.Array.toList(lpc_from_data(Util.Array.fromList(window.getSamples()),window.getSamples().size(),
		// 12));
	}

	static float[] lpc_from_data(float[] data, int n, int m) {
		float[] lpc = new float[m];
		float[] aut = new float[m + 1];
		float error;
		int i, j;

		// autocorrelation, p+1 lag coefficients

		j = m + 1;
		while (j-- != 0) {
			float d = 0;
			for (i = j; i < n; i++)
				d += data[i] * data[i - j];
			aut[j] = d;
		}

		// Generate lpc coefficients from autocorr values

		error = aut[0];
		/*
		 * if(error==0){ for(int k=0; k<m; k++) lpc[k]=0.0f; return 0; }
		 */

		for (i = 0; i < m; i++) {
			float r = -aut[i + 1];

			if (error == 0) {
				for (int k = 0; k < m; k++)
					lpc[k] = 0.0f;
				return null;
			}

			// Sum up this iteration's reflection coefficient; note that in
			// Vorbis we don't save it. If anyone wants to recycle this code
			// and needs reflection coefficients, save the results of 'r' from
			// each iteration.

			for (j = 0; j < i; j++)
				r -= lpc[j] * aut[i - j];
			r /= error;

			// Update LPC coefficients and total error

			lpc[i] = r;
			for (j = 0; j < i / 2; j++) {
				float tmp = lpc[j];
				lpc[j] += r * lpc[i - 1 - j];
				lpc[i - 1 - j] += r * tmp;
			}
			if (i % 2 != 0)
				lpc[j] += lpc[j] * r;

			error *= 1.0 - r * r;
		}

		// we need the error value to know how big an impulse to hit the
		// filter with later

		return lpc;
	}

	private RealMatrix createRMatrix(List<Double> r) {
		RealMatrix matrix = new Array2DRowRealMatrix(this.coeficientCount, 1);
		for (int i = 0; i < this.coeficientCount; i++) {
			matrix.setEntry(i, 0, r.get(i + 1));
		}
		return matrix;
	}

	private RealMatrix createBigMatrix(List<Double> r) {
		RealMatrix matrix = new Array2DRowRealMatrix(this.coeficientCount, this.coeficientCount);

		for (int i = 0; i < this.coeficientCount; i++) {
			for (int j = 0; j < this.coeficientCount; j++) {
				int rIndex = Math.abs(i - j);
				matrix.setEntry(i, j, r.get(rIndex));
			}
		}

		return matrix;
	}

	private List<Double> calculateRs(List<Double> samples) {
		List<Double> r = new ArrayList<>();
		for (int i = 0; i <= this.coeficientCount; i++) {
			r.add(calculateR(samples, i));
		}
		return r;
	}

	private double calculateR(List<Double> samples, int k) {
		double value = 0;
		for (int i = 0; i < samples.size(); i++) {
			if (i < k) {
				// we need to take a look at previous window
				value += samples.get(i) * samples.get(samples.size() + i - k);
			} else {
				// we are in same window
				value += samples.get(i) * samples.get(i - k);
			}
		}
		return value;
	}

	public List<List<Double>> getCoeficients() {
		return this.coeficients;
	}

}
