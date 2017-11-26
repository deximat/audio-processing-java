package com.codlex.audio.pg.domaci2;

import java.util.ArrayList;

import com.codlex.audio.enpointing.Word;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import lombok.Getter;

public class WordsList {

	private class HBoxCell extends HBox {
		@Getter
		private Word word;

		HBoxCell(final Word word) {
			this.word = word;
			Label label = new Label();
			label.setText(word.getName());
			label.setMaxWidth(Double.MAX_VALUE);
			this.getChildren().add(label);

			HBox.setHgrow(label, Priority.ALWAYS);

			final Button setAsLeftButton = new Button("Set as left");
			setAsLeftButton.addEventHandler(MouseEvent.MOUSE_CLICKED,
					(e) -> WordsList.this.main.setLeftWord(this.word));
			this.getChildren().add(setAsLeftButton);

			final Button setAsRightButton = new Button("Set as right");
			setAsRightButton.addEventHandler(MouseEvent.MOUSE_CLICKED,
					(e) -> WordsList.this.main.setRightWord(this.word));
			this.getChildren().add(setAsRightButton);

		}
	}

	private final Domaci2 main;
	private final ListView<HBoxCell> node;
	ObservableList<HBoxCell> myObservableList = FXCollections.observableList(new ArrayList<>());

	public Node getNode() {
		return this.node;
	}

	public WordsList(Domaci2 main) {
		this.main = main;
		this.node = new ListView<HBoxCell>();
		node.setItems(this.myObservableList);
	}

	public void addWord(Word word) {
		this.myObservableList.add(new HBoxCell(word));
	}

	public void recalculate() {
		for (HBoxCell wordContainer : this.myObservableList) {
			wordContainer.getWord().recalculateLPC();
		}
	}
}