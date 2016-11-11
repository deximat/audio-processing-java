package com.codlex.audio.view;


import java.util.List;
import java.util.function.Consumer;

import com.codlex.audio.file.WavFile;
import com.codlex.audio.generator.Wave;
import com.codlex.audio.windowing.WindowFunction;
import com.google.common.collect.Lists;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import lombok.installer.WindowsDriveInfo;
import javafx.stage.Stage;

public class SignalProcessingGUI extends Application {

	private Model model;
	private Stage stage;

	private WavFile openWavFile(Stage stage) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Wav File");
		// TODO: check why extensions doesn't work
		fileChooser.setSelectedExtensionFilter(new ExtensionFilter("Uncompressed wav file.", Lists.newArrayList("*.wav", "*.wave")));
		return WavFile.load(fileChooser.showOpenDialog(stage).getAbsolutePath());
	}
	
	@Override
	public void start(final Stage stage) throws Exception {
		this.stage = stage;
        stage.setTitle("Signal Processing App");
        this.model = new Model();
        this.model.init(Wave.sine(12, 1, 1024));
		VBox vbox = new VBox(10);
		List<Node> vboxChildren = vbox.getChildren();
		vboxChildren.add(buildFrequencyChart());
		vboxChildren.add(buildProcessingControls());
		vboxChildren.add(buildTimeChart());
		vboxChildren.add(buildFileControls());
        Scene scene  = new Scene(vbox, 800, 600);
        this.stage.setScene(scene);
        stage.show();
	}
	
	private void redrawCharts() {
		VBox vbox = (VBox) this.stage.getScene().getRoot();
		vbox.getChildren().set(0, buildFrequencyChart());
		vbox.getChildren().set(2, buildTimeChart());
	}
	
	private Node buildFileControls() {
		HBox fileControls = new HBox(5);
		List<Node> fileControlsChildren = fileControls.getChildren();
		fileControlsChildren.add(buildWaveGenerator());
		fileControlsChildren.add(createButton("Open", 100, () -> {
			this.model.init(openWavFile(this.stage));
			redrawCharts();
		}));
		return fileControls;
	}

	private Node buildWaveGenerator() {
		TextField input = new TextField();
		input.setPrefWidth(500);
		input.setText("wave(2,10) + wave(10,100)");
		
		HBox holder = new HBox();
		holder.getChildren().add(input);
		holder.getChildren().add(createButton("Calculate", 100, () -> {
			this.model.init(Wave.parse(input.getText()));
			redrawCharts();
		}));
		
		return holder;
	}

	private Button createButton(String string, int i, Runnable onClick) {
		final Button button = new Button();
		button.setText(string);
		button.setPrefWidth(100);
		button.addEventHandler(MouseEvent.MOUSE_CLICKED, 
		    new EventHandler<MouseEvent>() {
		        @Override public void handle(MouseEvent e) {
		        	onClick.run();
		        }
		});
		return button;
	}

	private Node buildProcessingControls() {
		HBox container = new HBox();
		List<Node> containerChildren = container.getChildren();
		containerChildren.add(buildWindowFunctionChooser());
		containerChildren.add(buildWindowSizeInput());
		containerChildren.add(createButton("Process", 100, () -> {
			redrawCharts();
		}));
		
		
		return container;
	}

	private Node buildWindowSizeInput() {
		TextField input = new TextField();
		// filter out anything that is not number
		input.textProperty().addListener((observable, oldValue, newValue) -> {
	        if (!newValue.matches("\\d*")) {
	        	String newValidValue = newValue.replaceAll("[^\\d]", "");
	        	input.setText(newValidValue);
	        	if (!newValidValue.equals(oldValue)) {
	        		// update model if new value is present
	        		this.model.setWindowSize(Integer.parseInt(newValidValue));
	        	}
	        }
	    });
		input.setText("1024");
		return input;
	}

	private Node buildWindowFunctionChooser() {
		ChoiceBox<WindowFunction> windowFunctions = new ChoiceBox<>(FXCollections.observableArrayList(WindowFunction.values()));
		windowFunctions.getSelectionModel().selectFirst();
		windowFunctions.getSelectionModel().selectedItemProperty().addListener((object, oldValue, newValue) -> {
			this.model.setWindowFunction(newValue);
		});
		return windowFunctions;
	}

	private Node buildTimeChart() {
		return Charts.line(this.model.getSignal());
	}

	private Node buildFrequencyChart() {
		return Charts.lineFrequency(this.model.calculateFrequencyDomain());
	}

	public static void main(String[] args) {
		launch(new String[0]);
	}

}
