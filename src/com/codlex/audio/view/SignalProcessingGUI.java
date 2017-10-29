package com.codlex.audio.view;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.codlex.audio.enpointing.Word;
import com.codlex.audio.file.WavFile;
import com.codlex.audio.windowing.WindowFunction;
import com.google.common.collect.Lists;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
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
        this.model.init(WavFile.load("primer.wav"));
        redrawEverything();
        stage.show();
	}

	private void redrawEverything() {
		VBox vbox = new VBox(10);
		List<Node> vboxChildren = vbox.getChildren();
		vboxChildren.add(buildFrequencyChart());
		vboxChildren.add(buildProcessingControls());
		vboxChildren.add(buildTimeChart());
		vboxChildren.add(buildFileControls());
        Scene scene  = new Scene(vbox, 1024, 768);
        this.stage.setScene(scene);			
	}

	private void redrawCharts() {
		VBox vbox = (VBox) this.stage.getScene().getRoot();
		vbox.getChildren().set(0, buildFrequencyChart());
		vbox.getChildren().set(2, buildTimeChart());
	}
	
	private void redrawWindowChooser() {
		VBox vbox = (VBox) this.stage.getScene().getRoot();
		HBox processingControlls = (HBox) vbox.getChildren().get(1);
		processingControlls.getChildren().set(7, buildWindowChooser());
	}
	
	private Node buildFileControls() {
		HBox fileControls = new HBox(5);
		fileControls.setAlignment(Pos.CENTER);
		fileControls.setSpacing(10);
		List<Node> fileControlsChildren = fileControls.getChildren();
		// fileControlsChildren.add(buildWaveGenerator());
		fileControlsChildren.add(createButton("Open", 100, () -> {
			this.model.init(openWavFile(this.stage));
			redrawEverything();
//			fileControlsChildren.set(2, buildWordChooser());
//			redrawCharts();
		}));
		fileControlsChildren.add(buildText("Choose word: "));
		fileControlsChildren.add(buildWordChooser());
		return fileControls;
	}

	private Node buildWordChooser() {
		ChoiceBox<Word> windowNumbers = new ChoiceBox<>(FXCollections.observableArrayList(this.model.getWords()));
		windowNumbers.getSelectionModel().selectFirst();
		windowNumbers.getSelectionModel().selectedItemProperty().addListener((object, oldValue, newValue) -> {
			this.model.setActiveWord(newValue);
			redrawWindowChooser();
			redrawCharts();

		});
		return windowNumbers;
	}

//	private Node buildWaveGenerator() {
//		TextField input = new TextField();
//		input.setPrefWidth(500);
//		input.setText("wave(2,10) + wave(10,100)");
//		
//		HBox holder = new HBox();
//		holder.getChildren().add(input);
//		holder.getChildren().add(createButton("Calculate", 100, () -> {
//			this.model.init(Wave.parse(input.getText()));
//			redrawCharts();
//		}));
//		
//		return holder;
//	}

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
		container.setSpacing(10);
		List<Node> containerChildren = container.getChildren();
		containerChildren.add(buildText("Window function:"));
		containerChildren.add(buildWindowFunctionChooser());
		containerChildren.add(buildText("Window size (ms):"));
		containerChildren.add(buildWindowSizeInput());
		containerChildren.add(buildText("Amplitude filter:"));
		containerChildren.add(buildAmplitudeFilter());
		containerChildren.add(buildText("Window number:"));
		containerChildren.add(buildWindowChooser());
		containerChildren.add(createButton("Process", 100, () -> {
			redrawCharts();
		}));
		
		
		return container;
	}

	private Node buildText(String string) {
		Label label = new Label();
		label.setText(string);
		label.setAlignment(Pos.CENTER);
		// label.setPrefHeight(100);
		return label;
	}

	private Node buildAmplitudeFilter() {
		TextField input = new TextField();
		input.setText("0");
		// filter out anything that is not number
		input.textProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue.isEmpty()) {
				newValue = "0";
			}
	        if (!newValue.matches("\\d*|(\\.)*")) {
	        	String newValidValue = newValue.replaceAll("[^\\d|\\.]", "");
	        	input.setText(newValidValue);
	        	this.model.setAmplitudeFilter(Double.parseDouble(newValidValue));
	        } else {
	        	this.model.setAmplitudeFilter(Double.parseDouble(newValue));
	        }
    		
	    });
		return input;
	}

	private Node buildWindowChooser() {
		List<Integer> numbersSet = IntStream.rangeClosed(0, this.model.calculateNumberOfWindows()).boxed().collect(Collectors.toList());
		ChoiceBox<Integer> windowNumbers = new ChoiceBox<>(FXCollections.observableArrayList(numbersSet));
		windowNumbers.getSelectionModel().selectFirst();
		windowNumbers.getSelectionModel().selectedItemProperty().addListener((object, oldValue, newValue) -> {
			this.model.setActiveWindow(newValue);
			redrawCharts();
		});
		return windowNumbers;
	}

	private Node buildWindowSizeInput() {
		TextField input = new TextField();
		input.setText("10");
		// filter out anything that is not number
		input.focusedProperty().addListener((observable, oldFocusValue, newFocusValue) -> {
			boolean isFoucsLost = oldFocusValue && !newFocusValue;
			if (isFoucsLost) {
				String newValue = input.textProperty().get();
		        if (!newValue.matches("\\d*")) {
		        	String newValidValue = newValue.replaceAll("[^\\d]", "");
		        	input.setText(newValidValue);
		        	this.model.setWindowSize(Integer.parseInt(newValidValue));
		        } else {
		        	this.model.setWindowSize(Integer.parseInt(newValue));
		        }
	    		
	    		redrawWindowChooser();
			}
	    });
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
		return Charts.line(this.model.getSignal(), this.model.getActiveWindowIndex(), this.model.getSampleDuration(), this.model.getWords(), this.model.getActiveWord());
	}

	private Node buildFrequencyChart() {
		return Charts.barFrequency(this.model.calculateFrequencyDomain(), this.model.getAmplitudeFilter(), 0, 10000);
	}

	public static void main(String[] args) {
		launch(new String[0]);
	}

}
