package com.codlex.audio.view;

import java.util.List;

import com.codlex.audio.transform.Frequency;

import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class Charts {
	public static final LineChart line(List<Double> signal) {
        final LineChart<Number,Number> lineChart = 
                new LineChart<Number,Number>(new NumberAxis(), new NumberAxis());
        
        XYChart.Series series = new XYChart.Series();
        
		for (int i = 0; i < signal.size(); i += signal.size() / 1000) {
			Double sample = signal.get(i);
			series.getData().add(new XYChart.Data(i, sample));
		}
        lineChart.getData().add(series);

		return lineChart;
	}
	
	
	public static final LineChart lineFrequency(List<Frequency> signal, double amplitudeFilter) {
        final LineChart<Number,Number> lineChart = 
                new LineChart<Number,Number>(new NumberAxis(), new NumberAxis());

        XYChart.Series series = new XYChart.Series();
        
        System.out.println("size : " + signal.size());
		for (Frequency sample : signal) {
			
			if (sample.getFrequency() < 0.0001) {
				continue;
			}
			
			if (sample.getAmplitude() > amplitudeFilter) {
				series.getData().add(new XYChart.Data(sample.getFrequency(), sample.getAmplitude()));
			}
			
		}
        lineChart.getData().add(series);


		return lineChart;
	}
	
	
	
}
