package com.codlex.audio.pg.domaci1;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.codlex.audio.file.WavFile;
import com.codlex.audio.transform.Frequency;
import com.codlex.audio.view.Charts;
import com.codlex.audio.view.Model;
import com.codlex.audio.windowing.WindowFunction;
import com.codlex.helpers.pdf.PDF;
import com.google.common.collect.Lists;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class Domaci1 extends Application {

	private Node buildWindowSizeInput() {
		TextField input = new TextField();
		input.setText("" + this.model.getWindowSizeMs());
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

				redrawEverything();
			}
		});
		return input;
	}

	private Node buildProcessingControls() {
		HBox container = new HBox();
		container.setSpacing(10);
		List<Node> containerChildren = container.getChildren();
		containerChildren.add(buildText("Window function:"));
		containerChildren.add(buildWindowFunctionChooser());
		containerChildren.add(buildText("Window size (ms):"));
		containerChildren.add(buildWindowSizeInput());
		containerChildren.add(buildText("Window number:"));
		containerChildren.add(buildWindowChooser());
		containerChildren.add(createButton("Process", () -> {
			redrawEverything();
		}));
		
		Button openWav = new Button("Open file");
		openWav.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
			WavFile file = openWavFile(this.stage);
			if (file != null) {
				this.model.init(file);
				onModelInitialized();
			}
			redrawEverything();
		});
		containerChildren.add(openWav);


		return container;
	}

	private Button createButton(String string, Runnable onClick) {
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

	private Node buildWindowChooser() {
		List<Integer> numbersSet = IntStream.rangeClosed(0, this.model.calculateNumberOfWindows() - 1).boxed()
				.collect(Collectors.toList());
		ChoiceBox<Integer> windowNumbers = new ChoiceBox<>(FXCollections.observableArrayList(numbersSet));
		windowNumbers.getSelectionModel().select(this.model.getActiveWindow());
		windowNumbers.getSelectionModel().selectedItemProperty().addListener((object, oldValue, newValue) -> {
			this.model.setActiveWindow(newValue);
			redrawEverything();
		});
		return windowNumbers;
	}

	private Node buildText(String string) {
		Label label = new Label();
		label.setText(string);
		label.setAlignment(Pos.CENTER);
		// label.setPrefHeight(100);
		return label;
	}

	private Stage stage;
	private Model model;

	@Override
	public void start(final Stage stage) throws Exception {
		this.model = new Model();
		this.model.init(WavFile.load("d1-tests/5-generated-cat.wav"));
		onModelInitialized();

		this.stage = stage;
		stage.setTitle("Domaci 1 - dpekter RN13/11");

		redrawEverything();
		stage.show();
	}

	private void onModelInitialized() {
		this.sonogramStart = 0;
		this.sonogramEnd = this.model.getDuration();
		this.frequencyStart = 0;
		this.frequencyEnd = this.model.getFrequencyCount();
		System.out.println("fe: " + this.frequencyEnd);
		this.windowFunctionSelected = 0;
	}

	private WavFile openWavFile(Stage stage) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Wav File");
		fileChooser.setSelectedExtensionFilter(
				new ExtensionFilter("Uncompressed wav file.", Lists.newArrayList("*.wav", "*.wave")));
		return WavFile.load(fileChooser.showOpenDialog(stage).getAbsolutePath());
	}

	private int windowFunctionSelected;

	private Node buildWindowFunctionChooser() {
		ChoiceBox<WindowFunction> windowFunctions = new ChoiceBox<>(
				FXCollections.observableArrayList(WindowFunction.values()));
		windowFunctions.getSelectionModel().select(this.windowFunctionSelected);
		windowFunctions.getSelectionModel().selectedItemProperty().addListener((object, oldValue, newValue) -> {
			this.model.setWindowFunction(newValue);
			this.windowFunctionSelected = windowFunctions.getSelectionModel().getSelectedIndex();
			redrawEverything();
		});
		return windowFunctions;
	}

	private Node buildFrequencyChart() {
		VBox box = new VBox();
		javafx.scene.chart.Chart chart = Charts.barFrequency(this.model.calculateFrequencyDomain(),
				this.model.getAmplitudeFilter(), this.frequencyStart, this.frequencyEnd);
		box.getChildren().add(buildFrequencyControls(chart));
		box.getChildren().add(chart);
		return box;
	}

	private void redrawEverything() {
		VBox vbox = new VBox(10);
		List<Node> vboxChildren = vbox.getChildren();
		vboxChildren.add(buildCharts());
		vboxChildren.add(buildProcessingControls());
		vbox.setAlignment(Pos.CENTER);
		Scene scene = new Scene(vbox, 1100, 500);
		this.stage.setScene(scene);
	}

	private Node buildCharts() {
		HBox hbox = new HBox();
		hbox.getChildren().add(buildSonogram());
		hbox.getChildren().add(buildFrequencyChart());
		return hbox;
	}

	private Node buildSonogram() {
		VBox vbox = new VBox();

		List<List<Frequency>> signal = this.model.calculateFullFrequencyDomain().subList(
				this.sonogramStart / this.model.getWindowSizeMs(),
				Math.max(1, this.sonogramEnd / this.model.getWindowSizeMs()));
		System.out.println("ss: " + this.sonogramStart + " se: " + this.sonogramEnd);
		Canvas sonogram = createGrid(signal, this.sonogramStart, this.sonogramEnd);
		vbox.getChildren().add(buildSonogramControls(sonogram));
		vbox.getChildren().add(sonogram);
		return vbox;
	}

	private int sonogramStart;
	private int sonogramEnd;
	
	private int frequencyStart;
	private int frequencyEnd;

	private Node buildSonogramControls(final Node sonogram) {

		HBox hbox = new HBox();

		Button moveLeft = new Button("<");
		moveLeft.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
			int size = this.sonogramEnd - this.sonogramStart;
			if (this.sonogramStart - size < 0 || this.sonogramEnd - size < 0) {
				System.out.println("tried to move to left but can't go anymore");
				this.sonogramStart = 0;
				this.sonogramEnd = Math.min(size, this.model.getDuration());
				redrawEverything();
				return;
			}

			this.sonogramStart -= size;
			this.sonogramEnd -= size;

			redrawEverything();
		});
		hbox.getChildren().add(moveLeft);

		Button zoomIn = new Button("+");
		zoomIn.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
			int size = this.sonogramEnd - this.sonogramStart;
			int nextSize = Math.max(10, size / 2);

			this.sonogramEnd = this.sonogramStart + nextSize;
			redrawEverything();
		});
		hbox.getChildren().add(zoomIn);

		Button zoomOut = new Button("-");
		zoomOut.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
			int size = this.sonogramEnd - this.sonogramStart;
			int newSize = size * 2;
			if (this.sonogramEnd + newSize >= this.model.getDuration()) {
				this.sonogramEnd = this.model.getDuration();
				this.sonogramStart = Math.max(0, this.sonogramEnd - newSize);
				redrawEverything();
				return;
			}

			this.sonogramEnd = this.sonogramStart + Math.min(this.model.getDuration(), newSize);
			redrawEverything();
		});
		hbox.getChildren().add(zoomOut);

		Button moveRight = new Button(">");
		moveRight.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
			int size = this.sonogramEnd - this.sonogramStart;
			if (this.sonogramStart + size > this.model.getDuration()
					|| this.sonogramEnd + size > this.model.getDuration()) {
				System.out.println("tried to move to right but can't go anymore");
				this.sonogramStart = Math.max(0, this.model.getDuration() - size);
				this.sonogramEnd = this.model.getDuration();
				redrawEverything();

				return;
			}

			this.sonogramStart += size;
			this.sonogramEnd += size;
			redrawEverything();

		});
		hbox.getChildren().add(moveRight);

		hbox.getChildren().add(buildExportToPdf(sonogram));
		
		hbox.setAlignment(Pos.CENTER);
		return hbox;
	}

	private Node buildExportToPdf(Node node) {
		return createButton("TO PDF", () -> {
			WritableImage image = node.snapshot(new SnapshotParameters(), null);
			PDF.exportImageToPdf(PDF.fileChooser(this.stage), SwingFXUtils.fromFXImage(image, null));
		});
	}

	private Node buildFrequencyControls(Node chart) {
		HBox hbox = new HBox();

		Button moveLeft = new Button("<");
		moveLeft.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
			int size = this.frequencyEnd - this.frequencyStart;
			if (this.frequencyStart - size < 0 || this.frequencyEnd - size < 0) {
				System.out.println("tried to move to left but can't go anymore");
				this.frequencyStart = 0;
				this.frequencyEnd = Math.min(size, this.model.getFrequencyCount());
				redrawEverything();
				return;
			}

			this.frequencyStart -= size;
			this.frequencyEnd -= size;

			redrawEverything();
		});
		hbox.getChildren().add(moveLeft);

		Button zoomIn = new Button("+");
		zoomIn.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
			int size = this.frequencyEnd - this.frequencyStart;
			int nextSize = Math.max(10, size / 2);

			this.frequencyEnd = this.frequencyStart + nextSize;
			redrawEverything();
		});
		hbox.getChildren().add(zoomIn);

		Button zoomOut = new Button("-");
		zoomOut.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
			int size = this.frequencyEnd - this.frequencyStart;
			int newSize = size * 2;
			if (this.frequencyEnd + newSize >= this.model.getFrequencyCount()) {
				this.frequencyEnd = this.model.getFrequencyCount();
				this.frequencyStart = Math.max(0, this.frequencyEnd - newSize);
				redrawEverything();
				return;
			}

			this.frequencyEnd = Math.min(this.model.getFrequencyCount(), this.frequencyStart + newSize);
			redrawEverything();
		});
		hbox.getChildren().add(zoomOut);

		Button moveRight = new Button(">");
		moveRight.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
			int size = this.frequencyEnd - this.frequencyStart;
			if (this.frequencyStart + size >= this.model.getFrequencyCount()
					|| this.frequencyEnd + size >= this.model.getFrequencyCount()) {
				System.out.println("tried to move to right but can't go anymore");
				this.frequencyStart = Math.max(0, this.model.getFrequencyCount() - size);
				this.frequencyEnd = this.model.getFrequencyCount();
				redrawEverything();

				return;
			}

			this.frequencyStart += size;
			this.frequencyEnd += size;
			redrawEverything();

		});
		hbox.getChildren().add(moveRight);

		hbox.getChildren().add(buildExportToPdf(chart));
		hbox.setAlignment(Pos.CENTER);
		return hbox;
	}

	public static void main(final String[] args) {
		launch(args);

	}

	private int map(int minValue, int maxValue, double percent) {
		int count = maxValue - minValue;
		return (int) (count * percent);
	}

	private Canvas createGrid(List<List<Frequency>> data, int beginScale, int endScale) {
		// double maxAmplitude = 0.1;
		double maxAmplitude = data.stream()
				.mapToDouble(d -> d.stream().mapToDouble(x -> x.getAmplitude()).max().getAsDouble()).max()
				.getAsDouble();

		// System.out.println("maxFrequ:" + maxFrequency);
		// System.exit(0);
		//
		int width = 500;
		int height = 300;
		int offset = 100;

		Canvas canvas = new Canvas(width + offset, height + offset);
		GraphicsContext gc = canvas.getGraphicsContext2D();

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int x = (int) ((double) i / width * data.size());
				int y = (int) ((double) j / height * data.get(x).size());
				// System.out.println("size: " + data.get(0).size());
				// System.out.println("height: " + height + " j: " + j);
				// System.out.println("x: " + x + " y: " + y);
				double percent = Math.min(1, data.get(x).get(y).getAmplitude() / maxAmplitude);
				// System.out.println("p: " + percent);
				int brighteness = map(0, 255, 1 - percent);
				// if (brighteness < 250) {
				gc.getPixelWriter().setColor(i, height - j, Color.rgb(brighteness, brighteness, brighteness));
				// }
			}
		}

		int numberOfVPodeoka = 5;
		int maxFreq = 22050;
		for (int i = 1; i <= numberOfVPodeoka; i++) {
			float percent = ((float) i / numberOfVPodeoka);
			int y = Math.round(percent * height);

			int scaleSize = 10;
			for (int j = 0; j < width + scaleSize; j++) {
				gc.getPixelWriter().setColor(j, y, Color.BLACK);
			}

			int value = Math.round((1 - percent) * maxFreq);
			gc.setStroke(Color.BLACK);
			gc.fillText("" + value, width + scaleSize * 2, y);
		}

		int numberOfPodeoka = 10;

		for (int i = 0; i <= numberOfPodeoka; i++) {
			float percent = (float) i / numberOfPodeoka;
			int x = (int) (percent * width);

			int scaleSize = 10;
			for (int j = 0; j < scaleSize; j++) {
				gc.getPixelWriter().setColor(x, height + j, Color.BLACK);
			}
			int value = beginScale + Math.round(percent * (endScale - beginScale));
			gc.setStroke(Color.BLACK);
			gc.setTextAlign(TextAlignment.CENTER);
			gc.fillText("" + value, x, height + scaleSize * 2);
		}
		
		// TODO: fix for zoom
		for (int i = 0; i < 10; i++) {
			double percent = (double) this.model.getActiveWindow() / this.model.calculateNumberOfWindows();
			int x = (int) Math.round(width * percent);
			System.out.println("x: " + x + "percent: " + percent);
			gc.getPixelWriter().setColor(x, height + i, Color.RED);
		}

		return canvas;

	}

}
