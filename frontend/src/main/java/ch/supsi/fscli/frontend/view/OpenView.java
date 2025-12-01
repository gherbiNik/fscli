package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.frontend.controller.mapper.IFsStateMapperController;
import ch.supsi.fscli.frontend.model.IPreferenceModel;
import javafx.stage.FileChooser;

import java.io.File;

public class OpenView implements ShowView{
    private static OpenView myself;
    private IFsStateMapperController iFsStateMapperController;
    private IPreferenceModel preferencesModel;

    private OpenView() {}

    public static OpenView getInstance(IFsStateMapperController fsStateMapper, IPreferenceModel preferencesModel) {
        if (myself == null) {
            myself = new OpenView();
            myself.initialize(fsStateMapper, preferencesModel);
        }
        return myself;
    }
    private void initialize(IFsStateMapperController fsStateMapper, IPreferenceModel preferencesModel){
        this.iFsStateMapperController = fsStateMapper;
        this.preferencesModel = preferencesModel;
    }


    @Override
    public void showView() {
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
            System.out.println("DOVE???\nNessun file selezionato");
        }

    }
}
