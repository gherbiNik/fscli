package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.frontend.controller.mapper.IFsStateMapperController;
import ch.supsi.fscli.frontend.model.IPreferenceModel;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.stage.FileChooser;

import java.io.File;

@Singleton
public class OpenView implements ShowView{

    private final IFsStateMapperController iFsStateMapperController;
    private final IPreferenceModel preferencesModel;

    @Inject
    public OpenView(IFsStateMapperController fsStateMapper, IPreferenceModel preferencesModel) {
        this.iFsStateMapperController = fsStateMapper;
        this.preferencesModel = preferencesModel;
    }

    @Override
    public void show() {
        FileChooser fileChooser = new FileChooser();

        String userPreferencesPath = preferencesModel.getUserPreferencesDirectoryPath().toString();
        File initialDirectory = new File(userPreferencesPath);
        if (initialDirectory.exists() && initialDirectory.isDirectory()) {
            fileChooser.setInitialDirectory(initialDirectory);
        }

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("JSON Files", "*.json"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            iFsStateMapperController.open(selectedFile.toString());

        } else {
            System.out.println("Nessun file selezionato");
        }

    }
}