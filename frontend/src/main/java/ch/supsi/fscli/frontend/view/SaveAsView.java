package ch.supsi.fscli.frontend.view;

import ch.supsi.fscli.frontend.controller.mapper.IFsStateMapperController;
import ch.supsi.fscli.frontend.model.IPreferenceModel;
import javafx.stage.FileChooser;

import java.io.File;

public class SaveAsView implements ShowView{

    public static SaveAsView instance;
    private IFsStateMapperController iFsStateMapperController;
    private IPreferenceModel preferencesModel;

    private SaveAsView() {}
    public static SaveAsView getInstance(IFsStateMapperController iFsStateMapperController, IPreferenceModel preferencesModel) {
        if (instance == null) {
            instance = new SaveAsView();
            instance.initialize(iFsStateMapperController, preferencesModel);
        }
        return instance;
    }

    private void initialize(IFsStateMapperController iFsStateMapperController, IPreferenceModel preferencesModel){
        this.iFsStateMapperController = iFsStateMapperController;
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


        File selectedfile = fileChooser.showSaveDialog(null);

        if (selectedfile != null) {
            iFsStateMapperController.saveAs(selectedfile);
        } else {
            System.out.println("VA STAMPATO ANCHE NEL LOG??\nOperazione SaveAs annullata");
        }
    }
}

