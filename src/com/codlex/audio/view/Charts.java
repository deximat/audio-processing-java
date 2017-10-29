package com.codlex.audio.view;

import java.util.List;

import com.codlex.audio.enpointing.Word;
import com.codlex.audio.transform.Frequency;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class Charts {
	public static final LineChart line(List<Double> signal, int position, double sampleDuration, final List<Word> words,
			Word activeWord) {
		NumberAxis xAxis = activeWord != null
				? new NumberAxis(activeWord.getStart() * sampleDuration, activeWord.getEnd() * sampleDuration, 0.01)
				: new NumberAxis();
		xAxis.setLabel("Time domain " + (!activeWord.isWholeSignal() ? activeWord : ""));
		final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, new NumberAxis());

		XYChart.Series series = new XYChart.Series();

		double maxValue = Double.MIN_VALUE;
		double minValue = Double.MAX_VALUE;

		int samplesPerWindow = signal.size() / 1000; // this way we will have 1000 points per signal
		if (samplesPerWindow == 0)
			samplesPerWindow = 1;

		int samples = 0;
		double sum = 0;
		int i;
		for (i = 100; i < signal.size(); i++) {
			double sample = signal.get(i);
			sum += sample;
			samples++;

			if (samples % samplesPerWindow == 0) {
				double trackingSample = sum / samples;
				double x;
				if (!activeWord.isWholeSignal()) {
					x = (i + activeWord.getStart()) * sampleDuration;
				} else {
					x = i * sampleDuration;
				}
				series.getData().add(new XYChart.Data(x, trackingSample));
				if (trackingSample < minValue)
					minValue = trackingSample;
				if (trackingSample > maxValue)
					maxValue = trackingSample;
				sum = 0;
				samples = 0;
			}

		}

		if (samples != 0) {
			double x;
			if (!activeWord.isWholeSignal()) {
				x = (i + activeWord.getStart()) * sampleDuration;
			} else {
				x = i * sampleDuration;
			}
			series.getData().add(new XYChart.Data(x, sum / samples));
		}

		// series.getData().add(new XYChart.Data(position * sampleDuration, minValue *
		// 2));
		// series.getData().add(new XYChart.Data(position * sampleDuration, maxValue *
		// 2));

		if (activeWord.isWholeSignal()) {
			for (Word word : words) {
				series.getData().add(new XYChart.Data(word.getStart() * sampleDuration, minValue * 2));
				series.getData().add(new XYChart.Data(word.getStart() * sampleDuration, maxValue * 2));

				series.getData().add(new XYChart.Data(word.getEnd() * sampleDuration, minValue * 2));
				series.getData().add(new XYChart.Data(word.getEnd() * sampleDuration, maxValue * 2));
			}

		}

		// System.out.println("position: " + position);
		// System.out.println("word start: " + activeWord.getStart());
		// System.out.println("svee: " + (activeWord.getStart() + position) *
		// sampleDuration);
		// position marker:
		if (position != -1) {
			series.getData().add(new XYChart.Data((activeWord.getStart() + position) * sampleDuration, minValue * 2.5));
			series.getData().add(new XYChart.Data((activeWord.getStart() + position) * sampleDuration, maxValue * 2.5));
		}
		lineChart.getData().add(series);
		return lineChart;
	}

	public static final Chart barFrequency(List<Frequency> signal, double amplitudeFilter, int frequencyStart,
			int frequencyEnd) {
		final BarChart chart = new BarChart(new CategoryAxis(), new NumberAxis());

		XYChart.Series series = new XYChart.Series();

		System.out.println("size : " + signal.size());

		double maxAmplitude = signal.stream().mapToDouble(Frequency::getAmplitude).max().getAsDouble();
		for (Frequency sample : signal) {
			boolean isInZoomRange = frequencyStart <= sample.getFrequency() && sample.getFrequency() <= frequencyEnd;
			if (sample.getFrequency() < 0.0001 || !isInZoomRange) {
				continue;
			}

			if (sample.getAmplitude() > amplitudeFilter) {
				int normalizedAmplitude = (int) Math.min(Math.round((sample.getAmplitude() / maxAmplitude) * 100), 100);
				series.getData().add(new XYChart.Data(Math.round(sample.getFrequency()) + " Hz", normalizedAmplitude));
				System.out.println("blabla");
			}
		}
		
		chart.getData().add(series);
		chart.setMaxWidth(500);
		return chart;
	}

}
