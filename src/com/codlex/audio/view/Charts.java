package com.codlex.audio.view;

import java.util.List;

import com.codlex.audio.transform.Frequency;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class Charts {
	public static final LineChart line(List<Double> signal) {
        final LineChart<Number,Number> lineChart = 
                new LineChart<Number,Number>(new NumberAxis(), new NumberAxis());
        
        XYChart.Series series = new XYChart.Series();
        
		int i = 0;
		for (Double sample : signal) {
			series.getData().add(new XYChart.Data(i, sample));
			if (i > 1000) {
				break;
			}
			i++;
		}
        lineChart.getData().add(series);

		return lineChart;
	}
	
	
	public static final LineChart lineFrequency(List<Frequency> signal) {
        final LineChart<Number,Number> lineChart = 
                new LineChart<Number,Number>(new NumberAxis(), new NumberAxis());
        
        XYChart.Series series = new XYChart.Series();
        
		int i = 0;
		for (Frequency sample : signal) {
			if (i > 1000) {
				break;
			}
			series.getData().add(new XYChart.Data(sample.getFrequency(), sample.getAmplitude()));
			i++;
		}
        lineChart.getData().add(series);

		return lineChart;
	}
	
	
	
}
