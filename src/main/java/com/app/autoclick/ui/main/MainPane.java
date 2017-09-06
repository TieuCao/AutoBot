package com.app.autoclick.ui.main;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.app.autoclick.action.Action;
import com.app.autoclick.action.Capture;
import com.app.autoclick.action.Click;
import com.app.autoclick.action.Click.ClickType;
import com.app.autoclick.action.Typing;
import com.app.autoclick.service.CommandManager;
import com.app.autoclick.service.capture.CaptureCommand;
import com.app.autoclick.service.click.ClickCommand;
import com.app.autoclick.service.type.TypingCommand;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class MainPane extends Application implements Initializable, EventHandler<ActionEvent>, Observer {
	private Stage stage;
	private FXMLLoader loader;
	private @FXML TextField txtWaitTime;
	private @FXML TextField txtWaitTimeTask;
	private @FXML TextField txtInterval;
	private @FXML ToggleButton btnStart;
	private @FXML Button btnAdd;
	private @FXML Button btnDelete;
	private @FXML Button btnUpdate;
	private @FXML Button btnEditAxisTask;
	private @FXML Label lblTaskType;
	private @FXML Label lblSourceAxis;
	private @FXML TableView<Action> tblTasks;
	private @FXML StackPane pnAdditional;
	private @FXML TitledPane pnDetailTask;
	private GridPane pnAdtForTypeTask;
	private GridPane pnAdtForClickTask;
	private GridPane pnAdtForCaptureTask;
	private TextArea txtMessage;
	private TextField txtSavePath;
	private Label lblDestAxis;
	private Button btnEditDestAxisTask;
	private RadioButton radLeft, radRight, radCenter;
	private ToggleGroup grpRadMouse;
	private ObservableList<Action> lstActions;
	private CommandManager cmdManager;
	private ExecutorService pool;
	private Button btnChooseFile;

	@Override
	public void start(Stage primaryStage) throws Exception {
		pool = Executors.newFixedThreadPool(1);
		cmdManager = CommandManager.getInstance();
		lstActions = FXCollections.observableArrayList();

		stage = primaryStage;
		loader = new FXMLLoader(getClass().getResource("/fxml/MainPane.fxml"));
		loader.setController(this);
		Parent parent = loader.load();

		primaryStage.setScene(new Scene(parent));
		primaryStage.setTitle("AutoBot");
		primaryStage.setOnCloseRequest(event -> {
			pool.shutdownNow();
			cmdManager.stopAll();
		});

		final KeyCombination keyTurnOff = new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN);
		final KeyCombination keyTurnOn = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);

		primaryStage.addEventHandler(KeyEvent.KEY_RELEASED, event -> {
			if (keyTurnOff.match(event)) {
				cmdManager.stopAll();

				btnStart.setSelected(false);
				btnStart.setText("START");
				btnStart.getStyleClass().remove(btnStart.getStyleClass().size() - 1);
				btnStart.getStyleClass().add("btn-success");
			} else if (keyTurnOn.match(event) && !btnStart.isSelected()) {
				btnStart.setSelected(true);
				handleBtnStart();
			}
		});
		primaryStage.show();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		buildAdditionPaneForClickTask();
		buildAdditionPaneForTypingTask();
		buildAdditionPaneForCaptureTask();
		buildTaskTable();

		btnAdd.setOnAction(this);
		btnDelete.setOnAction(this);
		btnEditAxisTask.setOnAction(this);
		btnEditDestAxisTask.setOnAction(this);
		btnStart.setOnAction(this);
		btnUpdate.setOnAction(this);
		btnChooseFile.setOnAction(this);

		tblTasks.getSelectionModel().selectedItemProperty()
				.addListener((observable, oldValue, newValue) -> handleChangingRowInTable(newValue));

	}

	/**
	 * build task table to display all task
	 */
	@SuppressWarnings("unchecked")
	private void buildTaskTable() {
		tblTasks.setItems(lstActions);

		TableColumn<Action, String> colNo = new TableColumn<>("No");
		TableColumn<Action, String> colType = new TableColumn<>("Action Type");
		TableColumn<Action, String> colWaitTime = new TableColumn<>("Wait Time");
		TableColumn<Action, String> colInitalAxis = new TableColumn<>("Initial Coordinate");

		colNo.setCellFactory(param -> new TableCell<Action, String>() {
			@Override
			protected void updateItem(String item, boolean empty) {
				super.updateItem(item, empty);
				setGraphic(null);
				setText(empty ? null : getIndex() + 1 + "");
			}
		});
		colType.setCellValueFactory(param -> {
			Action action = param.getValue();
			if (action instanceof Click)
				return new SimpleStringProperty("Click");
			else if (action instanceof Typing)
				return new SimpleStringProperty("Typing");
			else if (action instanceof Capture)
				return new SimpleStringProperty("Capture");
			return new SimpleStringProperty("Unknow");
		});
		colWaitTime
				.setCellValueFactory(param -> new SimpleStringProperty(String.valueOf(param.getValue().getWaitTime())));
		colInitalAxis.setCellValueFactory(param -> new SimpleStringProperty(
				String.format("%d, %d", param.getValue().getAxisX(), param.getValue().getAxisY())));

		tblTasks.getColumns().addAll(colNo, colType, colWaitTime, colInitalAxis);
	}

	/**
	 * build typing task when typing is selected, a additional panel of typing task is shown next to common action pane
	 */
	private GridPane buildAdditionPaneForTypingTask() {
		if (pnAdtForTypeTask == null) {
			pnAdtForTypeTask = new GridPane();
			pnAdtForTypeTask.setVgap(10);
			pnAdtForTypeTask.add(new Label("Message"), 0, 0);
			pnAdtForTypeTask.add(txtMessage = new TextArea(), 0, 1);
		}
		return pnAdtForTypeTask;
	}

	/**
	 * build clicking task when clicking is selected, a additional panel of clicking task is shown next to common action
	 * pane
	 */
	private GridPane buildAdditionPaneForClickTask() {
		if (pnAdtForClickTask == null) {
			grpRadMouse = new ToggleGroup();

			radLeft = new RadioButton("LEFT");
			radRight = new RadioButton("RIGHT");
			radCenter = new RadioButton("CENTER");

			radLeft.setToggleGroup(grpRadMouse);
			radRight.setToggleGroup(grpRadMouse);
			radCenter.setToggleGroup(grpRadMouse);

			ColumnConstraints col1 = new ColumnConstraints();
			ColumnConstraints col2 = new ColumnConstraints();

			col1.setHgrow(Priority.NEVER);
			col1.setPercentWidth(30);
			col1.setFillWidth(false);

			col2.setHgrow(Priority.NEVER);
			col2.setPercentWidth(70);
			col2.setFillWidth(true);

			pnAdtForClickTask = new GridPane();
			pnAdtForClickTask.getColumnConstraints().addAll(col1, col2);

			pnAdtForClickTask.add(new Label("Mouse Type"), 0, 0);
			pnAdtForClickTask.add(new FlowPane(5, 5, radLeft, radRight, radCenter), 1, 0);
		}
		return pnAdtForClickTask;
	}

	/**
	 * build capture task when clicking is selected, a additional panel of capture task is shown next to common action
	 * pane
	 */
	private GridPane buildAdditionPaneForCaptureTask() {
		if (pnAdtForCaptureTask == null) {
			btnChooseFile = new Button("Edit");
			btnChooseFile.getStyleClass().addAll("btn", "btn-blue");
			btnEditDestAxisTask = new Button("Edit");
			btnEditDestAxisTask.getStyleClass().addAll("btn", "btn-blue");
			txtSavePath = new TextField();
			txtSavePath.setEditable(false);

			ColumnConstraints col1 = new ColumnConstraints();
			ColumnConstraints col2 = new ColumnConstraints();

			col1.setHgrow(Priority.NEVER);
			col1.setPercentWidth(30);
			col1.setFillWidth(false);

			col2.setHgrow(Priority.NEVER);
			col2.setPercentWidth(70);
			col2.setFillWidth(true);

			pnAdtForCaptureTask = new GridPane();
			pnAdtForCaptureTask.getColumnConstraints().addAll(col1, col2);

			pnAdtForCaptureTask.setHgap(5);
			pnAdtForCaptureTask.setVgap(10);
			pnAdtForCaptureTask.add(new Label("Dest Coordinate"), 0, 0);
			pnAdtForCaptureTask.add(new Label("Path Save"), 0, 1);
			pnAdtForCaptureTask.add(new HBox(5, btnChooseFile, txtSavePath), 1, 1);
			pnAdtForCaptureTask.add(new FlowPane(5, 5, btnEditDestAxisTask, lblDestAxis = new Label("N/A")), 1, 0);

			HBox.setHgrow(txtSavePath, Priority.ALWAYS);
		}
		return pnAdtForCaptureTask;
	}

	@Override
	public void handle(ActionEvent e) {
		Object src = e.getSource();

		if (src == btnAdd)
			handleBtnAdd();
		else if (src == btnDelete)
			handleBtnDelete();
		else if (src == btnUpdate)
			handleBtnUpdate();
		else if (src == btnStart)
			handleBtnStart();
		else if (src == btnEditAxisTask)
			handleBtnEditAxisTask();
		else if (src == btnEditDestAxisTask)
			handleBtnEditDestAxisTask();
		else if (src == btnChooseFile)
			handleBtnEditSavePath();
	}

	/**
	 * handle when click a row in task table
	 */
	private void handleChangingRowInTable(Action newValue) {
		if (newValue != null) {
			pnDetailTask.setExpanded(true);
			pnDetailTask.setDisable(false);

			txtWaitTimeTask.setText(String.valueOf(newValue.getWaitTime()));
			lblSourceAxis.setText(String.format("%d, %d", newValue.getAxisX(), newValue.getAxisY()));

			// depend on the case, clicking pane, typing pane or capture pane will be displayed in there
			if (newValue instanceof Click) {
				lblTaskType.setText("Click");
				ClickType type = ((Click) newValue).getType();

				grpRadMouse.selectToggle(
						type == ClickType.LEFT ? radLeft : type == ClickType.RIGHT ? radRight : radCenter);
				pnAdditional.getChildren().setAll(pnAdtForClickTask);
			}

			else if (newValue instanceof Typing) {
				lblTaskType.setText("Typing");
				txtMessage.setText(((Typing) newValue).getMessage());
				pnAdditional.getChildren().setAll(pnAdtForTypeTask);
			}

			else if (newValue instanceof Capture) {
				lblTaskType.setText("Capture");
				pnAdditional.getChildren().setAll(pnAdtForCaptureTask);
				lblDestAxis.setText(
						String.format("%d, %d", ((Capture) newValue).getDestX(), ((Capture) newValue).getDestY()));
				txtSavePath.setText(((Capture) newValue).getSavePath());
			}
		} else {
			pnDetailTask.setExpanded(false);
			pnDetailTask.setDisable(true);
		}
	}

	/**
	 * handle when add button is clicked
	 */
	private void handleBtnAdd() {
		try {
			AddTaskPane addTask = new AddTaskPane();
			addTask.addObserver(this);
			addTask.start(new Stage(), stage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * handle when pressed edit coordinate button in detail task pane
	 */
	private void handleBtnEditAxisTask() {
		new AddCoordinateDialog().showAndWait().ifPresent(t -> {
			lblSourceAxis.setText(String.format("%s, %s", t[0], t[1]));
		});
	}

	/**
	 * handle when pressed edit destination coordinate button in detail task pane
	 */
	private void handleBtnEditDestAxisTask() {
		new AddCoordinateDialog().showAndWait().ifPresent(t -> {
			lblDestAxis.setText(String.format("%s, %s", t[0], t[1]));
		});
	}

	/**
	 * handle when pressed file chooser button
	 */
	private void handleBtnEditSavePath() {
		FileChooser chooser = new FileChooser();
		Arrays.asList("png|jpg|jpeg|gif".split("[|]"))
				.forEach(t -> chooser.getExtensionFilters().add(new ExtensionFilter(t, "*." + t)));
		File file = chooser.showSaveDialog(stage);
		if (file != null)
			txtSavePath.setText(file.getAbsolutePath());
	}

	/**
	 * handle when pressed start button
	 */
	private void handleBtnStart() {
		if (btnStart.isSelected()) {
			btnStart.setSelected(false);
			if (lstActions.isEmpty()) {
				buildAlertDialog(AlertType.WARNING, "Warning", "No task to execute").show();
				return;
			}

			int waitTime, interval;
			try {
				waitTime = Integer.parseInt(txtWaitTime.getText());
				if (waitTime < 0)
					throw new Exception();
			} catch (Exception e) {
				buildAlertDialog(AlertType.WARNING, "Warning", "Format of wait time field is invalid").show();
				btnStart.setSelected(false);
				return;
			}
			try {
				interval = Integer.parseInt(txtInterval.getText());
				if (interval < 0)
					throw new Exception();
			} catch (Exception e) {
				buildAlertDialog(AlertType.WARNING, "Warning", "Format of interval field is invalid").show();
				btnStart.setSelected(false);
				return;
			}

			btnStart.setSelected(true);
			btnStart.setText("STOP");
			btnStart.getStyleClass().remove(btnStart.getStyleClass().size() - 1);
			btnStart.getStyleClass().add("btn-danger");

			cmdManager.clearAll();
			cmdManager.addCommand(lstActions.stream().map(t -> {
				try {
					if (t instanceof Typing)
						return new TypingCommand((Typing) t);

					if (t instanceof Capture)
						return new CaptureCommand((Capture) t);

					return new ClickCommand((Click) t);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}).collect(Collectors.toList()));

			pool.submit(() -> {
				try {
					cmdManager.executeAll(waitTime, interval);
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		} else {
			btnStart.setText("START");
			cmdManager.stopAll();
		}
	}

	/**
	 * handle when pressed update button to update new value to action
	 */
	private void handleBtnUpdate() {
		int index = tblTasks.getSelectionModel().getSelectedIndex();
		if (index > -1) { // At least one row is exist in task table
			int waitTimeTask;
			try {
				waitTimeTask = Integer.parseInt(txtWaitTimeTask.getText());
				if (waitTimeTask < 0)
					throw new Exception("Wait time is negative");
			} catch (Exception e) {
				buildAlertDialog(AlertType.WARNING, "Warning", "Wait time muse be positive number").show();
				return;
			}

			int[] sAxis = convertToAxis(lblSourceAxis.getText());
			Action item = tblTasks.getSelectionModel().getSelectedItem();

			item.setWaitTime(Integer.parseInt(txtWaitTimeTask.getText()));
			item.setAxisX(sAxis[0]);
			item.setAxisY(sAxis[1]);

			if (item instanceof Click) {
				Click click = (Click) item;
				if (radLeft.isSelected())
					click.setType(ClickType.LEFT);
				else if (radRight.isSelected())
					click.setType(ClickType.RIGHT);
				else
					click.setType(ClickType.CENTER);
			}

			else if (item instanceof Typing) {
				((Typing) item).setMessage(txtMessage.getText());
			}

			else if (item instanceof Capture) {
				int[] dAxis = convertToAxis(lblDestAxis.getText());
				Capture cap = (Capture) item;
				cap.setDestX(dAxis[0]);
				cap.setDestY(dAxis[1]);
				cap.setPathSave(txtSavePath.getText());
			}

			lstActions.set(index, item);
		}

	}

	/**
	 * handle when pressed delete button
	 */
	private void handleBtnDelete() {
		int index = tblTasks.getSelectionModel().getSelectedIndex();
		if (index != -1) {
			lstActions.remove(index);
		}
	}

	/**
	 * handle when having return data from Add Task Pane class
	 */
	@Override
	public void update(Observable o, Object arg) {
		if (arg instanceof Action)
			lstActions.add((Action) arg);
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

	/**
	 * convert coordinate string to axis array with format x, y. Example coordinate 132, 132
	 */
	private int[] convertToAxis(String coordinate) {
		String[] tokens = coordinate.split(",\\s*");
		return new int[] { Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]) };
	}

	public static void main(String[] args) {
		launch(args);
	}
}
