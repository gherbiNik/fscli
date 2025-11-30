package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.frontend.controller.IPreferenceController;
import ch.supsi.fscli.frontend.util.I18nManager;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.List;

public class PreferenceView implements ShowView {
    private IPreferenceController controller;
    private I18nManager i18nManager;

    // --- UI Components as Fields ---
    private ComboBox<String> languageComboBox;
    private Spinner<Integer> columnsSpinner;
    private Spinner<Integer> outputLinesSpinner;
    private Spinner<Integer> logLinesSpinner;
    private ComboBox<String> commandLineFontComboBox;
    private ComboBox<String> outputAreaFontComboBox;
    private ComboBox<String> logAreaFontComboBox;
    private Button saveButton;
    private Button cancelButton;

    // --- Labels also declared as fields to allow translation ---
    private Label languageLabel;
    private Label columnsLabel;
    private Label outputLinesLabel;
    private Label logLinesLabel;
    private Label commandLineFontLabel;
    private Label outputAreaFontLabel;
    private Label logAreaFontLabel;

    private final Stage stage;


    private void initialize() {


    }

    public PreferenceView(IPreferenceController controller, I18nManager i18nManager) {
        this.controller = controller;
        this.i18nManager = i18nManager;
        this.stage = new Stage();
        initializeUI();
        applicateTranslation();
        loadCurrentPreferences();
    }

    // This method now handles ALL text content
    private void applicateTranslation() {
        // Assume you have these keys in your resource bundle (e.g., messages_en_US.properties)
        stage.setTitle(i18nManager.getString("preference.title"));
        languageLabel.setText(i18nManager.getString("preference.label.language"));
        columnsLabel.setText(i18nManager.getString("preference.label.columns"));
        outputLinesLabel.setText(i18nManager.getString("preference.label.outputLines"));
        logLinesLabel.setText(i18nManager.getString("preference.label.logLines"));
        commandLineFontLabel.setText(i18nManager.getString("preference.label.commandLineFont"));
        outputAreaFontLabel.setText(i18nManager.getString("preference.label.outputAreaFont"));
        logAreaFontLabel.setText(i18nManager.getString("preference.label.logAreaFont"));
        saveButton.setText(i18nManager.getString("button.save"));
        cancelButton.setText(i18nManager.getString("button.cancel"));
    }

    private void initializeUI() {
        VBox mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(15));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        List<String> availableFonts = Font.getFamilies();

        // --- Component Creation (without hardcoded text) ---
        languageLabel = new Label();
        languageComboBox = new ComboBox<>(FXCollections.observableArrayList("it-IT", "en-US"));
        grid.add(languageLabel, 0, 0);
        grid.add(languageComboBox, 1, 0);

        columnsLabel = new Label();
        columnsSpinner = new Spinner<>(40, 100, 80);
        grid.add(columnsLabel, 0, 1);
        grid.add(columnsSpinner, 1, 1);

        outputLinesLabel = new Label();
        outputLinesSpinner = new Spinner<>(3, 50, 10);
        grid.add(outputLinesLabel, 0, 2);
        grid.add(outputLinesSpinner, 1, 2);

        logLinesLabel = new Label();
        logLinesSpinner = new Spinner<>(3, 50, 5);
        grid.add(logLinesLabel, 0, 3);
        grid.add(logLinesSpinner, 1, 3);

        commandLineFontLabel = new Label();
        commandLineFontComboBox = new ComboBox<>(FXCollections.observableArrayList(availableFonts));
        grid.add(commandLineFontLabel, 0, 4);
        grid.add(commandLineFontComboBox, 1, 4);

        outputAreaFontLabel = new Label();
        outputAreaFontComboBox = new ComboBox<>(FXCollections.observableArrayList(availableFonts));
        grid.add(outputAreaFontLabel, 0, 5);
        grid.add(outputAreaFontComboBox, 1, 5);

        logAreaFontLabel = new Label();
        logAreaFontComboBox = new ComboBox<>(FXCollections.observableArrayList(availableFonts));
        grid.add(logAreaFontLabel, 0, 6);
        grid.add(logAreaFontComboBox, 1, 6);

        saveButton = new Button();
        saveButton.setOnAction(e -> {
            savePreferences();
            closeView();
        });

        cancelButton = new Button();
        cancelButton.setOnAction(e -> closeView());

        HBox buttonBox = new HBox(10, saveButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        mainLayout.getChildren().addAll(grid, buttonBox);
        VBox.setVgrow(grid, Priority.ALWAYS);

        Scene scene = new Scene(mainLayout, 500, 400);
        stage.setScene(scene);
    }

    private void savePreferences() {
        controller.savePreferences(languageComboBox.getValue(), columnsSpinner.getValue().toString(), outputLinesSpinner.getValue().toString(),
                logLinesSpinner.getValue().toString(), commandLineFontComboBox.getValue(),  outputAreaFontComboBox.getValue(), logAreaFontComboBox.getValue());
    }

    private void loadCurrentPreferences() {
        languageComboBox.setValue(controller.getPreferences("language-tag"));
        columnsSpinner.getValueFactory().setValue(Integer.parseInt(controller.getPreferences("column")));
        outputLinesSpinner.getValueFactory().setValue(Integer.parseInt(controller.getPreferences("output-area-row")));
        logLinesSpinner.getValueFactory().setValue(Integer.parseInt(controller.getPreferences("log-area-row")));
        commandLineFontComboBox.setValue(controller.getPreferences("font-command-line"));
        outputAreaFontComboBox.setValue(controller.getPreferences("font-output-area"));
        logAreaFontComboBox.setValue(controller.getPreferences("font-log-area"));
    }

    @Override
    public void show() {
        stage.show();
    }

    public void closeView() {
        stage.close();
    }
}