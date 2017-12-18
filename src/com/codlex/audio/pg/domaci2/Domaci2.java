package com.codlex.audio.pg.domaci2;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.codlex.audio.enpointing.Word;
import com.codlex.audio.pg.domaci3.AudioConstants;
import com.codlex.audio.pg.domaci3.JavaSoundRecorder;
import com.codlex.audio.windowing.WindowFunction;
import com.google.common.collect.Lists;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class Domaci2 extends Application {

	private Stage stage;

	// LPC SETTINGS
	private WordsList wordsList = new WordsList(this);
	private BorderPane chartPane = new BorderPane();
	private int vectorsToAnalyze = 12;

	@Override
	public void start(final Stage stage) throws Exception {
		onModelInitialized();
		this.stage = stage;

		stage.setTitle("Domaci 2 - dpekter RN13/11");

		redrawEverything();
		stage.show();
	}

	private void onModelInitialized() {
	}

	private void redrawEverything() {
		VBox vbox = new VBox(10);
		add(vbox, buildLPCSettings());
		add(vbox, buildNewWords());
		this.wordsList.recalculate();
		add(vbox, this.wordsList.getNode());
		add(vbox, buildSelectedWords());
		add(vbox, chartPane);
		chartPane.setCenter(buildChart());

		vbox.setAlignment(Pos.CENTER);
		Scene scene = new Scene(vbox, 1500, 1000);
		this.stage.setScene(scene);
	}

	private Node buildSelectedWords() {
		HBox selected = new HBox();
		add(selected, buildText("Selected words:      "));
		add(selected, buildText("Left word: " + this.leftWord));
		add(selected, buildWordPositionChooser(this.leftWord));
		add(selected, buildText("          "));
		add(selected, buildText("Right word: " + this.rightWord));
		add(selected, buildWordPositionChooser(this.rightWord));
		add(selected, buildVectorSizeChooser());
		return selected;
	}

	private Node buildVectorSizeChooser() {
		if (this.leftWord == null || this.rightWord == null) {
			return new Pane();
		}

		int maxVectorSize = Math.min(this.leftWord.getCoeficients().size() - this.leftWord.getVectorToAnalyzeStart(),
				this.rightWord.getCoeficients().size() - this.rightWord.getVectorToAnalyzeStart());
		List<Integer> numbersSet = IntStream.rangeClosed(1, maxVectorSize).boxed().collect(Collectors.toList());
		ChoiceBox<Integer> windowNumbers = new ChoiceBox<>(FXCollections.observableArrayList(numbersSet));
		windowNumbers.getSelectionModel().select((Integer) this.vectorsToAnalyze);
		windowNumbers.getSelectionModel().selectedItemProperty().addListener((object, oldValue, newValue) -> {
			this.vectorsToAnalyze = newValue;
			redrawEverything();
		});
		return windowNumbers;
	}

	private Node buildWordPositionChooser(final Word word) {
		if (word == null) {
			return new Pane();
		}

		int maxStartPosition = word.getCoeficients().size() - this.vectorsToAnalyze;
		List<Integer> numbersSet = IntStream.rangeClosed(0, maxStartPosition).boxed().collect(Collectors.toList());
		ChoiceBox<Integer> windowNumbers = new ChoiceBox<>(FXCollections.observableArrayList(numbersSet));
		windowNumbers.getSelectionModel().select((Integer) word.getVectorToAnalyzeStart());
		windowNumbers.getSelectionModel().selectedItemProperty().addListener((object, oldValue, newValue) -> {
			word.setVectorToAnalyzeStart(newValue);
			redrawEverything();
		});
		return windowNumbers;
	}

	private Node buildChart() {
		if (this.leftWord == null || this.rightWord == null) {
			return null;
		}

		final LineChart<Number, Number> lineChart = new LineChart<Number, Number>(new NumberAxis(), new NumberAxis());
		final List<Double> distances = this.leftWord.distancePerCoeff(this.rightWord, this.vectorsToAnalyze);
		XYChart.Series series = new XYChart.Series();

		for (int i = 0; i < distances.size(); i++) {
			series.getData().add(new XYChart.Data(i, distances.get(i)));
		}

		lineChart.getData().add(series);

		return lineChart;
	}

	private File openFile(Stage stage) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Wav File");
		fileChooser.setSelectedExtensionFilter(
				new ExtensionFilter("Uncompressed wav file.", Lists.newArrayList("*.wav", "*.wave")));
		return fileChooser.showOpenDialog(stage).getAbsoluteFile();
	}

	private Node buildNewWords() {
		HBox newWords = new HBox();
		add(newWords, createButton("Record", () -> {
			JavaSoundRecorder.recordSample("IN-APP-RECORDING", (file) -> {
				loadWords(file);
			});
		}));
		add(newWords, createButton("Open wav", () -> {
			loadWords(openFile(stage));
		}));

		return newWords;
	}

	private void loadWords(File file) {
		System.out.println("WORDS LOADED FROM: " + file);
		List<Word> words = Word.loadAll(file.getAbsolutePath());
		for (Word word : words) {
			System.out.println("Adding: " + word);
			this.wordsList.addWord(word);
		}
	}

	private Node buildLPCSettings() {
		HBox lpcSettings = new HBox();

		add(lpcSettings, buildText("LPC Settings:     "));
		add(lpcSettings, createInput("P:  ", () -> AudioConstants.lpcCoeficients,
				(value) -> AudioConstants.lpcCoeficients = value));
		add(lpcSettings, createInput("Window size:  ", () -> AudioConstants.lpcWindowSize,
				(value) -> AudioConstants.lpcWindowSize = value));
		add(lpcSettings, createInput("Window shift:  ", () -> AudioConstants.lpcWindowShift,
				(value) -> AudioConstants.lpcWindowShift = value));
		add(lpcSettings, buildUnitChooser());
		add(lpcSettings, buildWindowFunctionChooser());
		add(lpcSettings, createButton("Apply", () -> redrawEverything()));

		return lpcSettings;
	}

	private Node buildUnitChooser() {
		HBox unitChooser = new HBox();
		add(unitChooser, buildText("Unit: "));
		ChoiceBox<WindowSizeUnit> units = new ChoiceBox<>(FXCollections.observableArrayList(WindowSizeUnit.values()));
		units.getSelectionModel().select(AudioConstants.lpcWindowUnit);
		units.getSelectionModel().selectedItemProperty().addListener((object, oldValue, newValue) -> {
			AudioConstants.lpcWindowUnit = newValue;
			redrawEverything();
		});
		add(unitChooser, units);
		return unitChooser;
	}

	private Node buildWindowFunctionChooser() {
		ChoiceBox<WindowFunction> windowFunctions = new ChoiceBox<>(
				FXCollections.observableArrayList(WindowFunction.values()));
		windowFunctions.getSelectionModel().select(AudioConstants.lpcWindowFunction);
		windowFunctions.getSelectionModel().selectedItemProperty().addListener((object, oldValue, newValue) -> {
			AudioConstants.lpcWindowFunction = newValue;
			redrawEverything();
		});
		return windowFunctions;
	}

	private static Node buildText(String string) {
		Label label = new Label();
		label.setText(string);
		label.setAlignment(Pos.CENTER);
		return label;
	}

	private static Button createButton(String string, Runnable onClick) {
		final Button button = new Button();
		button.setText(string);
		button.setPrefWidth(100);
		button.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				onClick.run();
			}
		});
		return button;
	}

	private Node createInput(String name, Supplier<Integer> getter, Consumer<Integer> setter) {
		HBox field = new HBox();
		field.getChildren().add(buildText(name));

		TextField input = new TextField();
		input.setText("" + getter.get());
		input.focusedProperty().addListener((observable, oldFocusValue, newFocusValue) -> {
			boolean isFoucsLost = oldFocusValue && !newFocusValue;
			if (isFoucsLost) {
				setter.accept(Integer.parseInt(input.getText()));
			}
		});

		field.getChildren().add(input);

		return field;
	}

	public static void main(final String[] args) {
		launch(args);
	}

	private static void add(Pane pane, Node node) {
		pane.getChildren().add(node);
	}

	public void setLeftWord(Word word) {
		this.leftWord = word;
		redrawEverything();
	}

	public void setRightWord(Word word) {
		this.rightWord = word;
		redrawEverything();
	}

	private Word leftWord;
	private Word rightWord;

}
