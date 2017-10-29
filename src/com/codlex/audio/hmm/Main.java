package com.codlex.audio.hmm;

import java.util.List;

import com.codlex.audio.hmm.model.HiddenMarkovModel;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

public class Main extends Application {
//
//	private ObservableList<Sequence> sequences = FXCollections.observableArrayList();
//	private ObservableList<Sequence> trainingSequences = FXCollections.observableArrayList();
//		
//	private Sequence currentSequence;
//	private SimilarVectorsGenerator similarVectorGenerator;
//
//	private GridPane sequenceView;
//	private GridPane similarVectorsView;
//	private Window stage;
//	
//	private HiddenMarkovModel model = new HiddenMarkovModel(3);
//	
//	private FindResults lastFindResult;
//	private GridPane trelis = new GridPane();
//	
//	@Override
	public void start(Stage primaryStage) {
//		try {
//			this.stage = primaryStage;
//			
//			VBox main = new VBox();
//			
//			HBox controlls = new HBox();
//			controlls.getChildren().add(buildSequencesView());
//			controlls.getChildren().add(buildTrainingSequencesView());
//			controlls.getChildren().add(buildModelControllsView());
//			main.getChildren().add(controlls);
//			main.getChildren().add(this.trelis);
//
//			Scene scene = new Scene(main, 1024, 768);
//
//			primaryStage.setScene(scene);
//			primaryStage.show();
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
//
//	private Node buildModelControllsView() {
//		VBox box = new VBox();
//		Button openModel = new Button("Load model");
//		openModel.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
//			FileChooser fileChooser = new FileChooser();
//			fileChooser.setTitle("Open HMM model");
//			openModel(fileChooser.showOpenDialog(this.stage).getAbsolutePath());
//		});
//		
//		Button saveModel = new Button("Save model");
//		saveModel.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
//			FileChooser fileChooser = new FileChooser();
//			fileChooser.setTitle("Save HMM model");
//			saveModel(fileChooser.showSaveDialog(this.stage).getAbsolutePath());
//		});
//		
//		Button resetModel = new Button("Reset model");
//		resetModel.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
//			resetModel();
//		});
//		
//		box.getChildren().add(new Label("HMM Operations"));
//		box.getChildren().add(openModel);
//		box.getChildren().add(saveModel);
//		
//		return box;
//
//		
//		
//	}
//
//	private void resetModel() {
//		System.out.println("THIS METHOD IS NOT IMPLEMENTED YET");		
//	}
//
//	private void saveModel(String absolutePath) {
//		System.out.println("THIS METHOD IS NOT IMPLEMENTED YET");
//	}
//
//	private void openModel(String absolutePath) {
//		System.out.println("THIS METHOD IS NOT IMPLEMENTED YET");
//	}
//
//	private Node buildSequencesView() {
//		VBox pane = new VBox();
//		TableView<Sequence> table = new TableView<>();
//		table.setItems(this.sequences);		
//		TableColumn<Sequence, String> column = new TableColumn<>("Sequence name");
//		column.setCellValueFactory(
//	                new PropertyValueFactory<>("name"));
//		
//		table.getColumns().add(column);
//		table.getColumns().add(FxHelpers.createButtonColumn("Remove", (seq) -> {
//			this.sequences.remove(seq);
//		}));
//		table.getColumns().add(FxHelpers.createButtonColumn("Edit", (seq) -> {
//			editSequence(seq);
//		}));
//		
//		table.getColumns().add(FxHelpers.createButtonColumn("Add to training", (seq) -> {
//			this.trainingSequences.add(seq);
//		}));
//		
//		table.getColumns().add(FxHelpers.createButtonColumn("Find", (seq) -> {
//			findSequence(seq);
//		}));
//		
//		Label label = new Label("All sequences");
//		label.setFont(new Font(20));
//		pane.getChildren().add(label);
//		pane.getChildren().add(table);
//		pane.getChildren().add(buildSequenceCreator());
//		return pane;
//	}
//	
//	private static int VECTOR_DIMENSIONS = 10;
//	
//	private Node buildSequenceCreator() {
//		HBox sequenceCreator = new HBox();
//		TextField sequenceName = new TextField("Sequence1");
//		
//		
//		Button button = new Button("New sequence");
//		button.addEventFilter(MouseEvent.MOUSE_CLICKED, (e) -> {
//			Sequence sequence = new Sequence(sequenceName.textProperty().get(), VECTOR_DIMENSIONS);
//			this.sequences.add(sequence);
//			System.out.println(this.sequences.size());
//			onSequencesChange();
//		});
//		sequenceCreator.getChildren().add(sequenceName);
//		sequenceCreator.getChildren().add(button);
//		
//		return sequenceCreator;
//	}
//
//	private Node buildTrainingSequencesView() {
//		VBox pane = new VBox();
//		
//		TableView<Sequence> table = new TableView<>();
//		table.setItems(this.trainingSequences);		
//		TableColumn<Sequence, String> column = new TableColumn<>("Sequence name");
//		column.setCellValueFactory(
//	                new PropertyValueFactory<>("name"));
//		
//		table.getColumns().add(column);
//		table.getColumns().add(FxHelpers.createButtonColumn("Remove", (seq) -> {
//			this.trainingSequences.remove(seq);
//		}));
//		table.getColumns().add(FxHelpers.createButtonColumn("Edit", (seq) -> {
//			editSequence(seq);
//		}));
//		table.getColumns().add(FxHelpers.createButtonColumn("Find", (seq) -> {
//			findSequence(seq);
//		}));
//		
//		Label label = new Label("Training sequences");
//		label.setFont(new Font(20));
//		pane.getChildren().add(label);
//		pane.getChildren().add(table);
//		Button trainButton = new Button("Train");
//		trainButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
//			this.model.train(this.trainingSequences);
//		});
//		
//		// TOOD: add logic
//		pane.getChildren().add(trainButton);
//		return pane;
//	}
//
//	private void findSequence(Sequence seq) {		
//		this.lastFindResult = this.model.find(seq);
//		onFindResultChanged();
//	}
//
//	private void onFindResultChanged() {
//		this.trelis.getChildren().clear();
//		ScoreAndState[][] results = this.lastFindResult.getResults();
//		int[] bestStates = this.lastFindResult.getStateSequence();
//		for (int i = 0; i < results.length; i++) {
//			for (int j = 0; j < results[i].length; j++) {
//				Label label = new Label();
//				label.setText(Double.toString(results[i][j].getScore()));
//				if (i == bestStates[j]) {
//					label.setStyle("-fx-text-fill: green;");
//				}
//				this.trelis.add(label, j, i);
//			}
//		}
//	}
//
//	private void onSequencesChange() {
//		System.out.println("NOT IMPLEMENTED YET");
//	}
//
//	private void editSequence(Sequence sequence) {
//		Stage editSequenceStage = new Stage();
//		this.sequenceView = new GridPane();
//		this.similarVectorsView = new GridPane();
//		this.currentSequence = sequence;
//		this.similarVectorGenerator = new SimilarVectorsGenerator(this.currentSequence.getVectorSize());
//
//		
//		VBox sequenceEditorWrapper = new VBox();
//		rebuildSequence();
//		sequenceEditorWrapper.getChildren().add(this.sequenceView);
//		sequenceEditorWrapper.getChildren().add(buildVectorControls());
//		sequenceEditorWrapper.getChildren().add(this.similarVectorsView);
//		TextField index = new TextField("0");
//		sequenceEditorWrapper.getChildren().add(index);
//		Button addAllButton = new Button("Add all.");
//		addAllButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
//			for (List<Double> vector : this.similarVectorGenerator.getSimilarVectors()) {
//				this.currentSequence.insertVector(Integer.parseInt(index.textProperty().get()), vector);
//			}
//			
//			onSequenceChange();
//		});
//		sequenceEditorWrapper.getChildren().add(addAllButton);
//
//		Scene scene = new Scene(sequenceEditorWrapper, 400, 400);
//		editSequenceStage.setScene(scene);
//		editSequenceStage.show();
//	}
//
//	private Node buildCovarienceView(final TextField index) {
//		VBox wrapper = new VBox();
//		
//		GridPane pane = new GridPane();
//		double[][] covarience = this.similarVectorGenerator.getCovariance(); 
//		for (int i = 0; i < covarience.length; i++) {
//			final int row = i;
//			for (int j = 0; j < covarience[i].length; j++) {
//				final int column = j;
//				TextField field = new TextField();
//				field.setMaxWidth(40);
//				field.setText(Double.toString(covarience[i][j]));
//				field.textProperty().addListener((e) -> {
//					try {
//						Double newValue = Double.parseDouble(field.getText());
//						covarience[row][column] = newValue;
//					} catch (NumberFormatException error) {
//						System.out.println("Empty string will not be updated");
//					}
//				});
//				pane.add(field, i, j);
//			}
//		}
//		Label title = new Label("Generation Covarience");
//		title.setFont(new Font(30));
//		
//		Button generateSimilar = new Button("Generate similar");
//		generateSimilar.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
//			List<Double> vector = this.currentSequence.getVectors().get(Integer.parseInt(index.textProperty().get()));
//			System.out.println("Generating similar vectors");
//
//			this.similarVectorGenerator.generate(vector);
//			onSimilarVectorsChange();
//		});
//		
//		wrapper.getChildren().add(title);
//		wrapper.getChildren().add(pane);
//		wrapper.getChildren().add(generateSimilar);
//
//		return wrapper;
//	}
//
//	private Node buildVectorControls() {
//		HBox vectorControls = new HBox();
//		vectorControls.getChildren().add(new Label("Index: "));
//
//		TextField index = new TextField();
//		index.setText(Integer.toString(0));
//		vectorControls.getChildren().add(index);
//		Button insertButton = new Button("Insert");
//		insertButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
//			this.currentSequence.insertEmpty(Integer.parseInt(index.textProperty().get()));
//			
//			onSequenceChange();
//		});
//
//		Button deleteButton = new Button("Delete");
//		deleteButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
//			this.currentSequence.deleteVector(Integer.parseInt(index.textProperty().get()));
//			onSequenceChange();
//		});
//
//		Button clearSequence = new Button("Clear sequence");
//		clearSequence.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
//			this.currentSequence.getVectors().clear();
//			this.similarVectorGenerator.getSimilarVectors().clear();
//			onSimilarVectorsChange();
//			onSequenceChange();
//		});
//
//		vectorControls.getChildren().add(insertButton);
//		vectorControls.getChildren().add(deleteButton);
//		vectorControls.getChildren().add(buildCovarienceView(index));
//		vectorControls.getChildren().add(clearSequence);
//
//		return vectorControls;
//
//	}
//
//
//	private void onSimilarVectorsChange() {
//		List<List<Double>> similarVectors = this.similarVectorGenerator.getSimilarVectors();
//		this.similarVectorsView.getChildren().clear();
//
//		for (int i = 0; i < similarVectors.size(); i++) {
//			List<Double> vector = similarVectors.get(i);
//			Label numb = new Label("V" + i);
//			numb.setPrefWidth(100.0);
//			GridPane.setHalignment(numb, HPos.CENTER);
//			this.similarVectorsView.add(numb, 0, i);
//
//			for (int j = 0; j < vector.size(); j++) {
//				Double coordinate = vector.get(j);
//				TextField field = new TextField();
//				field.setEditable(false);
//				field.setText(coordinate.toString());
//				this.similarVectorsView.add(field, j + 1, i);
//			}
//
//			Label insertLabel = new Label("Insert at:");
//			insertLabel.setPrefWidth(100);
//			GridPane.setHalignment(insertLabel, HPos.CENTER);
//
//			this.similarVectorsView.add(insertLabel, vector.size() + 1, i);
//			TextField insertIndex = new TextField("0");
//			insertIndex.setPrefWidth(50);
//			this.similarVectorsView.add(insertIndex, vector.size() + 2, i);
//			Button insertButton = new Button("Insert");
//			insertButton.setPrefWidth(100);
//			insertButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (e) -> {
//				this.currentSequence.insertVector(Integer.parseInt(insertIndex.textProperty().get()), vector);
//				onSequenceChange();
//			});
//			this.similarVectorsView.add(insertButton, vector.size() + 3, i);
//		}
//	}
//
//	private void rebuildSequence() {
//		Platform.runLater(() -> {
//			this.sequenceView.getChildren().clear();
//			List<List<Double>> vectors = this.currentSequence.getVectors();
//			for (int i = 0; i < vectors.size(); i++) {
//				List<Double> vector = vectors.get(i);
//				for (int j = 0; j < vector.size(); j++) {
//					Double coordinate = vector.get(j);
//					TextField field = new TextField();
//					field.setText(coordinate.toString());
//					final int row = i;
//					final int column = j;
//					field.textProperty().addListener((e) -> {
//						try {
//							Double newValue = Double.parseDouble(field.getText());
//							this.currentSequence.getVectors().get(row).set(column, newValue);
//						} catch (NumberFormatException error) {
//							System.out.println("Empty string will not be updated");
//						}
//					});
//					this.sequenceView.add(field, i, j);
//				}
//			}
//
//			for (int j = 0; j < vectors.size(); j++) {
//				Label label = new Label("V" + j);
//				GridPane.setHalignment(label, HPos.CENTER);
//				this.sequenceView.add(label, j, vectors.get(0).size());
//			}
//		});
//	}
//
//	private void onSequenceChange() {
//		rebuildSequence();
//	}

	public static void main(String[] args) {
		launch(args);
	}
}
