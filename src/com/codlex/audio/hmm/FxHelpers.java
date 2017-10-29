package com.codlex.audio.hmm;

import java.util.function.Consumer;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class FxHelpers {

	public static TableColumn<Sequence, Sequence> createButtonColumn(String name, Consumer<Sequence> actionToRun) {
		TableColumn actionCol = new TableColumn(name);
		actionCol.setCellValueFactory(new PropertyValueFactory<>("reference"));

		Callback<TableColumn<Sequence, String>, TableCell<Sequence, String>> cellFactory = //
				new Callback<TableColumn<Sequence, String>, TableCell<Sequence, String>>() {
					@Override
					public TableCell call(final TableColumn<Sequence, String> param) {
						
						final TableCell<Sequence, Sequence> cell = new TableCell<Sequence, Sequence>() {
							final Button btn = new Button(name);
							
							@Override
							public void updateItem(Sequence item, boolean empty) {
								super.updateItem(item, empty);
								if (empty) {
									setGraphic(null);
									setText(null);
								} else {
									btn.setOnAction((ActionEvent event) -> {
										actionToRun.accept(item);
									});
									setGraphic(btn);
									setText(null);
								}
							}
						};
						
						return cell;
					}
				};

		actionCol.setCellFactory(cellFactory);

		return actionCol;
	}

}
