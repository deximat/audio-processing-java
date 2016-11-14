package com.codlex.audio.view;


import java.awt.TextField;
import java.util.List;
import java.util.stream.Collectors;

import com.codlex.audio.file.WavFile;
import com.codlex.audio.generator.Wave;
import com.codlex.audio.transform.ComplexNumber;
import com.codlex.audio.transform.FastFourierTransform;
import com.codlex.audio.transform.Frequency;

import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class AudioGUI extends Application {

	@Override
	public void start(final Stage stage) throws Exception {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.showOpenDialog(stage);
//		final FXMLLoader loader = new FXMLLoader();
//		loader.setLocation(AudioGUI.class.getResource("MainScene.fxml"));
//		AnchorPane mainScene = loader.load();
//		primaryStage.setScene(new Scene(mainScene));
//		primaryStage.show()
		
        stage.setTitle("Line Chart Sample");
//        List<Double> signal = Wave.add(Wave.sine(5, 10, 1024), Wave.sine(10, 100, 1024));
        WavFile file = WavFile.load("primer2.wav");
        System.out.println(1 << 15);
        List<Double> signal = file.getSamples(0).subList(5000, 5000 + (1 << 15));
        List<Frequency> ffs = new FastFourierTransform(0.74, signal).getFrequencies();
        
        LineChart chart = Charts.line(signal, 0);
        LineChart chart2 = Charts.lineFrequency(ffs, 0.0);

		VBox box = new VBox(10);
		HBox inputBox = new HBox(5);
		javafx.scene.control.TextField input = new javafx.scene.control.TextField();
		input.setMaxWidth(Double.POSITIVE_INFINITY);
		input.setPrefWidth(1000);
		input.setText("wave(2,10) + wave(10,100)");
		inputBox.getChildren().add(input);
		Button calculateButton = new Button();
		calculateButton.setPrefWidth(100);
		calculateButton.addEventHandler(MouseEvent.MOUSE_CLICKED, 
		    new EventHandler<MouseEvent>() {
		        @Override public void handle(MouseEvent e) {
					List<Double> signal = Wave.parse(input.getText());
			        List<Frequency> ffs = new FastFourierTransform(1.0/1024, signal).getFrequencies();

					box.getChildren().set(1, Charts.line(signal));
					box.getChildren().set(2, Charts.lineFrequency(ffs, 0));
		        }
		});
		
		calculateButton.setMaxWidth(Double.POSITIVE_INFINITY);
		calculateButton.setText("Generate");
		inputBox.getChildren().add(calculateButton);
		
		box.getChildren().add(inputBox);
		box.getChildren().add(chart);
		box.getChildren().add(chart2);
        Scene scene  = new Scene(box,800,600);
       
        stage.setScene(scene);
        stage.show();
	}
	
	public static void main(String[] args) {
		launch(new String[0]);
	}

}
