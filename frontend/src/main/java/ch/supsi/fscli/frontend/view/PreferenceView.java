package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.frontend.controller.IPreferenceController;
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

    private static PreferenceView instance;

    private IPreferenceController controller;

    // Componenti UI che il Controller dovrà conoscere
    private ComboBox<String> languageComboBox;
    private Spinner<Integer> columnsSpinner;
    private Spinner<Integer> outputLinesSpinner;
    private Spinner<Integer> logLinesSpinner;
    private ComboBox<String> commandLineFontComboBox;
    private ComboBox<String> outputAreaFontComboBox;
    private ComboBox<String> logAreaFontComboBox;
    private Button saveButton;
    private Button cancelButton;

    private Stage stage = new Stage();

    public static PreferenceView getInstance(IPreferenceController controller) {
        if (instance == null) {
            instance = new PreferenceView();
            instance.initialize(controller);
        }
        return instance;
    }

    private void initialize(IPreferenceController controller) {
        this.controller = controller;
        loadCurrentPreferences(languageComboBox,columnsSpinner,outputLinesSpinner, logLinesSpinner, commandLineFontComboBox,outputAreaFontComboBox, logAreaFontComboBox);

    }

    public PreferenceView() {
        initializeUI();

    }



    private void initializeUI() {
        stage.setTitle("Preferenze");
        VBox mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(15));

        // Layout a griglia per le impostazioni
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        // Lista di font disponibili sul sistema
        List<String> availableFonts = Font.getFamilies();

        // 1. Lingua
        languageComboBox = new ComboBox<>(FXCollections.observableArrayList("Italiano", "English"));
        grid.add(new Label("Lingua:"), 0, 0);
        grid.add(languageComboBox, 1, 0);


        // 2. Colonne Command Line

        columnsSpinner = new Spinner<>(40, 100, 80); // min, max, initial
        grid.add(new Label("Numero di colonne (caratteri):"), 0, 1);
        grid.add(columnsSpinner, 1, 1);

        // 3. Righe Output Area
        outputLinesSpinner = new Spinner<>(3, 50, 10);
        grid.add(new Label("Righe visibili area output (>= 3):"), 0, 2);
        grid.add(outputLinesSpinner, 1, 2);

        // 4. Righe Log Area
        logLinesSpinner = new Spinner<>(3, 50, 5);
        grid.add(new Label("Righe visibili area log (>= 3):"), 0, 3);
        grid.add(logLinesSpinner, 1, 3);

        // 5. Font Command Line
        commandLineFontComboBox = new ComboBox<>(FXCollections.observableArrayList(availableFonts));
        grid.add(new Label("Font Command Line:"), 0, 4);
        grid.add(commandLineFontComboBox, 1, 4);

        // 6. Font Output Area
        outputAreaFontComboBox = new ComboBox<>(FXCollections.observableArrayList(availableFonts));
        grid.add(new Label("Font Area Output:"), 0, 5);
        grid.add(outputAreaFontComboBox, 1, 5);

        // 7. Font Log Area
        logAreaFontComboBox = new ComboBox<>(FXCollections.observableArrayList(availableFonts));
        grid.add(new Label("Font Area Log:"), 0, 6);
        grid.add(logAreaFontComboBox, 1, 6);

        // Sezione Pulsanti
        saveButton = new Button("Salva");
        cancelButton = new Button("Annulla");
        HBox buttonBox = new HBox(10, saveButton, cancelButton);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        mainLayout.getChildren().addAll(grid, buttonBox);
        VBox.setVgrow(grid, Priority.ALWAYS);

        Scene scene = new Scene(mainLayout, 500, 400);
        stage.setScene(scene);

    }

    private void loadCurrentPreferences(ComboBox<String> languageComboBox, Spinner<Integer> columnsSpinner, Spinner<Integer> outputLinesSpinner, Spinner<Integer> logLinesSpinner, ComboBox<String> commandLineFontComboBox, ComboBox<String> outputAreaFontComboBox, ComboBox<String> logAreaFontComboBox) {
        languageComboBox.setValue(controller.getPreferences("language-tag"));
        columnsSpinner.getValueFactory().setValue(Integer.parseInt(controller.getPreferences("column")));
        outputLinesSpinner.getValueFactory().setValue(Integer.parseInt(controller.getPreferences("output-area-row")));
        logLinesSpinner.getValueFactory().setValue(Integer.parseInt(controller.getPreferences("log-area-row")));
        commandLineFontComboBox.setValue(controller.getPreferences("font-command-line"));
        outputAreaFontComboBox.setValue(controller.getPreferences("font-output-area"));
        logAreaFontComboBox.setValue(controller.getPreferences("font-log-area"));
    }

    @Override
    public void showView() {
        stage.show();
    }

    public void closeView() {
        stage.close();
    }


}