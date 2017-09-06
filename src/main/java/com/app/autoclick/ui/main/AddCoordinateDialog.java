package com.app.autoclick.ui.main;

import java.awt.MouseInfo;
import java.awt.Point;

import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

public class AddCoordinateDialog extends Dialog<int[]> {
	private int x, y;

	public AddCoordinateDialog() {
		KeyCombination keyCombinRecord = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN);

		Label lblMessage = new Label("Press Ctrl + R to record coordinate");
		Label lblAxis = new Label("Coordinate: 0, 0");

		setTitle("Select Coordinate");
		getDialogPane().setContent(new VBox(5, lblMessage, lblAxis));
		getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		getDialogPane().addEventHandler(KeyEvent.KEY_RELEASED, event -> {
			if (keyCombinRecord.match(event)) {
				Point point = MouseInfo.getPointerInfo().getLocation();
				x = (int) point.getX();
				y = (int) point.getY();
				lblAxis.setText(String.format("Coordinate: %d, %d", x, y));
			}
		});

		setResultConverter(btnType -> btnType == ButtonType.OK ? new int[] { x, y } : null);
	}

}
