package com.codlex.audio.view;

import java.util.List;

import com.codlex.audio.generator.Wave;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

public class MainController {
	
	@FXML
	private LineChart<Double, Double> lineChart;
	
	
	@FXML
	public void onAddClicked() {
        
		
		XYChart.Series series = new XYChart.Series();
        series.setName("My portfolio");
		List<Double> signal = Wave.sine(10, 100, 1024);

		int i = 0;
		for (Double sample : signal) {
			series.getData().add(new XYChart.Data(i, sample));
			i++;
		}
		
		lineChart.getData().add(series);
	}

}
