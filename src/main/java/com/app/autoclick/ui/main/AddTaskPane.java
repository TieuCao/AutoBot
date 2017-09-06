package com.app.autoclick.ui.main;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import com.app.autoclick.action.Capture;
import com.app.autoclick.action.Click;
import com.app.autoclick.action.Click.ClickType;
import com.app.autoclick.action.Typing;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AddTaskPane extends Application implements Initializable, EventHandler<ActionEvent> {
	private FXMLLoader loader;
	private @FXML ComboBox<String> cmbType;
	private @FXML TextField txtWaitTime;
	private @FXML Button btnEditAxis;
	private @FXML Button btnSave;
	private @FXML StackPane pnAdditional;
	private @FXML Label lblSourceAxis;
	private GridPane pnAdtForTypeTask;
	private GridPane pnAdtForClickTask;
	private GridPane pnAdtForCaptureTask;
	private TextArea txtMessage;
	private Button btnEditDestAxis;
	private Label lblDestAxis;
	private Stage primaryStage;
	private Button btnChooseFile;
	private RadioButton radLeft, radRight, radCenter;
	private ToggleGroup grpRadMouse;
	private TextField txtSavePath;
	private int sAxisX = -1, sAxisY = -1;
	private int dAxisX = -1, dAxisY = -1;
	private CustomObservable observable;

	public AddTaskPane() {
		observable = new CustomObservable();
	}

	public void addObserver(Observer observer) {
		observable.addObserver(observer);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		loader = new FXMLLoader(getClass().getResource("/fxml/AddTaskPane.fxml"));
		loader.setController(this);

		Parent parent = loader.load();
		primaryStage.setScene(new Scene(parent));
		primaryStage.setTitle("Add Task");
		primaryStage.setResizable(false);
		primaryStage.show();

	}

	public void start(Stage primaryStage, Stage ownerStage) throws Exception {
		primaryStage.initModality(Modality.WINDOW_MODAL);
		primaryStage.initOwner(ownerStage);
		start(primaryStage);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		buildAdditionPaneForClickTask();
		buildAdditionPaneForTypingTask();
		buildAdditionPaneForCaptureTask();

		cmbType.setItems(FXCollections.observableArrayList("Click", "Typing", "Capture"));
		cmbType.valueProperty().addListener((observable, oldValue, newValue) -> handleCmbTypeChangeValue(newValue));
		cmbType.getSelectionModel().select(0);

		btnEditAxis.setOnAction(this);
		btnEditDestAxis.setOnAction(this);
		btnChooseFile.setOnAction(this);
		btnSave.setOnAction(this);
	}

	private GridPane buildAdditionPaneForClickTask() {
		if (pnAdtForClickTask == null) {
			grpRadMouse = new ToggleGroup();

			radLeft = new RadioButton("LEFT");
			radRight = new RadioButton("RIGHT");
			radCenter = new RadioButton("CENTER");

			radLeft.setToggleGroup(grpRadMouse);
			radRight.setToggleGroup(grpRadMouse);
			radCenter.setToggleGroup(grpRadMouse);

			radLeft.setSelected(true);

			ColumnConstraints col1 = new ColumnConstraints();
			ColumnConstraints col2 = new ColumnConstraints();

			col1.setHgrow(Priority.NEVER);
			col1.setPercentWidth(20);
			col1.setFillWidth(false);

			col2.setHgrow(Priority.ALWAYS);
			col2.setPercentWidth(80);

			pnAdtForClickTask = new GridPane();
			pnAdtForClickTask.getColumnConstraints().addAll(col1, col2);

			pnAdtForClickTask.add(new Label("Mouse Type"), 0, 0);
			pnAdtForClickTask.add(new FlowPane(5, 5, radLeft, radRight, radCenter), 1, 0);
		}
		return pnAdtForClickTask;
	}

	private GridPane buildAdditionPaneForTypingTask() {
		if (pnAdtForTypeTask == null) {
			ColumnConstraints col1 = new ColumnConstraints();
			ColumnConstraints col2 = new ColumnConstraints();

			col1.setHgrow(Priority.NEVER);
			col1.setPercentWidth(20);
			col1.setFillWidth(false);

			col2.setHgrow(Priority.ALWAYS);
			col2.setPercentWidth(80);

			pnAdtForTypeTask = new GridPane();
			pnAdtForTypeTask.getColumnConstraints().addAll(col1, col2);

			pnAdtForTypeTask.add(new Label("Message"), 0, 0);
			pnAdtForTypeTask.add(txtMessage = new TextArea(), 1, 0);
		}
		return pnAdtForTypeTask;
	}

	private GridPane buildAdditionPaneForCaptureTask() {
		if (pnAdtForCaptureTask == null) {
			txtSavePath = new TextField("N/A");
			txtSavePath.setEditable(false);
			btnChooseFile = new Button("Choose File");
			btnEditDestAxis = new Button("Edit");
			lblDestAxis = new Label("N/A");

			ColumnConstraints col1 = new ColumnConstraints();
			col1.setHgrow(Priority.NEVER);
			col1.setPercentWidth(20);
			col1.setFillWidth(false);

			ColumnConstraints col2 = new ColumnConstraints();
			col2.setPercentWidth(80);
			col2.setHgrow(Priority.ALWAYS);

			pnAdtForCaptureTask = new GridPane();
			pnAdtForCaptureTask.getColumnConstraints().addAll(col1, col2);
			pnAdtForCaptureTask.setVgap(10);
			pnAdtForCaptureTask.add(new Label("Dest Coordinate"), 0, 0);
			pnAdtForCaptureTask.add(new Label("Path Save"), 0, 1);
			pnAdtForCaptureTask.add(new HBox(5, btnChooseFile, txtSavePath), 1, 1);
			pnAdtForCaptureTask.add(new FlowPane(5, 5, btnEditDestAxis, lblDestAxis), 1, 0);
			
			HBox.setHgrow(txtSavePath, Priority.ALWAYS);
		}
		return pnAdtForCaptureTask;
	}

	@Override
	public void handle(ActionEvent event) {
		if (event.getSource() == btnEditAxis)
			handleBtnEditAxis();
		else if (event.getSource() == btnEditDestAxis)
			handleBtnDestAxis();
		else if (event.getSource() == btnChooseFile)
			handleBtnChooseFile();
		else if (event.getSource() == btnSave)
			handleBtnSave();
	}

	private void handleBtnChooseFile() {
		FileChooser chooser = new FileChooser();
		Arrays.asList("png|jpg|jpeg|gif".split("[|]"))
				.forEach(t -> chooser.getExtensionFilters().add(new ExtensionFilter(t, "*." + t)));
		File file = chooser.showSaveDialog(primaryStage);
		if (file != null)
			txtSavePath.setText(file.getAbsolutePath());
	}

	private void handleBtnDestAxis() {
		new AddCoordinateDialog().showAndWait().ifPresent(t -> {
			dAxisX = t[0];
			dAxisY = t[1];
			lblDestAxis.setText(String.format("%d, %d", t[0], t[1]));
		});
	}

	private void handleBtnEditAxis() {
		new AddCoordinateDialog().showAndWait().ifPresent(t -> {
			sAxisX = t[0];
			sAxisY = t[1];
			lblSourceAxis.setText(String.format("%d, %d", t[0], t[1]));
		});
	}

	private void handleCmbTypeChangeValue(String newValue) {
		switch (newValue) {
			case "Typing":
				pnAdditional.getChildren().setAll(pnAdtForTypeTask);
				break;
			case "Capture":
				pnAdditional.getChildren().setAll(pnAdtForCaptureTask);
				break;
			default:
				pnAdditional.getChildren().setAll(pnAdtForClickTask);
				break;
		}
		primaryStage.sizeToScene();
	}

	private void handleBtnSave() {
		int waitTime;
		try {
			waitTime = Integer.parseInt(txtWaitTime.getText());
		} catch (Exception e) {
			buildAlertDialog(AlertType.WARNING, "Warning", "Format of wait time is not valid").show();
			return;
		}

		if (sAxisX == -1 || sAxisY == -1) {
			buildAlertDialog(AlertType.WARNING, "Warning", "Please enter the coordinate").show();
			return;
		}

		switch (cmbType.getSelectionModel().getSelectedItem()) {

			case "Click":
				Toggle radChoose = grpRadMouse.getSelectedToggle();
				observable.setChanged();

				if (radChoose == radRight)
					observable.notifyObservers(new Click(ClickType.RIGHT, sAxisX, sAxisY, waitTime));
				else if (radChoose == radCenter)
					observable.notifyObservers(new Click(ClickType.CENTER, sAxisX, sAxisY, waitTime));
				else
					observable.notifyObservers(new Click(ClickType.LEFT, sAxisX, sAxisY, waitTime));
				break;

			case "Typing":
				String msg = txtMessage.getText();
				if (msg == null || msg.isEmpty()) {
					buildAlertDialog(AlertType.WARNING, "Warning", "Please enter message field").show();
					return;
				}
				observable.setChanged();
				observable.notifyObservers(new Typing(sAxisX, sAxisY, waitTime, txtMessage.getText()));
				break;

			case "Capture":
				if (dAxisX == -1 || dAxisY == -1) {
					buildAlertDialog(AlertType.WARNING, "Warning", "Please enter the destination coordinate").show();
					return;
				}
				if ("N/A".equals(txtSavePath.getText())) {
					buildAlertDialog(AlertType.WARNING, "Warning", "Please enter file path of image").show();
					return;
				}
				observable.setChanged();
				observable
						.notifyObservers(new Capture(sAxisX, sAxisY, dAxisX, dAxisY, waitTime, txtSavePath.getText()));
				break;

			default:
				break;
		}
	}

	/**
	 * build alert dialog
	 */
	private Alert buildAlertDialog(AlertType type, String title, String message) {
		Alert alert = new Alert(type);
		alert.setAlertType(type);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		return alert;
	}

	private class CustomObservable extends Observable {
		public void setChanged() {
			super.setChanged();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
